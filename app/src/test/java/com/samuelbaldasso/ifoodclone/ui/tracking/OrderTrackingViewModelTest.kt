package com.samuelbaldasso.ifoodclone.ui.tracking

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import com.samuelbaldasso.ifoodclone.core.domain.model.AppResult
import com.samuelbaldasso.ifoodclone.core.domain.model.Money
import com.samuelbaldasso.ifoodclone.core.domain.model.Order
import com.samuelbaldasso.ifoodclone.core.domain.model.OrderAddress
import com.samuelbaldasso.ifoodclone.core.domain.model.OrderStatus
import com.samuelbaldasso.ifoodclone.core.domain.model.PaymentMethod
import com.samuelbaldasso.ifoodclone.core.domain.repository.OrderRepository
import io.mockk.coEvery
import io.mockk.coVerify
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
class OrderTrackingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val orderRepository: OrderRepository = mockk(relaxed = true)
    private val orderFlow = MutableStateFlow<Order?>(null)

    private val sampleOrder = Order(
        id = "ord_999",
        restaurantId = "rest_1",
        restaurantName = "Burger King Test",
        items = emptyList(),
        subtotal = Money(3000L),
        deliveryFee = Money(500L),
        discount = Money.ZERO,
        total = Money(3500L),
        status = OrderStatus.CONFIRMED,
        paymentMethod = PaymentMethod.PIX,
        deliveryAddress = OrderAddress("Rua B", "20", "Centro", "SP", "SP"),
        createdAtMillis = System.currentTimeMillis()
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { orderRepository.getOrderById("ord_999") } returns orderFlow
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `GIVEN order loaded WHEN initialized THEN exposes order details and allows cancellation`() = runTest(testDispatcher) {
        orderFlow.value = sampleOrder
        val savedStateHandle = SavedStateHandle(mapOf("orderId" to "ord_999"))
        val viewModel = OrderTrackingViewModel(orderRepository, savedStateHandle)
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.order?.restaurantName).isEqualTo("Burger King Test")
        assertThat(state.canCancel).isTrue()
    }

    @Test
    fun `GIVEN active order WHEN AdvanceSimulationStatus intent sent THEN updates order status`() = runTest(testDispatcher) {
        orderFlow.value = sampleOrder
        coEvery { orderRepository.updateOrderStatus("ord_999", OrderStatus.PREPARING) } returns AppResult.Success(Unit)

        val savedStateHandle = SavedStateHandle(mapOf("orderId" to "ord_999"))
        val viewModel = OrderTrackingViewModel(orderRepository, savedStateHandle)
        testScheduler.advanceUntilIdle()

        viewModel.handleIntent(OrderTrackingIntent.AdvanceSimulationStatus)
        testScheduler.advanceUntilIdle()

        coVerify { orderRepository.updateOrderStatus("ord_999", OrderStatus.PREPARING) }
    }

    @Test
    fun `GIVEN cancellable order WHEN CancelOrder intent sent THEN delegates to repository`() = runTest(testDispatcher) {
        orderFlow.value = sampleOrder
        coEvery { orderRepository.cancelOrder("ord_999") } returns AppResult.Success(Unit)

        val savedStateHandle = SavedStateHandle(mapOf("orderId" to "ord_999"))
        val viewModel = OrderTrackingViewModel(orderRepository, savedStateHandle)
        testScheduler.advanceUntilIdle()

        viewModel.handleIntent(OrderTrackingIntent.CancelOrder)
        testScheduler.advanceUntilIdle()

        coVerify { orderRepository.cancelOrder("ord_999") }
    }
}
