package com.samuelbaldasso.ifoodclone.ui.theme.composables.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samuelbaldasso.ifoodclone.data.repository.RestaurantRepository
import com.samuelbaldasso.ifoodclone.data.repository.RestaurantRepositoryImpl
import com.samuelbaldasso.ifoodclone.domain.restaurant.Restaurant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository
) : ViewModel() {
    private val _restaurants = MutableStateFlow<List<Restaurant>>(emptyList())
    val restaurants = _restaurants.asStateFlow()

    init {
        fetchRestaurants()
    }

    private fun fetchRestaurants() {
        viewModelScope.launch {
            _restaurants.value = restaurantRepository.getRestaurants()
        }
    }
}