package com.samuelbaldasso.ifoodclone.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samuelbaldasso.ifoodclone.core.domain.model.AppResult
import com.samuelbaldasso.ifoodclone.core.domain.model.Money
import com.samuelbaldasso.ifoodclone.core.domain.model.Restaurant
import com.samuelbaldasso.ifoodclone.core.domain.repository.RestaurantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val selectedCategory: String? = null,
    val onlyFreeDelivery: Boolean = false,
    val onlyTopRated: Boolean = false,
    val allRestaurants: List<Restaurant> = emptyList(),
    val searchResults: List<Restaurant> = emptyList(),
    val popularTags: List<String> = listOf("Hambúrguer", "Pizza", "Japonesa", "Doces & Bolos", "Carnes", "Açaí"),
    val recentSearches: List<String> = listOf("Burger King", "Sushi", "Açaí", "Pizza"),
    val isLoading: Boolean = false
) {
    val isSearching: Boolean get() = query.isNotBlank() || selectedCategory != null || onlyFreeDelivery || onlyTopRated
}

sealed interface SearchUiIntent {
    data class UpdateQuery(val query: String) : SearchUiIntent
    data class SelectTag(val tag: String) : SearchUiIntent
    data object ToggleFreeDelivery : SearchUiIntent
    data object ToggleTopRated : SearchUiIntent
    data object ClearQuery : SearchUiIntent
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState(isLoading = true))
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        loadRestaurants()
    }

    private fun loadRestaurants() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = restaurantRepository.getRestaurants()) {
                is AppResult.Success -> {
                    val list = result.data
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            allRestaurants = list,
                            searchResults = filterRestaurants(list, state.query, state.selectedCategory, state.onlyFreeDelivery, state.onlyTopRated)
                        )
                    }
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun handleIntent(intent: SearchUiIntent) {
        when (intent) {
            is SearchUiIntent.UpdateQuery -> {
                _uiState.update { state ->
                    val newQuery = intent.query
                    state.copy(
                        query = newQuery,
                        searchResults = filterRestaurants(state.allRestaurants, newQuery, state.selectedCategory, state.onlyFreeDelivery, state.onlyTopRated)
                    )
                }
            }
            is SearchUiIntent.SelectTag -> {
                _uiState.update { state ->
                    val newTag = if (state.selectedCategory == intent.tag) null else intent.tag
                    state.copy(
                        selectedCategory = newTag,
                        searchResults = filterRestaurants(state.allRestaurants, state.query, newTag, state.onlyFreeDelivery, state.onlyTopRated)
                    )
                }
            }
            is SearchUiIntent.ToggleFreeDelivery -> {
                _uiState.update { state ->
                    val toggled = !state.onlyFreeDelivery
                    state.copy(
                        onlyFreeDelivery = toggled,
                        searchResults = filterRestaurants(state.allRestaurants, state.query, state.selectedCategory, toggled, state.onlyTopRated)
                    )
                }
            }
            is SearchUiIntent.ToggleTopRated -> {
                _uiState.update { state ->
                    val toggled = !state.onlyTopRated
                    state.copy(
                        onlyTopRated = toggled,
                        searchResults = filterRestaurants(state.allRestaurants, state.query, state.selectedCategory, state.onlyFreeDelivery, toggled)
                    )
                }
            }
            is SearchUiIntent.ClearQuery -> {
                _uiState.update { state ->
                    state.copy(
                        query = "",
                        selectedCategory = null,
                        onlyFreeDelivery = false,
                        onlyTopRated = false,
                        searchResults = emptyList()
                    )
                }
            }
        }
    }

    private fun filterRestaurants(
        list: List<Restaurant>,
        query: String,
        category: String?,
        freeDelivery: Boolean,
        topRated: Boolean
    ): List<Restaurant> {
        return list.filter { r ->
            val matchesQuery = query.isBlank() || r.name.contains(query, ignoreCase = true) || r.category.contains(query, ignoreCase = true)
            val matchesCat = category == null || r.category.contains(category, ignoreCase = true)
            val matchesFree = !freeDelivery || r.deliveryFee == Money.ZERO
            val matchesRating = !topRated || r.rating >= 4.7
            matchesQuery && matchesCat && matchesFree && matchesRating
        }
    }
}
