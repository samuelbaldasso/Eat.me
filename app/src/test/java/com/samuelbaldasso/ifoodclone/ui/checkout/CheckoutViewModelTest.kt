package com.samuelbaldasso.ifoodclone.ui.checkout

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.samuelbaldasso.ifoodclone.core.domain.model.AppResult
import com.samuelbaldasso.ifoodclone.core.domain.model.Cart
import com.samuelbaldasso.ifoodclone.core.domain.model.CartItem
import com.samuelbaldasso.ifoodclone.core.domain.model.Money
import com.samuelbaldasso.ifoodclone.core.domain.model.Order
import com.samuelbaldasso.ifoodclone.core.domain.model.OrderAddress
import com.samuelbaldasso.ifoodclone.core.domain.model.OrderStatus
import com.samuelbaldasso.ifoodclone.core.domain.model.PaymentMethod
import com.samuelbaldasso.ifoodclone.core.domain.model.Restaurant
import com.samuelbaldasso.ifoodclone.core.domain.repository.CartRepository
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
class CheckoutViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val cartRepository: CartRepository = mockk(relaxed = true)
    private val orderRepository: OrderRepository = mockk(relaxed = true)
    private val cartFlow = MutableStateFlow(Cart())

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

    private val sampleCart = Cart(
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

    private val sampleOrder = Order(
        id = "ord_123",
        restaurantId = "rest_1",
        restaurantName = "Burger King Test",
        items = emptyList(),
        subtotal = Money(2500L),
        deliveryFee = Money(500L),
        discount = Money.ZERO,
        total = Money(3000L),
        status = OrderStatus.CONFIRMED,
        paymentMethod = PaymentMethod.PIX,
        deliveryAddress = OrderAddress("Rua A", "10", "Centro", "SP", "SP"),
        createdAtMillis = System.currentTimeMillis()
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { cartRepository.getCart() } returns cartFlow
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `GIVEN payment method chosen WHEN SelectPaymentMethod THEN updates uiState`() = runTest(testDispatcher) {
        val viewModel = CheckoutViewModel(cartRepository, orderRepository)
        testScheduler.advanceUntilIdle()

        viewModel.handleIntent(CheckoutUiIntent.SelectPaymentMethod(PaymentMethod.CREDIT_CARD))

        assertThat(viewModel.uiState.value.selectedPaymentMethod).isEqualTo(PaymentMethod.CREDIT_CARD)
    }

    @Test
    fun `GIVEN valid cart WHEN ConfirmOrder intent sent THEN places order and emits NavigateToOrderTracking effect`() = runTest(testDispatcher) {
        cartFlow.value = sampleCart
        coEvery { orderRepository.placeOrder(any(), any(), any(), any()) } returns AppResult.Success(sampleOrder)

        val viewModel = CheckoutViewModel(cartRepository, orderRepository)
        testScheduler.advanceUntilIdle()

        viewModel.uiEffect.test {
            viewModel.handleIntent(CheckoutUiIntent.ConfirmOrder)
            testScheduler.advanceUntilIdle()

            val effect = awaitItem()
            assertThat(effect).isEqualTo(CheckoutUiEffect.NavigateToOrderTracking("ord_123"))
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { orderRepository.placeOrder(any(), any(), any(), any()) }
    }

    @Test
    fun `GIVEN empty cart WHEN ConfirmOrder intent sent THEN emits ShowSnackbar effect`() = runTest(testDispatcher) {
        cartFlow.value = Cart()
        val viewModel = CheckoutViewModel(cartRepository, orderRepository)
        testScheduler.advanceUntilIdle()

        viewModel.uiEffect.test {
            viewModel.handleIntent(CheckoutUiIntent.ConfirmOrder)
            testScheduler.advanceUntilIdle()

            val effect = awaitItem()
            assertThat(effect).isInstanceOf(CheckoutUiEffect.ShowSnackbar::class.java)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 0) { orderRepository.placeOrder(any(), any(), any(), any()) }
    }
    @Test
    fun `coupon from navigation is included in the submitted order`() = runTest(testDispatcher) {
        cartFlow.value = sampleCart
        coEvery { orderRepository.placeOrder(any(), any(), any(), any()) } returns AppResult.Success(sampleOrder)
        val viewModel = CheckoutViewModel(cartRepository, orderRepository,
            androidx.lifecycle.SavedStateHandle(mapOf("discountCents" to 1000L)))
        testScheduler.advanceUntilIdle()
        assertThat(viewModel.uiState.value.finalTotal).isEqualTo(Money(2000L))
        viewModel.handleIntent(CheckoutUiIntent.ConfirmOrder)
        testScheduler.advanceUntilIdle()
        coVerify(exactly = 1) { orderRepository.placeOrder(sampleCart, any(), any(), Money(1000L)) }
    }

    @Test
    fun `repeated confirmations while submitting and after success place only one order`() = runTest(testDispatcher) {
        cartFlow.value = sampleCart
        coEvery { orderRepository.placeOrder(any(), any(), any(), any()) } coAnswers {
            kotlinx.coroutines.delay(100)
            AppResult.Success(sampleOrder)
        }
        val viewModel = CheckoutViewModel(cartRepository, orderRepository)
        testScheduler.advanceUntilIdle()
        repeat(2) { viewModel.handleIntent(CheckoutUiIntent.ConfirmOrder) }
        testScheduler.advanceUntilIdle()
        viewModel.handleIntent(CheckoutUiIntent.ConfirmOrder)
        testScheduler.advanceUntilIdle()
        coVerify(exactly = 1) { orderRepository.placeOrder(any(), any(), any(), any()) }
    }

    @Test
    fun `persistence error resets submitting state and permits retry`() = runTest(testDispatcher) {
        cartFlow.value = sampleCart
        coEvery { orderRepository.placeOrder(any(), any(), any(), any()) } returns
            AppResult.Error(com.samuelbaldasso.ifoodclone.core.domain.model.OrderError.Persistence(IllegalStateException("disk")))
        val viewModel = CheckoutViewModel(cartRepository, orderRepository)
        testScheduler.advanceUntilIdle()
        viewModel.handleIntent(CheckoutUiIntent.ConfirmOrder)
        testScheduler.advanceUntilIdle()
        assertThat(viewModel.uiState.value.isSubmitting).isFalse()
        assertThat(viewModel.uiState.value.errorMessage).isNotNull()
        coEvery { orderRepository.placeOrder(any(), any(), any(), any()) } returns AppResult.Success(sampleOrder)
        viewModel.handleIntent(CheckoutUiIntent.ConfirmOrder)
        testScheduler.advanceUntilIdle()
        assertThat(viewModel.uiState.value.placedOrder).isEqualTo(sampleOrder)
    }

}
