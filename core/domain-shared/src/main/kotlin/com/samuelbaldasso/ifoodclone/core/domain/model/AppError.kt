package com.samuelbaldasso.ifoodclone.core.domain.model

sealed interface AppError {
    data object Network : AppError
    data object Timeout : AppError
    data object Unauthorized : AppError
    data class Validation(val message: String, val field: String? = null) : AppError
    data class BusinessRule(val code: String, val message: String? = null) : AppError
    data class NotFound(val message: String? = null) : AppError
    data class Unknown(val throwable: Throwable? = null, val message: String? = null) : AppError
}

sealed interface CartError : AppError {
    data class DifferentRestaurant(
        val currentRestaurantId: String,
        val currentRestaurantName: String,
        val newRestaurantId: String,
        val newRestaurantName: String
    ) : CartError

    data class MaxQuantityExceeded(val limit: Int = 20) : CartError
    data class ItemNotFound(val id: String) : CartError
}

sealed interface OrderError : AppError {
    data class Persistence(val cause: Throwable) : OrderError
    data object EmptyCart : OrderError
    data class MinOrderNotSatisfied(val minOrderValue: Money, val currentSubtotal: Money) : OrderError
    data class OrderNotFound(val orderId: String) : OrderError
    data class InvalidStateTransition(val from: String, val to: String) : OrderError
}
