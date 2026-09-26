package com.samuelbaldasso.ifoodclone.ui.restaurant

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.samuelbaldasso.ifoodclone.core.domain.model.AppError
import com.samuelbaldasso.ifoodclone.core.domain.model.AppResult
import com.samuelbaldasso.ifoodclone.core.domain.model.Dish
import com.samuelbaldasso.ifoodclone.core.domain.model.MenuSection
import com.samuelbaldasso.ifoodclone.core.domain.model.Money
import com.samuelbaldasso.ifoodclone.core.domain.model.Option
import com.samuelbaldasso.ifoodclone.core.domain.model.OptionGroup
import com.samuelbaldasso.ifoodclone.core.domain.model.Restaurant
import com.samuelbaldasso.ifoodclone.core.domain.model.RestaurantDetails
import com.samuelbaldasso.ifoodclone.core.domain.repository.RestaurantRepository
import com.samuelbaldasso.ifoodclone.core.domain.usecase.CalculateDishPriceUseCase
import com.samuelbaldasso.ifoodclone.core.domain.usecase.ValidateDishSelectionUseCase
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
class RestaurantDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: RestaurantRepository = mockk()
    private val cartRepository: com.samuelbaldasso.ifoodclone.core.domain.repository.CartRepository = mockk()
    private val validateDishSelectionUseCase = ValidateDishSelectionUseCase()
    private val calculateDishPriceUseCase = CalculateDishPriceUseCase()

    private val sampleDish = Dish(
        id = "dish_1",
        restaurantId = "rest_1",
        name = "Whopper Test",
        description = "Delicioso hambúrguer",
        basePrice = Money(3000L),
        promoPrice = Money(2500L),
        isAvailable = true,
        optionGroups = listOf(
            OptionGroup(
                id = "grp_point",
                title = "Ponto da carne",
                minSelect = 1,
                maxSelect = 1,
                isRequired = true,
                options = listOf(
                    Option("opt_point_medium", "Ao ponto", Money.ZERO),
                    Option("opt_point_well", "Bem passado", Money.ZERO)
                )
            ),
            OptionGroup(
                id = "grp_extras",
                title = "Adicionais",
                minSelect = 0,
                maxSelect = 2,
                isRequired = false,
                options = listOf(
                    Option("opt_bacon", "Bacon crocante", Money(500L)),
                    Option("opt_cheese", "Queijo extra", Money(300L))
                )
            )
        )
    )

    private val sampleDetails = RestaurantDetails(
        restaurant = Restaurant(
            id = "rest_1",
            name = "Burger King Test",
            category = "Lanches",
            rating = 4.7,
            ratingCount = 500,
            deliveryTimeRange = "30-40 min",
            deliveryFee = Money.ZERO,
            distanceKm = 2.5,
            imageUrl = "http://example.com/logo.png"
        ),
        description = "Hambúrgueres grelhados no fogo.",
        address = "Av. Paulista, 1000",
        menuSections = listOf(
            MenuSection(
                id = "sec_1",
                name = "Burgers",
                dishes = listOf(sampleDish)
            )
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { repository.getRestaurantDetails("rest_1") } returns AppResult.Success(sampleDetails)
        io.mockk.every { cartRepository.getCart() } returns kotlinx.coroutines.flow.flowOf(com.samuelbaldasso.ifoodclone.core.domain.model.Cart())
        coEvery { cartRepository.addToCart(any(), any(), any(), any(), any()) } returns AppResult.Success(Unit)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `GIVEN repository returns details WHEN initialized THEN loads and exposes restaurant details`() = runTest(testDispatcher) {
        val savedStateHandle = SavedStateHandle(mapOf("restaurantId" to "rest_1"))
        val viewModel = RestaurantDetailViewModel(
            restaurantRepository = repository,
            cartRepository = cartRepository,
            validateDishSelectionUseCase = validateDishSelectionUseCase,
            calculateDishPriceUseCase = calculateDishPriceUseCase,
            savedStateHandle = savedStateHandle
        )
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.restaurantDetails).isEqualTo(sampleDetails)
        assertThat(state.selectedSectionIndex).isEqualTo(0)
    }

    @Test
    fun `GIVEN repository error WHEN initialized THEN exposes error message`() = runTest(testDispatcher) {
        coEvery { repository.getRestaurantDetails("rest_1") } returns AppResult.Error(AppError.NotFound())

        val savedStateHandle = SavedStateHandle(mapOf("restaurantId" to "rest_1"))
        val viewModel = RestaurantDetailViewModel(
            restaurantRepository = repository,
            cartRepository = cartRepository,
            validateDishSelectionUseCase = validateDishSelectionUseCase,
            calculateDishPriceUseCase = calculateDishPriceUseCase,
            savedStateHandle = savedStateHandle
        )
        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.restaurantDetails).isNull()
        assertThat(state.errorMessage).isNotNull()
    }

    @Test
    fun `GIVEN details loaded WHEN OpenDishCustomization intent THEN initializes customization and preselects required single-choice`() = runTest(testDispatcher) {
        val savedStateHandle = SavedStateHandle(mapOf("restaurantId" to "rest_1"))
        val viewModel = RestaurantDetailViewModel(
            restaurantRepository = repository,
            cartRepository = cartRepository,
            validateDishSelectionUseCase = validateDishSelectionUseCase,
            calculateDishPriceUseCase = calculateDishPriceUseCase,
            savedStateHandle = savedStateHandle
        )
        testScheduler.advanceUntilIdle()

        viewModel.handleIntent(RestaurantDetailIntent.OpenDishCustomization(sampleDish))

        val state = viewModel.uiState.value
        val customization = state.customizationState
        assertThat(customization).isNotNull()
        assertThat(customization?.dish).isEqualTo(sampleDish)
        assertThat(customization?.quantity).isEqualTo(1)
        // Promo price of sampleDish is 2500L
        assertThat(customization?.totalPrice).isEqualTo(Money(2500L))
        // Required single choice group pre-selected first option
        assertThat(customization?.selectedOptionsByGroup?.get("grp_point")).contains("opt_point_medium")
        assertThat(customization?.isValid).isTrue()
    }

    @Test
    fun `GIVEN dish customization open WHEN extra option toggled THEN updates price dynamically`() = runTest(testDispatcher) {
        val savedStateHandle = SavedStateHandle(mapOf("restaurantId" to "rest_1"))
        val viewModel = RestaurantDetailViewModel(
            restaurantRepository = repository,
            cartRepository = cartRepository,
            validateDishSelectionUseCase = validateDishSelectionUseCase,
            calculateDishPriceUseCase = calculateDishPriceUseCase,
            savedStateHandle = savedStateHandle
        )
        testScheduler.advanceUntilIdle()

        viewModel.handleIntent(RestaurantDetailIntent.OpenDishCustomization(sampleDish))
        // Toggle bacon (+500L)
        viewModel.handleIntent(RestaurantDetailIntent.ToggleOption("grp_extras", "opt_bacon"))

        val state = viewModel.uiState.value
        val customization = state.customizationState
        assertThat(customization?.selectedOptionsByGroup?.get("grp_extras")).contains("opt_bacon")
        // 2500L + 500L = 3000L
        assertThat(customization?.totalPrice).isEqualTo(Money(3000L))
    }

    @Test
    fun `GIVEN dish customization open WHEN quantity changed THEN updates price and respects bounds`() = runTest(testDispatcher) {
        val savedStateHandle = SavedStateHandle(mapOf("restaurantId" to "rest_1"))
        val viewModel = RestaurantDetailViewModel(
            restaurantRepository = repository,
            cartRepository = cartRepository,
            validateDishSelectionUseCase = validateDishSelectionUseCase,
            calculateDishPriceUseCase = calculateDishPriceUseCase,
            savedStateHandle = savedStateHandle
        )
        testScheduler.advanceUntilIdle()

        viewModel.handleIntent(RestaurantDetailIntent.OpenDishCustomization(sampleDish))
        // Change quantity to 3
        viewModel.handleIntent(RestaurantDetailIntent.ChangeQuantity(3))

        val state = viewModel.uiState.value
        val customization = state.customizationState
        assertThat(customization?.quantity).isEqualTo(3)
        // 2500L * 3 = 7500L
        assertThat(customization?.totalPrice).isEqualTo(Money(7500L))

        // Change quantity below min (0 -> clamped to 1)
        viewModel.handleIntent(RestaurantDetailIntent.ChangeQuantity(0))
        assertThat(viewModel.uiState.value.customizationState?.quantity).isEqualTo(1)

        // Change quantity above max (25 -> clamped to 20)
        viewModel.handleIntent(RestaurantDetailIntent.ChangeQuantity(25))
        assertThat(viewModel.uiState.value.customizationState?.quantity).isEqualTo(20)
    }

    @Test
    fun `GIVEN valid customization WHEN ConfirmAddToCart intent THEN emits AddedToCart effect and closes customization`() = runTest(testDispatcher) {
        val savedStateHandle = SavedStateHandle(mapOf("restaurantId" to "rest_1"))
        val viewModel = RestaurantDetailViewModel(
            restaurantRepository = repository,
            cartRepository = cartRepository,
            validateDishSelectionUseCase = validateDishSelectionUseCase,
            calculateDishPriceUseCase = calculateDishPriceUseCase,
            savedStateHandle = savedStateHandle
        )
        testScheduler.advanceUntilIdle()

        viewModel.handleIntent(RestaurantDetailIntent.OpenDishCustomization(sampleDish))

        viewModel.uiEffect.test {
            viewModel.handleIntent(RestaurantDetailIntent.ConfirmAddToCart)

            val addedEffect = awaitItem()
            assertThat(addedEffect).isInstanceOf(RestaurantDetailEffect.AddedToCart::class.java)
            val added = addedEffect as RestaurantDetailEffect.AddedToCart
            assertThat(added.dishName).isEqualTo("Whopper Test")
            assertThat(added.quantity).isEqualTo(1)
            assertThat(added.totalPrice).isEqualTo(Money(2500L))

            val snackbarEffect = awaitItem()
            assertThat(snackbarEffect).isInstanceOf(RestaurantDetailEffect.ShowSnackbar::class.java)

            cancelAndIgnoreRemainingEvents()
        }

        // Sheet closed
        assertThat(viewModel.uiState.value.customizationState).isNull()
    }
}
