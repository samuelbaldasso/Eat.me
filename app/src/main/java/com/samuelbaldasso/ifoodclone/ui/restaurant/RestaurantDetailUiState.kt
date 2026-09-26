package com.samuelbaldasso.ifoodclone.ui.restaurant

import com.samuelbaldasso.ifoodclone.core.domain.model.Cart
import com.samuelbaldasso.ifoodclone.core.domain.model.CartError
import com.samuelbaldasso.ifoodclone.core.domain.model.Dish
import com.samuelbaldasso.ifoodclone.core.domain.model.Money
import com.samuelbaldasso.ifoodclone.core.domain.model.RestaurantDetails

data class DishCustomizationState(
    val dish: Dish,
    val selectedOptionsByGroup: Map<String, Set<String>> = emptyMap(),
    val quantity: Int = 1,
    val totalPrice: Money = dish.effectivePrice,
    val isValid: Boolean = true,
    val groupErrors: Map<String, String> = emptyMap()
)

data class RestaurantDetailUiState(
    val isLoading: Boolean = true,
    val restaurantDetails: RestaurantDetails? = null,
    val selectedSectionIndex: Int = 0,
    val customizationState: DishCustomizationState? = null,
    val cart: Cart = Cart(),
    val showDifferentRestaurantDialog: Boolean = false,
    val pendingDifferentRestaurantError: CartError.DifferentRestaurant? = null,
    val errorMessage: String? = null
)

sealed interface RestaurantDetailIntent {
    data class LoadDetails(val restaurantId: String) : RestaurantDetailIntent
    data class SelectSection(val index: Int) : RestaurantDetailIntent
    data class OpenDishCustomization(val dish: Dish) : RestaurantDetailIntent
    data object CloseDishCustomization : RestaurantDetailIntent
    data class ToggleOption(val groupId: String, val optionId: String) : RestaurantDetailIntent
    data class ChangeQuantity(val newQuantity: Int) : RestaurantDetailIntent
    data object ConfirmAddToCart : RestaurantDetailIntent
    data object ConfirmClearCartAndAdd : RestaurantDetailIntent
    data object DismissDifferentRestaurantDialog : RestaurantDetailIntent
    data object Retry : RestaurantDetailIntent
}

sealed interface RestaurantDetailEffect {
    data object NavigateBack : RestaurantDetailEffect
    data object NavigateToCart : RestaurantDetailEffect
    data class ShowSnackbar(val message: String) : RestaurantDetailEffect
    data class AddedToCart(val dishName: String, val quantity: Int, val totalPrice: Money) : RestaurantDetailEffect
}
