package com.samuelbaldasso.ifoodclone.core.database.repository

import com.samuelbaldasso.ifoodclone.core.database.dao.CartDao
import com.samuelbaldasso.ifoodclone.core.database.dao.OrderDao
import com.samuelbaldasso.ifoodclone.core.database.mapper.createOrderEntitiesFromCart
import com.samuelbaldasso.ifoodclone.core.database.mapper.toDomain
import com.samuelbaldasso.ifoodclone.core.domain.model.AppResult
import com.samuelbaldasso.ifoodclone.core.domain.model.Cart
import com.samuelbaldasso.ifoodclone.core.domain.model.Money
import com.samuelbaldasso.ifoodclone.core.domain.model.Order
import com.samuelbaldasso.ifoodclone.core.domain.model.OrderAddress
import com.samuelbaldasso.ifoodclone.core.domain.model.OrderError
import com.samuelbaldasso.ifoodclone.core.domain.model.OrderStatus
import com.samuelbaldasso.ifoodclone.core.domain.model.PaymentMethod
import com.samuelbaldasso.ifoodclone.core.domain.repository.OrderRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomOrderRepository(
    private val orderDao: OrderDao,
    private val cartDao: CartDao,
    private val ioDispatcher: CoroutineDispatcher
) : OrderRepository {

    @Inject
    constructor(
        orderDao: OrderDao,
        cartDao: CartDao
    ) : this(orderDao, cartDao, Dispatchers.IO)

    override fun getOrders(): Flow<List<Order>> {
        return orderDao.getOrders().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getOrderById(orderId: String): Flow<Order?> {
        return orderDao.getOrderById(orderId).map { it?.toDomain() }
    }

    override suspend fun placeOrder(
        cart: Cart,
        paymentMethod: PaymentMethod,
        deliveryAddress: OrderAddress,
        discount: Money
    ): AppResult<Order, OrderError> = withContext(ioDispatcher) {
        if (cart.isEmpty) {
            return@withContext AppResult.Error(OrderError.EmptyCart)
        }
        if (!cart.isMinOrderSatisfied) {
            return@withContext AppResult.Error(
                OrderError.MinOrderNotSatisfied(
                    minOrderValue = cart.minOrderValue,
                    currentSubtotal = cart.subtotal
                )
            )
        }

        val (orderEntity, itemEntities, optionEntities) = createOrderEntitiesFromCart(
            cart = cart,
            paymentMethod = paymentMethod,
            address = deliveryAddress,
            discount = discount
        )

        orderDao.saveCompleteOrder(orderEntity, itemEntities, optionEntities)
        // Clear cart after placing order
        cartDao.clearCart()

        val savedOrder = orderDao.getOrderById(orderEntity.id).firstOrNull()?.toDomain()
        if (savedOrder != null) {
            AppResult.Success(savedOrder)
        } else {
            AppResult.Error(OrderError.OrderNotFound(orderEntity.id))
        }
    }

    override suspend fun updateOrderStatus(
        orderId: String,
        newStatus: OrderStatus
    ): AppResult<Unit, OrderError> = withContext(ioDispatcher) {
        orderDao.updateOrderStatus(orderId, newStatus.name)
        AppResult.Success(Unit)
    }

    override suspend fun cancelOrder(orderId: String): AppResult<Unit, OrderError> = withContext(ioDispatcher) {
        orderDao.updateOrderStatus(orderId, OrderStatus.CANCELLED.name)
        AppResult.Success(Unit)
    }
}
