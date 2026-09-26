package com.samuelbaldasso.ifoodclone.ui.checkout

import com.samuelbaldasso.ifoodclone.core.domain.model.Cart
import com.samuelbaldasso.ifoodclone.core.domain.model.Money
import com.samuelbaldasso.ifoodclone.core.domain.model.Order
import com.samuelbaldasso.ifoodclone.core.domain.model.OrderAddress
import com.samuelbaldasso.ifoodclone.core.domain.model.PaymentMethod

data class CheckoutUiState(
    val cart: Cart = Cart(),
    val deliveryAddress: OrderAddress = OrderAddress(
        street = "Rua dos Desenvolvedores",
        number = "1234",
        neighborhood = "Jardins",
        city = "São Paulo",
        state = "SP",
        complement = "Apto 42",
        reference = "Próximo à estação"
    ),
    val selectedPaymentMethod: PaymentMethod = PaymentMethod.PIX,
    val discount: Money = Money.ZERO,
    val couponCode: String? = null,
    val changeForCash: String = "",
    val cpfOnInvoice: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val placedOrder: Order? = null
) {
    val finalTotal: Money get() {
        val total = cart.total
        return (total - discount).coerceAtLeastZero()
    }
}

sealed interface CheckoutUiIntent {
    data class SelectPaymentMethod(val method: PaymentMethod) : CheckoutUiIntent
    data class UpdateChangeForCash(val amount: String) : CheckoutUiIntent
    data class UpdateCpf(val cpf: String) : CheckoutUiIntent
    data class UpdateAddress(val address: OrderAddress) : CheckoutUiIntent
    data object ConfirmOrder : CheckoutUiIntent
}

sealed interface CheckoutUiEffect {
    data class NavigateToOrderTracking(val orderId: String) : CheckoutUiEffect
    data object NavigateBack : CheckoutUiEffect
    data class ShowSnackbar(val message: String) : CheckoutUiEffect
}
