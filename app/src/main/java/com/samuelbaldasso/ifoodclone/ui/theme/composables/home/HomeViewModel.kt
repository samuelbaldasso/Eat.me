package com.samuelbaldasso.ifoodclone.ui.theme.composables.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samuelbaldasso.ifoodclone.data.repository.RestaurantRepository
import com.samuelbaldasso.ifoodclone.domain.restaurant.Restaurant
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
    val selectedCategory: String = "Todos",
    val categories: List<String> = listOf("Todos", "Lanches", "Japonesa", "Pizza", "Carnes", "Doces & Bolos"),
    val restaurants: List<Restaurant> = emptyList(),
    val filteredRestaurants: List<Restaurant> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed interface HomeUiIntent {
    data class SelectCategory(val category: String) : HomeUiIntent
    data object Refresh : HomeUiIntent
    data class RestaurantClick(val restaurantId: String) : HomeUiIntent
}

sealed interface HomeUiEffect {
    data class NavigateToRestaurant(val restaurantId: String) : HomeUiEffect
    data class ShowMessage(val message: String) : HomeUiEffect
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<HomeUiEffect>(Channel.BUFFERED)
    val uiEffect = _uiEffect.receiveAsFlow()

    init {
        loadRestaurants()
    }

    fun handleIntent(intent: HomeUiIntent) {
        when (intent) {
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
            try {
                val list = restaurantRepository.getRestaurants()
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        restaurants = list,
                        filteredRestaurants = applyCategoryFilter(list, state.selectedCategory)
                    )
                }
            } catch (t: Throwable) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Não foi possível carregar as lojas. Toque para tentar novamente."
                    )
                }
            }
        }
    }

    private fun onCategorySelected(category: String) {
        _uiState.update { state ->
            state.copy(
                selectedCategory = category,
                filteredRestaurants = applyCategoryFilter(state.restaurants, category)
            )
        }
    }

    private fun applyCategoryFilter(
        restaurants: List<Restaurant>,
        category: String
    ): List<Restaurant> {
        return if (category.equals("Todos", ignoreCase = true)) {
            restaurants
        } else {
            restaurants.filter { it.category.contains(category, ignoreCase = true) }
        }
    }
}