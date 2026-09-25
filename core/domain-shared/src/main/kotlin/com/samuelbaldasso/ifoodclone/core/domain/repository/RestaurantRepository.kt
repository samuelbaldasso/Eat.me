package com.samuelbaldasso.ifoodclone.core.domain.repository

import com.samuelbaldasso.ifoodclone.core.domain.model.AppError
import com.samuelbaldasso.ifoodclone.core.domain.model.AppResult
import com.samuelbaldasso.ifoodclone.core.domain.model.Restaurant
import com.samuelbaldasso.ifoodclone.core.domain.model.RestaurantDetails

interface RestaurantRepository {
    suspend fun getRestaurants(): AppResult<List<Restaurant>, AppError>
    suspend fun getRestaurantDetails(id: String): AppResult<RestaurantDetails, AppError>
}
