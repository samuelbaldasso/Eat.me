package com.samuelbaldasso.ifoodclone.core.database.repository

import com.samuelbaldasso.ifoodclone.core.database.dao.CartDao
import com.samuelbaldasso.ifoodclone.core.database.dao.RestaurantDao
import com.samuelbaldasso.ifoodclone.core.database.entity.CartItemEntity
import com.samuelbaldasso.ifoodclone.core.database.entity.CartItemOptionEntity
import com.samuelbaldasso.ifoodclone.core.database.mapper.toDomain
import com.samuelbaldasso.ifoodclone.core.domain.model.AppError
import com.samuelbaldasso.ifoodclone.core.domain.model.AppResult
import com.samuelbaldasso.ifoodclone.core.domain.model.Cart
import com.samuelbaldasso.ifoodclone.core.domain.model.Dish
import com.samuelbaldasso.ifoodclone.core.domain.model.Money
import com.samuelbaldasso.ifoodclone.core.domain.model.Option
import com.samuelbaldasso.ifoodclone.core.domain.model.Restaurant
import com.samuelbaldasso.ifoodclone.core.domain.model.CartError
import com.samuelbaldasso.ifoodclone.core.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class RoomCartRepository @Inject constructor(
    private val cartDao: CartDao,
    private val restaurantDao: RestaurantDao
) : CartRepository {

    override fun getCart(): Flow<Cart> {
        return cartDao.observeCartItemsWithOptions().map { list ->
            if (list.isEmpty()) {
                Cart()
            } else {
                val restaurantId = list.first().item.restaurantId
                val restaurantEntity = restaurantDao.getRestaurantById(restaurantId)
                Cart(
                    restaurant = restaurantEntity?.toDomain(),
                    items = list.map { it.toDomain() }
                )
            }
        }
    }

    override suspend fun addToCart(
        restaurant: Restaurant,
        dish: Dish,
        selectedOptions: List<Option>,
        quantity: Int,
        forceClearIfDifferentRestaurant: Boolean
    ): AppResult<Unit, CartError> {
        return try {
            val currentRestaurantId = cartDao.getCurrentRestaurantId()
            if (currentRestaurantId != null && currentRestaurantId != restaurant.id) {
                if (!forceClearIfDifferentRestaurant) {
                    val currentRestaurantName = restaurantDao.getRestaurantById(currentRestaurantId)?.name ?: "Outro restaurante"
                    return AppResult.Error(
                        CartError.DifferentRestaurant(
                            currentRestaurantId = currentRestaurantId,
                            currentRestaurantName = currentRestaurantName,
                            newRestaurantId = restaurant.id,
                            newRestaurantName = restaurant.name
                        )
                    )
                } else {
                    cartDao.clearCart()
                }
            }

            // Grouping identical items (RN-CART-02)
            val existingItems = cartDao.getCartItemsWithOptions()
            val targetOptionIds = selectedOptions.map { it.id }.toSet()

            val matchingItem = existingItems.find { itemWithOptions ->
                itemWithOptions.item.dishId == dish.id &&
                        itemWithOptions.options.map { it.optionId }.toSet() == targetOptionIds
            }

            if (matchingItem != null) {
                val newQty = matchingItem.item.quantity + quantity
                if (newQty > 20) {
                    return AppResult.Error(CartError.MaxQuantityExceeded(20))
                }
                cartDao.updateQuantity(matchingItem.item.id, newQty)
            } else {
                if (quantity > 20) {
                    return AppResult.Error(CartError.MaxQuantityExceeded(20))
                }

                var unitPrice = dish.effectivePrice
                for (opt in selectedOptions) {
                    unitPrice += opt.extraPrice
                }

                val cartItemId = UUID.randomUUID().toString()
                val cartItemEntity = CartItemEntity(
                    id = cartItemId,
                    restaurantId = restaurant.id,
                    dishId = dish.id,
                    dishName = dish.name,
                    dishImageUrl = dish.imageUrl,
                    unitPriceCents = unitPrice.cents,
                    quantity = quantity
                )

                val optionEntities = selectedOptions.map { opt ->
                    CartItemOptionEntity(
                        id = UUID.randomUUID().toString(),
                        cartItemId = cartItemId,
                        optionId = opt.id,
                        name = opt.name,
                        extraPriceCents = opt.extraPrice.cents
                    )
                }

                cartDao.insertCartItemWithOptions(cartItemEntity, optionEntities)
            }

            AppResult.Success(Unit)
        } catch (t: Throwable) {
            AppResult.Error(CartError.ItemNotFound(t.localizedMessage ?: "Erro desconhecido ao adicionar ao carrinho"))
        }
    }

    override suspend fun updateItemQuantity(
        cartItemId: String,
        newQuantity: Int
    ): AppResult<Unit, AppError> {
        return try {
            if (newQuantity <= 0) {
                cartDao.deleteCartItem(cartItemId)
            } else {
                if (newQuantity > 20) {
                    return AppResult.Error(AppError.Validation("Quantidade máxima é de 20 unidades por item (RN-CART-03)."))
                }
                cartDao.updateQuantity(cartItemId, newQuantity)
            }
            AppResult.Success(Unit)
        } catch (t: Throwable) {
            AppResult.Error(AppError.Unknown(throwable = t, message = t.localizedMessage))
        }
    }

    override suspend fun removeItem(cartItemId: String): AppResult<Unit, AppError> {
        return try {
            cartDao.deleteCartItem(cartItemId)
            AppResult.Success(Unit)
        } catch (t: Throwable) {
            AppResult.Error(AppError.Unknown(throwable = t, message = t.localizedMessage))
        }
    }

    override suspend fun clearCart(): AppResult<Unit, AppError> {
        return try {
            cartDao.clearCart()
            AppResult.Success(Unit)
        } catch (t: Throwable) {
            AppResult.Error(AppError.Unknown(throwable = t, message = t.localizedMessage))
        }
    }
}
