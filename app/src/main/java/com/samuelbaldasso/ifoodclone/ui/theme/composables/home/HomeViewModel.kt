package com.samuelbaldasso.ifoodclone.ui.theme.composables.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samuelbaldasso.ifoodclone.core.domain.model.AppResult
import com.samuelbaldasso.ifoodclone.core.domain.model.Cart
import com.samuelbaldasso.ifoodclone.core.domain.model.Restaurant
import com.samuelbaldasso.ifoodclone.core.domain.repository.CartRepository
import com.samuelbaldasso.ifoodclone.core.domain.repository.RestaurantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val address: String = "R. dos Desenvolvedores, 1234 • São Paulo, SP",
    val searchQuery: String = "",
    val selectedCategory: String = "Todos",
    val categories: List<String> = listOf("Todos", "Lanches", "Japonesa", "Pizza", "Carnes", "Doces & Bolos"),
    val restaurants: List<Restaurant> = emptyList(),
    val filteredRestaurants: List<Restaurant> = emptyList(),
    val cart: Cart = Cart(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed interface HomeUiIntent {
    data class SearchQueryChange(val query: String) : HomeUiIntent
    data class SelectCategory(val category: String) : HomeUiIntent
    data object Refresh : HomeUiIntent
    data class RestaurantClick(val restaurantId: String) : HomeUiIntent
}

sealed interface HomeUiEffect {
    data class NavigateToRestaurant(val restaurantId: String) : HomeUiEffect
    data object NavigateToCart : HomeUiEffect
    data class ShowMessage(val message: String) : HomeUiEffect
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<HomeUiEffect>(Channel.BUFFERED)
    val uiEffect = _uiEffect.receiveAsFlow()

    init {
        loadRestaurants()
        observeCart()
    }

    private fun observeCart() {
        viewModelScope.launch {
            cartRepository.getCart().collect { cart ->
                _uiState.update { it.copy(cart = cart) }
            }
        }
    }

    fun handleIntent(intent: HomeUiIntent) {
        when (intent) {
            is HomeUiIntent.SearchQueryChange -> onSearchQueryChange(intent.query)
            is HomeUiIntent.SelectCategory -> onCategorySelected(intent.category)
            is HomeUiIntent.Refresh -> loadRestaurants()
            is HomeUiIntent.RestaurantClick -> {
                viewModelScope.launch {
                    _uiEffect.send(HomeUiEffect.NavigateToRestaurant(intent.restaurantId))
                }
            }
        }
    }

    private fun loadRestaurants() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = restaurantRepository.getRestaurants()) {
                is AppResult.Success -> {
                    val list = result.data
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            restaurants = list,
                            filteredRestaurants = applyFilters(list, state.selectedCategory, state.searchQuery)
                        )
                    }
                }
                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Não foi possível carregar as lojas. Toque para tentar novamente."
                        )
                    }
                }
            }
        }
    }

    private fun onSearchQueryChange(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredRestaurants = applyFilters(state.restaurants, state.selectedCategory, query)
            )
        }
    }

    private fun onCategorySelected(category: String) {
        _uiState.update { state ->
            state.copy(
                selectedCategory = category,
                filteredRestaurants = applyFilters(state.restaurants, category, state.searchQuery)
            )
        }
    }

    private fun applyFilters(
        restaurants: List<Restaurant>,
        category: String,
        query: String
    ): List<Restaurant> {
        return restaurants.filter { restaurant ->
            val matchesCategory = category.equals("Todos", ignoreCase = true) ||
                    restaurant.category.contains(category, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    restaurant.name.contains(query, ignoreCase = true) ||
                    restaurant.category.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }
}