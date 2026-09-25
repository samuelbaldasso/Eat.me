package com.samuelbaldasso.ifoodclone.ui.home

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.samuelbaldasso.ifoodclone.data.repository.RestaurantRepository
import com.samuelbaldasso.ifoodclone.domain.restaurant.Restaurant
import com.samuelbaldasso.ifoodclone.ui.theme.composables.home.HomeUiEffect
import com.samuelbaldasso.ifoodclone.ui.theme.composables.home.HomeUiIntent
import com.samuelbaldasso.ifoodclone.ui.theme.composables.home.HomeViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: RestaurantRepository = mockk()

    private val fakeRestaurants = listOf(
        Restaurant(
            id = "1",
            name = "Burger King",
            category = "Lanches",
            deliveryFee = "Grátis",
            deliveryTime = "30-40 min",
            rating = 4.5,
            imageUrl = "http://example.com/bk.png"
        ),
        Restaurant(
            id = "2",
            name = "Sushibar",
            category = "Japonesa",
            deliveryFee = "R$ 5,00",
            deliveryTime = "50-60 min",
            rating = 4.8,
            imageUrl = "http://example.com/sushi.png"
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { repository.getRestaurants() } returns fakeRestaurants
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `GIVEN repository returns restaurants WHEN initialized THEN loads and displays all restaurants`() = runTest(testDispatcher) {
        val viewModel = HomeViewModel(repository)
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.restaurants).hasSize(2)
        assertThat(state.filteredRestaurants).hasSize(2)
    }

    @Test
    fun `GIVEN category selected WHEN SelectCategory intent sent THEN filters list`() = runTest(testDispatcher) {
        val viewModel = HomeViewModel(repository)
        testScheduler.advanceUntilIdle()

        viewModel.handleIntent(HomeUiIntent.SelectCategory("Lanches"))

        val state = viewModel.uiState.value
        assertThat(state.selectedCategory).isEqualTo("Lanches")
        assertThat(state.filteredRestaurants).hasSize(1)
        assertThat(state.filteredRestaurants.first().name).isEqualTo("Burger King")
    }

    @Test
    fun `GIVEN restaurant clicked WHEN RestaurantClick intent sent THEN emits NavigateToRestaurant effect`() = runTest(testDispatcher) {
        val viewModel = HomeViewModel(repository)
        testScheduler.advanceUntilIdle()

        viewModel.uiEffect.test {
            viewModel.handleIntent(HomeUiIntent.RestaurantClick("123"))
            val effect = awaitItem()
            assertThat(effect).isEqualTo(HomeUiEffect.NavigateToRestaurant("123"))
            cancelAndIgnoreRemainingEvents()
        }
    }
}
