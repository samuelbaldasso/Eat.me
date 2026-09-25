package com.samuelbaldasso.ifoodclone.data.repository

import com.samuelbaldasso.ifoodclone.domain.restaurant.Restaurant

interface RestaurantRepository {
    suspend fun getRestaurants(): List<Restaurant>
}