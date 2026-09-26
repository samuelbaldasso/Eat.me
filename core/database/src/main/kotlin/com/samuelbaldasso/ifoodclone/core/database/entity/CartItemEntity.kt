package com.samuelbaldasso.ifoodclone.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val id: String,
    val restaurantId: String,
    val dishId: String,
    val dishName: String,
    val dishImageUrl: String?,
    val unitPriceCents: Long,
    val quantity: Int,
    val notes: String? = null
)
