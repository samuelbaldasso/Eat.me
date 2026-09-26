package com.samuelbaldasso.ifoodclone.core.domain.repository

import com.samuelbaldasso.ifoodclone.core.domain.model.AppError
import com.samuelbaldasso.ifoodclone.core.domain.model.AppResult
import com.samuelbaldasso.ifoodclone.core.domain.model.Cart
import com.samuelbaldasso.ifoodclone.core.domain.model.CartError
import com.samuelbaldasso.ifoodclone.core.domain.model.Dish
import com.samuelbaldasso.ifoodclone.core.domain.model.Option
import com.samuelbaldasso.ifoodclone.core.domain.model.Restaurant
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getCart(): Flow<Cart>

    suspend fun addToCart(
        restaurant: Restaurant,
        dish: Dish,
        selectedOptions: List<Option>,
        quantity: Int,
        forceClearIfDifferentRestaurant: Boolean = false
    ): AppResult<Unit, CartError>

    suspend fun updateItemQuantity(cartItemId: String, newQuantity: Int): AppResult<Unit, AppError>

    suspend fun removeItem(cartItemId: String): AppResult<Unit, AppError>

    suspend fun clearCart(): AppResult<Unit, AppError>
}
