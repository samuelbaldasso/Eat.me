package com.samuelbaldasso.ifoodclone.ui.cart

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.samuelbaldasso.ifoodclone.core.domain.model.AppResult
import com.samuelbaldasso.ifoodclone.core.domain.model.Cart
import com.samuelbaldasso.ifoodclone.core.domain.model.CartItem
import com.samuelbaldasso.ifoodclone.core.domain.model.Money
import com.samuelbaldasso.ifoodclone.core.domain.model.Restaurant
import com.samuelbaldasso.ifoodclone.core.domain.repository.CartRepository
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
class CartViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val cartRepository: CartRepository = mockk(relaxed = true)
    private val cartFlow = MutableStateFlow(Cart())

    private val sampleRestaurant = Restaurant(
        id = "rest_1",
        name = "Burger King Test",
        category = "Lanches",
        deliveryFee = Money(500L),
        deliveryTimeRange = "30-40 min",
        rating = 4.5,
        imageUrl = "http://example.com/logo.png"
    )

    private val sampleCart = Cart(
        restaurant = sampleRestaurant,
        items = listOf(
            CartItem(
                id = "item_1",
                restaurantId = "rest_1",
                dishId = "dish_1",
                dishName = "Whopper Test",
                dishImageUrl = "http://example.com/whopper.png",
                unitPrice = Money(2500L),
                quantity = 2
            )
        )
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
    fun `GIVEN cart updates in repository WHEN observed THEN uiState updates with latest cart`() = runTest(testDispatcher) {
        val viewModel = CartViewModel(cartRepository)
        testScheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.value.cart.isEmpty).isTrue()

        cartFlow.value = sampleCart
        testScheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.value.cart.restaurant?.name).isEqualTo("Burger King Test")
        assertThat(viewModel.uiState.value.cart.totalQuantity).isEqualTo(2)
        assertThat(viewModel.uiState.value.cart.subtotal).isEqualTo(Money(5000L))
        assertThat(viewModel.uiState.value.cart.total).isEqualTo(Money(5500L))
    }

    @Test
    fun `GIVEN item in cart WHEN UpdateQuantity intent sent THEN delegates to repository`() = runTest(testDispatcher) {
        coEvery { cartRepository.updateItemQuantity("item_1", 3) } returns AppResult.Success(Unit)

        val viewModel = CartViewModel(cartRepository)
        testScheduler.advanceUntilIdle()

        viewModel.handleIntent(CartUiIntent.UpdateQuantity("item_1", 3))
        testScheduler.advanceUntilIdle()

        coVerify { cartRepository.updateItemQuantity("item_1", 3) }
    }

    @Test
    fun `GIVEN item in cart WHEN RemoveItem intent sent THEN delegates to repository and emits snackbar`() = runTest(testDispatcher) {
        coEvery { cartRepository.removeItem("item_1") } returns AppResult.Success(Unit)

        val viewModel = CartViewModel(cartRepository)
        testScheduler.advanceUntilIdle()

        viewModel.uiEffect.test {
            viewModel.handleIntent(CartUiIntent.RemoveItem("item_1"))
            testScheduler.advanceUntilIdle()

            val effect = awaitItem()
            assertThat(effect).isInstanceOf(CartUiEffect.ShowSnackbar::class.java)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { cartRepository.removeItem("item_1") }
    }

    @Test
    fun `GIVEN valid coupon WHEN ApplyCoupon intent sent THEN applies discount`() = runTest(testDispatcher) {
        cartFlow.value = sampleCart
        val viewModel = CartViewModel(cartRepository)
        testScheduler.advanceUntilIdle()

        viewModel.handleIntent(CartUiIntent.SetCouponCode("EATME10"))
        viewModel.handleIntent(CartUiIntent.ApplyCoupon)
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isCouponApplied).isTrue()
        assertThat(state.discount).isEqualTo(Money(1000L))
        assertThat(state.finalTotal).isEqualTo(Money(4500L))
    }

    @Test
    fun `GIVEN invalid coupon WHEN ApplyCoupon intent sent THEN sets coupon error`() = runTest(testDispatcher) {
        cartFlow.value = sampleCart
        val viewModel = CartViewModel(cartRepository)
        testScheduler.advanceUntilIdle()

        viewModel.handleIntent(CartUiIntent.SetCouponCode("INVALIDO"))
        viewModel.handleIntent(CartUiIntent.ApplyCoupon)
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isCouponApplied).isFalse()
        assertThat(state.couponError).isNotNull()
    }

    @Test
    fun `GIVEN applied coupon WHEN RemoveCoupon intent sent THEN clears discount`() = runTest(testDispatcher) {
        cartFlow.value = sampleCart
        val viewModel = CartViewModel(cartRepository)
        testScheduler.advanceUntilIdle()

        viewModel.handleIntent(CartUiIntent.SetCouponCode("EATME10"))
        viewModel.handleIntent(CartUiIntent.ApplyCoupon)
        testScheduler.advanceUntilIdle()

        viewModel.handleIntent(CartUiIntent.RemoveCoupon)
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isCouponApplied).isFalse()
        assertThat(state.discount).isEqualTo(Money.ZERO)
        assertThat(state.couponCode).isEmpty()
    }
}
