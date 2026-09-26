package com.samuelbaldasso.ifoodclone.ui.tracking

import com.samuelbaldasso.ifoodclone.core.domain.model.Order
import com.samuelbaldasso.ifoodclone.core.domain.model.OrderStatus

data class OrderTrackingUiState(
    val order: Order? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val courierName: String = "Carlos Santos",
    val courierVehicle: String = "Honda CG 160 • Vermelha",
    val deliveryPin: String = "4921",
    val canCancel: Boolean = true
) {
    val currentStatus: OrderStatus get() = order?.status ?: OrderStatus.CONFIRMED
}

sealed interface OrderTrackingIntent {
    data object AdvanceSimulationStatus : OrderTrackingIntent
    data object CancelOrder : OrderTrackingIntent
}

sealed interface OrderTrackingEffect {
    data object NavigateBack : OrderTrackingEffect
    data class ShowSnackbar(val message: String) : OrderTrackingEffect
}
