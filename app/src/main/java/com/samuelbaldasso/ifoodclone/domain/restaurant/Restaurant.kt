package com.samuelbaldasso.ifoodclone.domain.restaurant

data class Restaurant(
    val id: String,
    val name: String,
    val category: String,
    val deliveryFee: String,
    val deliveryTime: String,
    val rating: Double,
    val imageUrl: String
)