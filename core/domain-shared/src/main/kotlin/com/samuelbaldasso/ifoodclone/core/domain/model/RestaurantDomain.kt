package com.samuelbaldasso.ifoodclone.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Restaurant(
    val id: String,
    val name: String,
    val category: String,
    val rating: Double,
    val ratingCount: Int = 0,
    val deliveryTimeRange: String,
    val deliveryFee: Money,
    val minOrderValue: Money = Money.ZERO,
    val distanceKm: Double = 0.0,
    val imageUrl: String,
    val isOpen: Boolean = true
)

@Serializable
data class RestaurantDetails(
    val restaurant: Restaurant,
    val description: String,
    val address: String,
    val menuSections: List<MenuSection>
)

@Serializable
data class MenuSection(
    val id: String,
    val name: String,
    val dishes: List<Dish>
)

@Serializable
data class Dish(
    val id: String,
    val restaurantId: String,
    val name: String,
    val description: String,
    val imageUrl: String? = null,
    val basePrice: Money,
    val promoPrice: Money? = null,
    val isAvailable: Boolean = true,
    val optionGroups: List<OptionGroup> = emptyList(),
    val servesPeople: Int = 1
) {
    val effectivePrice: Money get() = promoPrice ?: basePrice
}

@Serializable
data class OptionGroup(
    val id: String,
    val title: String,
    val minSelect: Int = 0,
    val maxSelect: Int = 1,
    val isRequired: Boolean = false,
    val options: List<Option>
) {
    init {
        require(minSelect >= 0) { "minSelect must be >= 0, was $minSelect" }
        require(maxSelect >= minSelect) { "maxSelect ($maxSelect) must be >= minSelect ($minSelect)" }
    }
}

@Serializable
data class Option(
    val id: String,
    val name: String,
    val extraPrice: Money = Money.ZERO,
    val isAvailable: Boolean = true
)
