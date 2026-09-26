package com.samuelbaldasso.ifoodclone.ui.cart

import com.samuelbaldasso.ifoodclone.core.domain.model.Cart
import com.samuelbaldasso.ifoodclone.core.domain.model.Money

data class CartUiState(
    val cart: Cart = Cart(),
    val couponCode: String = "",
    val discount: Money = Money.ZERO,
    val isCouponApplied: Boolean = false,
    val couponError: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val finalTotal: Money get() {
        val totalWithDelivery = cart.total
        return (totalWithDelivery - discount).coerceAtLeastZero()
    }
}

sealed interface CartUiIntent {
    data class UpdateQuantity(val cartItemId: String, val newQuantity: Int) : CartUiIntent
    data class RemoveItem(val cartItemId: String) : CartUiIntent
    data object ClearCart : CartUiIntent
    data class SetCouponCode(val code: String) : CartUiIntent
    data object ApplyCoupon : CartUiIntent
    data object RemoveCoupon : CartUiIntent
}

sealed interface CartUiEffect {
    data object NavigateBack : CartUiEffect
    data object NavigateToCheckout : CartUiEffect
    data class NavigateToRestaurant(val restaurantId: String) : CartUiEffect
    data class ShowSnackbar(val message: String) : CartUiEffect
}
