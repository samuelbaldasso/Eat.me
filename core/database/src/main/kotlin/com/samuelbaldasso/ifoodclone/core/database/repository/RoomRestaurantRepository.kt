package com.samuelbaldasso.ifoodclone.core.database.repository

import com.samuelbaldasso.ifoodclone.core.database.dao.RestaurantDao
import com.samuelbaldasso.ifoodclone.core.database.mapper.toDomain
import com.samuelbaldasso.ifoodclone.core.domain.model.AppError
import com.samuelbaldasso.ifoodclone.core.domain.model.AppResult
import com.samuelbaldasso.ifoodclone.core.domain.model.Restaurant
import com.samuelbaldasso.ifoodclone.core.domain.model.RestaurantDetails
import com.samuelbaldasso.ifoodclone.core.domain.repository.RestaurantRepository
import javax.inject.Inject

class RoomRestaurantRepository @Inject constructor(
    private val restaurantDao: RestaurantDao
) : RestaurantRepository {

    override suspend fun getRestaurants(): AppResult<List<Restaurant>, AppError> {
        return try {
            val entities = restaurantDao.getAllRestaurants()
            AppResult.Success(entities.map { it.toDomain() })
        } catch (t: Throwable) {
            AppResult.Error(AppError.Unknown(throwable = t, message = t.localizedMessage))
        }
    }

    override suspend fun getRestaurantDetails(id: String): AppResult<RestaurantDetails, AppError> {
        return try {
            val relation = restaurantDao.getRestaurantWithDetails(id)
            if (relation != null) {
                AppResult.Success(relation.toDomain())
            } else {
                AppResult.Error(AppError.NotFound("Restaurante com ID '$id' não foi encontrado."))
            }
        } catch (t: Throwable) {
            AppResult.Error(AppError.Unknown(throwable = t, message = t.localizedMessage))
        }
    }
}
