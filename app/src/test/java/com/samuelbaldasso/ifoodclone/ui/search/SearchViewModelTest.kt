package com.samuelbaldasso.ifoodclone.ui.search

import com.google.common.truth.Truth.assertThat
import com.samuelbaldasso.ifoodclone.core.domain.model.AppResult
import com.samuelbaldasso.ifoodclone.core.domain.model.Money
import com.samuelbaldasso.ifoodclone.core.domain.model.Restaurant
import com.samuelbaldasso.ifoodclone.core.domain.repository.RestaurantRepository
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
class SearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val restaurantRepository: RestaurantRepository = mockk()

    private val restaurants = listOf(
        Restaurant(
            id = "1",
            name = "Burger King",
            category = "Lanches",
            deliveryFee = Money.ZERO,
            deliveryTimeRange = "30-40 min",
            rating = 4.8,
            imageUrl = "http://example.com/bk.png"
        ),
        Restaurant(
            id = "2",
            name = "Sushibar Oriental",
            category = "Japonesa",
            deliveryFee = Money(500L),
            deliveryTimeRange = "40-50 min",
            rating = 4.5,
            imageUrl = "http://example.com/sushi.png"
        ),
        Restaurant(
            id = "3",
            name = "Pizzaria Bella",
            category = "Pizza",
            deliveryFee = Money.ZERO,
            deliveryTimeRange = "25-35 min",
            rating = 4.9,
            imageUrl = "http://example.com/pizza.png"
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { restaurantRepository.getRestaurants() } returns AppResult.Success(restaurants)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `GIVEN restaurants loaded WHEN UpdateQuery intent sent THEN filters list by name`() = runTest(testDispatcher) {
        val viewModel = SearchViewModel(restaurantRepository)
        testScheduler.advanceUntilIdle()

        viewModel.handleIntent(SearchUiIntent.UpdateQuery("Burger"))

        val state = viewModel.uiState.value
        assertThat(state.searchResults).hasSize(1)
        assertThat(state.searchResults.first().name).isEqualTo("Burger King")
    }

    @Test
    fun `GIVEN restaurants loaded WHEN SelectTag intent sent THEN filters list by category`() = runTest(testDispatcher) {
        val viewModel = SearchViewModel(restaurantRepository)
        testScheduler.advanceUntilIdle()

        viewModel.handleIntent(SearchUiIntent.SelectTag("Japonesa"))

        val state = viewModel.uiState.value
        assertThat(state.searchResults).hasSize(1)
        assertThat(state.searchResults.first().name).isEqualTo("Sushibar Oriental")
    }

    @Test
    fun `GIVEN restaurants loaded WHEN ToggleFreeDelivery intent sent THEN filters only free delivery restaurants`() = runTest(testDispatcher) {
        val viewModel = SearchViewModel(restaurantRepository)
        testScheduler.advanceUntilIdle()

        viewModel.handleIntent(SearchUiIntent.ToggleFreeDelivery)

        val state = viewModel.uiState.value
        assertThat(state.searchResults).hasSize(2)
        assertThat(state.searchResults.map { it.id }).containsExactly("1", "3")
    }

    @Test
    fun `GIVEN search query active WHEN ClearQuery intent sent THEN resets search results`() = runTest(testDispatcher) {
        val viewModel = SearchViewModel(restaurantRepository)
        testScheduler.advanceUntilIdle()

        viewModel.handleIntent(SearchUiIntent.UpdateQuery("Pizza"))
        assertThat(viewModel.uiState.value.searchResults).isNotEmpty()

        viewModel.handleIntent(SearchUiIntent.ClearQuery)
        assertThat(viewModel.uiState.value.searchResults).isEmpty()
        assertThat(viewModel.uiState.value.query).isEmpty()
    }
}
