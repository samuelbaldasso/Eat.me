package com.samuelbaldasso.ifoodclone.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val restaurantId: String,
    val restaurantName: String,
    val restaurantImageUrl: String?,
    val subtotalCents: Long,
    val deliveryFeeCents: Long,
    val discountCents: Long,
    val totalCents: Long,
    val status: String,
    val paymentMethod: String,
    val street: String,
    val number: String,
    val neighborhood: String,
    val city: String,
    val state: String,
    val complement: String?,
    val reference: String?,
    val createdAtMillis: Long,
    val estimatedDeliveryMinutes: Int
)
