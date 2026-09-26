package com.samuelbaldasso.ifoodclone.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurants")
data class RestaurantEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val rating: Double,
    val ratingCount: Int,
    val deliveryTimeRange: String,
    val deliveryFeeCents: Long,
    val minOrderValueCents: Long,
    val distanceKm: Double,
    val imageUrl: String,
    val isOpen: Boolean,
    val description: String,
    val address: String
)
