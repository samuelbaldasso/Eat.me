package com.samuelbaldasso.ifoodclone.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CartItemOption(
    val optionId: String,
    val name: String,
    val extraPrice: Money
)

@Serializable
data class CartItem(
    val id: String,
    val restaurantId: String,
    val dishId: String,
    val dishName: String,
    val dishImageUrl: String? = null,
    val unitPrice: Money,
    val quantity: Int,
    val selectedOptions: List<CartItemOption> = emptyList(),
    val notes: String? = null
) {
    val totalPrice: Money get() = unitPrice * quantity
}

@Serializable
data class Cart(
    val restaurant: Restaurant? = null,
    val items: List<CartItem> = emptyList()
) {
    val isEmpty: Boolean get() = items.isEmpty()
    val isNotEmpty: Boolean get() = items.isNotEmpty()
    val totalQuantity: Int get() = items.sumOf { it.quantity }

    val subtotal: Money get() {
        var sum = Money.ZERO
        for (item in items) {
            sum += item.totalPrice
        }
        return sum
    }

    val deliveryFee: Money get() = restaurant?.deliveryFee ?: Money.ZERO

    val total: Money get() = if (isEmpty) Money.ZERO else subtotal + deliveryFee

    val minOrderValue: Money get() = restaurant?.minOrderValue ?: Money.ZERO

    val isMinOrderSatisfied: Boolean get() = subtotal >= minOrderValue

    val amountMissingForMinOrder: Money get() {
        return if (isMinOrderSatisfied) Money.ZERO else minOrderValue - subtotal
    }
}
