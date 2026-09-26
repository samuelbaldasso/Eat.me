package com.samuelbaldasso.ifoodclone.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.samuelbaldasso.ifoodclone.core.database.entity.CartItemEntity
import com.samuelbaldasso.ifoodclone.core.database.entity.CartItemOptionEntity
import com.samuelbaldasso.ifoodclone.core.database.relation.CartItemWithOptions
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Transaction
    @Query("SELECT * FROM cart_items")
    fun observeCartItemsWithOptions(): Flow<List<CartItemWithOptions>>

    @Transaction
    @Query("SELECT * FROM cart_items")
    suspend fun getCartItemsWithOptions(): List<CartItemWithOptions>

    @Query("SELECT * FROM cart_items WHERE id = :id")
    suspend fun getCartItemById(id: String): CartItemEntity?

    @Query("SELECT restaurantId FROM cart_items LIMIT 1")
    suspend fun getCurrentRestaurantId(): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItemOptions(options: List<CartItemOptionEntity>)

    @Query("UPDATE cart_items SET quantity = :newQuantity WHERE id = :id")
    suspend fun updateQuantity(id: String, newQuantity: Int)

    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun deleteCartItem(id: String)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    @Transaction
    suspend fun insertCartItemWithOptions(
        item: CartItemEntity,
        options: List<CartItemOptionEntity>
    ) {
        insertCartItem(item)
        if (options.isNotEmpty()) {
            insertCartItemOptions(options)
        }
    }
}
