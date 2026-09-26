package com.samuelbaldasso.ifoodclone.core.database.repository

import com.google.common.truth.Truth.assertThat
import com.samuelbaldasso.ifoodclone.core.database.dao.CartDao
import com.samuelbaldasso.ifoodclone.core.database.dao.OrderDao
import com.samuelbaldasso.ifoodclone.core.database.entity.OrderEntity
import com.samuelbaldasso.ifoodclone.core.database.relation.OrderWithItems
import com.samuelbaldasso.ifoodclone.core.domain.model.AppResult
import com.samuelbaldasso.ifoodclone.core.domain.model.Cart
import com.samuelbaldasso.ifoodclone.core.domain.model.CartItem
import com.samuelbaldasso.ifoodclone.core.domain.model.Money
import com.samuelbaldasso.ifoodclone.core.domain.model.OrderAddress
import com.samuelbaldasso.ifoodclone.core.domain.model.OrderError
import com.samuelbaldasso.ifoodclone.core.domain.model.OrderStatus
import com.samuelbaldasso.ifoodclone.core.domain.model.PaymentMethod
import com.samuelbaldasso.ifoodclone.core.domain.model.Restaurant
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

class RoomOrderRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private val orderDao: OrderDao = mockk(relaxed = true)
    private val cartDao: CartDao = mockk(relaxed = true)

    private val repository = RoomOrderRepository(
        orderDao = orderDao,
        cartDao = cartDao,
        ioDispatcher = testDispatcher
    )

    private val sampleAddress = OrderAddress(
        street = "Rua dos Desenvolvedores",
        number = "1234",
        neighborhood = "Jardins",
        city = "São Paulo",
        state = "SP"
    )

    private val sampleRestaurant = Restaurant(
        id = "rest_1",
        name = "Burger King Test",
        category = "Lanches",
        deliveryFee = Money(500L),
        deliveryTimeRange = "30-40 min",
        rating = 4.5,
        imageUrl = "http://example.com/logo.png",
        minOrderValue = Money(2000L)
    )

    @Test
    fun `GIVEN empty cart WHEN placeOrder THEN returns EmptyCart error`() = runTest(testDispatcher) {
        val result = repository.placeOrder(
            cart = Cart(),
            paymentMethod = PaymentMethod.PIX,
            deliveryAddress = sampleAddress,
            discount = Money.ZERO
        )

        assertThat(result).isInstanceOf(AppResult.Error::class.java)
        val error = (result as AppResult.Error).error
        assertThat(error).isEqualTo(OrderError.EmptyCart)
    }

    @Test
    fun `GIVEN cart below min order value WHEN placeOrder THEN returns MinOrderNotSatisfied error`() = runTest(testDispatcher) {
        val cart = Cart(
            restaurant = sampleRestaurant,
            items = listOf(
                CartItem(
                    id = "item_1",
                    restaurantId = "rest_1",
                    dishId = "dish_1",
                    dishName = "Batata Frita",
                    unitPrice = Money(1000L),
                    quantity = 1
                )
            )
        )

        val result = repository.placeOrder(
            cart = cart,
            paymentMethod = PaymentMethod.PIX,
            deliveryAddress = sampleAddress,
            discount = Money.ZERO
        )

        assertThat(result).isInstanceOf(AppResult.Error::class.java)
        val error = (result as AppResult.Error).error
        assertThat(error).isInstanceOf(OrderError.MinOrderNotSatisfied::class.java)
    }

    @Test
    fun `GIVEN valid cart WHEN placeOrder THEN saves order, clears cart, and returns placed order`() = runTest(testDispatcher) {
        val cart = Cart(
            restaurant = sampleRestaurant,
            items = listOf(
                CartItem(
                    id = "item_1",
                    restaurantId = "rest_1",
                    dishId = "dish_1",
                    dishName = "Whopper Test",
                    unitPrice = Money(2500L),
                    quantity = 1
                )
            )
        )

        every { orderDao.getOrderById(any()) } answers {
            val orderId = firstArg<String>()
            flowOf(
                OrderWithItems(
                    order = OrderEntity(
                        id = orderId,
                        restaurantId = "rest_1",
                        restaurantName = "Burger King Test",
                        restaurantImageUrl = "http://example.com/logo.png",
                        subtotalCents = 2500L,
                        deliveryFeeCents = 500L,
                        discountCents = 0L,
                        totalCents = 3000L,
                        status = OrderStatus.CONFIRMED.name,
                        paymentMethod = PaymentMethod.PIX.name,
                        street = sampleAddress.street,
                        number = sampleAddress.number,
                        neighborhood = sampleAddress.neighborhood,
                        city = sampleAddress.city,
                        state = sampleAddress.state,
                        complement = null,
                        reference = null,
                        createdAtMillis = System.currentTimeMillis(),
                        estimatedDeliveryMinutes = 35
                    ),
                    items = emptyList()
                )
            )
        }

        val result = repository.placeOrder(
            cart = cart,
            paymentMethod = PaymentMethod.PIX,
            deliveryAddress = sampleAddress,
            discount = Money.ZERO
        )

        assertThat(result).isInstanceOf(AppResult.Success::class.java)
        val order = (result as AppResult.Success).data
        assertThat(order.restaurantName).isEqualTo("Burger King Test")
        assertThat(order.total).isEqualTo(Money(3000L))

        coVerify { orderDao.saveCompleteOrder(any(), any(), any()) }
        coVerify { cartDao.clearCart() }
    }

    @Test
    fun `GIVEN order ID and new status WHEN updateOrderStatus THEN calls DAO`() = runTest(testDispatcher) {
        val result = repository.updateOrderStatus("ord_1", OrderStatus.OUT_FOR_DELIVERY)

        assertThat(result).isInstanceOf(AppResult.Success::class.java)
        coVerify { orderDao.updateOrderStatus("ord_1", OrderStatus.OUT_FOR_DELIVERY.name) }
    }

    @Test
    fun `GIVEN order ID WHEN cancelOrder THEN sets status to CANCELLED`() = runTest(testDispatcher) {
        val result = repository.cancelOrder("ord_1")

        assertThat(result).isInstanceOf(AppResult.Success::class.java)
        coVerify { orderDao.updateOrderStatus("ord_1", OrderStatus.CANCELLED.name) }
    }
}
