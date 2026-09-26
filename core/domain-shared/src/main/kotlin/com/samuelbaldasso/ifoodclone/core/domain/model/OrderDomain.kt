package com.samuelbaldasso.ifoodclone.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class OrderStatus(val label: String) {
    PLACED("Pedido realizado"),
    CONFIRMED("Confirmado pelo restaurante"),
    PREPARING("Em preparação"),
    OUT_FOR_DELIVERY("Saiu para entrega"),
    DELIVERED("Entregue"),
    CANCELLED("Cancelado")
}

@Serializable
enum class PaymentMethod(val label: String, val iconDescription: String) {
    PIX("Pix", "Chave Pix com aprovação imediata"),
    CREDIT_CARD("Cartão de Crédito", "Mastercard •••• 4242"),
    DEBIT_CARD("Cartão de Débito", "Visa •••• 8888"),
    CASH("Dinheiro", "Pagar na entrega"),
    EATME_PAY("Eat.me Pay", "Saldo da carteira digital")
}

@Serializable
data class OrderAddress(
    val street: String,
    val number: String,
    val neighborhood: String,
    val city: String,
    val state: String,
    val complement: String? = null,
    val reference: String? = null
) {
    val formatted: String
        get() = "$street, $number${complement?.let { " - $it" } ?: ""} • $neighborhood, $city - $state"
}

@Serializable
data class OrderItem(
    val id: String,
    val dishId: String,
    val dishName: String,
    val unitPrice: Money,
    val quantity: Int,
    val totalPrice: Money,
    val selectedOptions: List<CartItemOption> = emptyList(),
    val notes: String? = null
)

@Serializable
data class Order(
    val id: String,
    val restaurantId: String,
    val restaurantName: String,
    val restaurantImageUrl: String? = null,
    val items: List<OrderItem>,
    val subtotal: Money,
    val deliveryFee: Money,
    val discount: Money,
    val total: Money,
    val status: OrderStatus,
    val paymentMethod: PaymentMethod,
    val deliveryAddress: OrderAddress,
    val createdAtMillis: Long,
    val estimatedDeliveryMinutes: Int = 35
) {
    val isActive: Boolean
        get() = status != OrderStatus.DELIVERED && status != OrderStatus.CANCELLED
}
