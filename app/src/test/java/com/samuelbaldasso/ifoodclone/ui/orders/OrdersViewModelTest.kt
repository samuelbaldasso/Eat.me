package com.samuelbaldasso.ifoodclone.ui.orders

import com.google.common.truth.Truth.assertThat
import com.samuelbaldasso.ifoodclone.core.domain.model.Money
import com.samuelbaldasso.ifoodclone.core.domain.model.Order
import com.samuelbaldasso.ifoodclone.core.domain.model.OrderAddress
import com.samuelbaldasso.ifoodclone.core.domain.model.OrderStatus
import com.samuelbaldasso.ifoodclone.core.domain.model.PaymentMethod
import com.samuelbaldasso.ifoodclone.core.domain.repository.OrderRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OrdersViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val orderRepository: OrderRepository = mockk(relaxed = true)
    private val ordersFlow = MutableStateFlow<List<Order>>(emptyList())

    private val activeOrder = Order(
        id = "ord_active",
        restaurantId = "rest_1",
        restaurantName = "Burger King Test",
        items = emptyList(),
        subtotal = Money(3000L),
        deliveryFee = Money(500L),
        discount = Money.ZERO,
        total = Money(3500L),
        status = OrderStatus.PREPARING,
        paymentMethod = PaymentMethod.PIX,
        deliveryAddress = OrderAddress("Rua A", "10", "Centro", "SP", "SP"),
        createdAtMillis = System.currentTimeMillis()
    )

    private val deliveredOrder = Order(
        id = "ord_delivered",
        restaurantId = "rest_2",
        restaurantName = "Pizza Hut",
        items = emptyList(),
        subtotal = Money(5000L),
        deliveryFee = Money.ZERO,
        discount = Money.ZERO,
        total = Money(5000L),
        status = OrderStatus.DELIVERED,
        paymentMethod = PaymentMethod.CREDIT_CARD,
        deliveryAddress = OrderAddress("Rua A", "10", "Centro", "SP", "SP"),
        createdAtMillis = System.currentTimeMillis() - 86400000L
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { orderRepository.getOrders() } returns ordersFlow
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `GIVEN empty orders WHEN observed THEN exposes empty state`() = runTest(testDispatcher) {
        val viewModel = OrdersViewModel(orderRepository)
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.isEmpty).isTrue()
    }

    @Test
    fun `GIVEN active and past orders WHEN observed THEN correctly partitions orders`() = runTest(testDispatcher) {
        ordersFlow.value = listOf(activeOrder, deliveredOrder)
        val viewModel = OrdersViewModel(orderRepository)
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.activeOrders).containsExactly(activeOrder)
        assertThat(state.pastOrders).containsExactly(deliveredOrder)
    }
}
