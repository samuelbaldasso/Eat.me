package com.samuelbaldasso.ifoodclone.core.domain.repository

import com.samuelbaldasso.ifoodclone.core.domain.model.AppResult
import com.samuelbaldasso.ifoodclone.core.domain.model.Cart
import com.samuelbaldasso.ifoodclone.core.domain.model.Money
import com.samuelbaldasso.ifoodclone.core.domain.model.Order
import com.samuelbaldasso.ifoodclone.core.domain.model.OrderAddress
import com.samuelbaldasso.ifoodclone.core.domain.model.OrderError
import com.samuelbaldasso.ifoodclone.core.domain.model.OrderStatus
import com.samuelbaldasso.ifoodclone.core.domain.model.PaymentMethod
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    fun getOrders(): Flow<List<Order>>

    fun getOrderById(orderId: String): Flow<Order?>

    suspend fun placeOrder(
        cart: Cart,
        paymentMethod: PaymentMethod,
        deliveryAddress: OrderAddress,
        discount: Money
    ): AppResult<Order, OrderError>

    suspend fun updateOrderStatus(
        orderId: String,
        newStatus: OrderStatus
    ): AppResult<Unit, OrderError>

    suspend fun cancelOrder(orderId: String): AppResult<Unit, OrderError>
}
