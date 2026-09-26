package com.samuelbaldasso.ifoodclone.ui.restaurant

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samuelbaldasso.ifoodclone.core.domain.model.AppResult
import com.samuelbaldasso.ifoodclone.core.domain.model.CartError
import com.samuelbaldasso.ifoodclone.core.domain.model.Dish
import com.samuelbaldasso.ifoodclone.core.domain.model.Option
import com.samuelbaldasso.ifoodclone.core.domain.repository.CartRepository
import com.samuelbaldasso.ifoodclone.core.domain.repository.RestaurantRepository
import com.samuelbaldasso.ifoodclone.core.domain.usecase.CalculateDishPriceUseCase
import com.samuelbaldasso.ifoodclone.core.domain.usecase.DishValidationResult
import com.samuelbaldasso.ifoodclone.core.domain.usecase.ValidateDishSelectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantDetailViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository,
    private val cartRepository: CartRepository,
    private val validateDishSelectionUseCase: ValidateDishSelectionUseCase,
    private val calculateDishPriceUseCase: CalculateDishPriceUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val restaurantId: String = savedStateHandle.get<String>("restaurantId") ?: "1"

    private val _uiState = MutableStateFlow(RestaurantDetailUiState(isLoading = true))
    val uiState: StateFlow<RestaurantDetailUiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<RestaurantDetailEffect>(Channel.BUFFERED)
    val uiEffect = _uiEffect.receiveAsFlow()

    init {
        loadRestaurantDetails(restaurantId)
        observeCart()
    }

    private fun observeCart() {
        viewModelScope.launch {
            cartRepository.getCart().collect { cart ->
                _uiState.update { it.copy(cart = cart) }
            }
        }
    }

    fun handleIntent(intent: RestaurantDetailIntent) {
        when (intent) {
            is RestaurantDetailIntent.LoadDetails -> loadRestaurantDetails(intent.restaurantId)
            is RestaurantDetailIntent.Retry -> loadRestaurantDetails(restaurantId)
            is RestaurantDetailIntent.SelectSection -> onSelectSection(intent.index)
            is RestaurantDetailIntent.OpenDishCustomization -> openCustomization(intent.dish)
            is RestaurantDetailIntent.CloseDishCustomization -> closeCustomization()
            is RestaurantDetailIntent.ToggleOption -> onToggleOption(intent.groupId, intent.optionId)
            is RestaurantDetailIntent.ChangeQuantity -> onChangeQuantity(intent.newQuantity)
            is RestaurantDetailIntent.ConfirmAddToCart -> onConfirmAddToCart(forceClear = false)
            is RestaurantDetailIntent.ConfirmClearCartAndAdd -> onConfirmAddToCart(forceClear = true)
            is RestaurantDetailIntent.DismissDifferentRestaurantDialog -> {
                _uiState.update {
                    it.copy(showDifferentRestaurantDialog = false, pendingDifferentRestaurantError = null)
                }
            }
        }
    }

    private fun loadRestaurantDetails(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = restaurantRepository.getRestaurantDetails(id)) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            restaurantDetails = result.data,
                            selectedSectionIndex = 0
                        )
                    }
                }
                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Não foi possível carregar o cardápio. Toque para tentar novamente."
                        )
                    }
                }
            }
        }
    }

    private fun onSelectSection(index: Int) {
        _uiState.update { it.copy(selectedSectionIndex = index) }
    }

    private fun openCustomization(dish: Dish) {
        val initialSelections = mutableMapOf<String, Set<String>>()
        for (group in dish.optionGroups) {
            if (group.isRequired && group.minSelect == 1 && group.maxSelect == 1 && group.options.isNotEmpty()) {
                val firstAvailable = group.options.firstOrNull { it.isAvailable }
                if (firstAvailable != null) {
                    initialSelections[group.id] = setOf(firstAvailable.id)
                }
            }
        }

        val validation = validateDishSelectionUseCase(dish, initialSelections)
        val isValid = validation is DishValidationResult.Valid
        val errors = if (validation is DishValidationResult.Invalid) validation.groupErrors else emptyMap()

        val selectedOptions = resolveSelectedOptions(dish, initialSelections)
        val initialPrice = calculateDishPriceUseCase(dish, selectedOptions, quantity = 1)

        _uiState.update {
            it.copy(
                customizationState = DishCustomizationState(
                    dish = dish,
                    selectedOptionsByGroup = initialSelections,
                    quantity = 1,
                    totalPrice = initialPrice,
                    isValid = isValid,
                    groupErrors = errors
                )
            )
        }
    }

    private fun closeCustomization() {
        _uiState.update { it.copy(customizationState = null) }
    }

    private fun onToggleOption(groupId: String, optionId: String) {
        val current = _uiState.value.customizationState ?: return
        val dish = current.dish
        val group = dish.optionGroups.find { it.id == groupId } ?: return

        val currentGroupSelected = current.selectedOptionsByGroup[groupId].orEmpty()
        val newGroupSelected: Set<String> = if (group.maxSelect == 1) {
            if (currentGroupSelected.contains(optionId)) {
                if (group.isRequired) currentGroupSelected else emptySet()
            } else {
                setOf(optionId)
            }
        } else {
            if (currentGroupSelected.contains(optionId)) {
                currentGroupSelected - optionId
            } else {
                if (currentGroupSelected.size < group.maxSelect) {
                    currentGroupSelected + optionId
                } else {
                    currentGroupSelected
                }
            }
        }

        val updatedMap = current.selectedOptionsByGroup.toMutableMap()
        if (newGroupSelected.isEmpty()) {
            updatedMap.remove(groupId)
        } else {
            updatedMap[groupId] = newGroupSelected
        }

        val validation = validateDishSelectionUseCase(dish, updatedMap)
        val isValid = validation is DishValidationResult.Valid
        val errors = if (validation is DishValidationResult.Invalid) validation.groupErrors else emptyMap()

        val selectedOptions = resolveSelectedOptions(dish, updatedMap)
        val newPrice = calculateDishPriceUseCase(dish, selectedOptions, current.quantity)

        _uiState.update {
            it.copy(
                customizationState = current.copy(
                    selectedOptionsByGroup = updatedMap,
                    totalPrice = newPrice,
                    isValid = isValid,
                    groupErrors = errors
                )
            )
        }
    }

    private fun onChangeQuantity(newQuantity: Int) {
        val current = _uiState.value.customizationState ?: return
        val clampedQty = newQuantity.coerceIn(1, 20)
        val selectedOptions = resolveSelectedOptions(current.dish, current.selectedOptionsByGroup)
        val newPrice = calculateDishPriceUseCase(current.dish, selectedOptions, clampedQty)

        _uiState.update {
            it.copy(
                customizationState = current.copy(
                    quantity = clampedQty,
                    totalPrice = newPrice
                )
            )
        }
    }

    private fun onConfirmAddToCart(forceClear: Boolean) {
        val current = _uiState.value.customizationState ?: return
        val restaurant = _uiState.value.restaurantDetails?.restaurant ?: return

        if (!current.isValid) {
            viewModelScope.launch {
                _uiEffect.send(RestaurantDetailEffect.ShowSnackbar("Por favor, preencha as opções obrigatórias."))
            }
            return
        }

        val selectedOptions = resolveSelectedOptions(current.dish, current.selectedOptionsByGroup)

        viewModelScope.launch {
            val result = cartRepository.addToCart(
                restaurant = restaurant,
                dish = current.dish,
                selectedOptions = selectedOptions,
                quantity = current.quantity,
                forceClearIfDifferentRestaurant = forceClear
            )

            when (result) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            customizationState = null,
                            showDifferentRestaurantDialog = false,
                            pendingDifferentRestaurantError = null
                        )
                    }
                    _uiEffect.send(
                        RestaurantDetailEffect.AddedToCart(
                            dishName = current.dish.name,
                            quantity = current.quantity,
                            totalPrice = current.totalPrice
                        )
                    )
                    _uiEffect.send(
                        RestaurantDetailEffect.ShowSnackbar(
                            "${current.quantity}x ${current.dish.name} adicionado à sacola!"
                        )
                    )
                }
                is AppResult.Error -> {
                    val error = result.error
                    if (error is CartError.DifferentRestaurant) {
                        _uiState.update {
                            it.copy(
                                showDifferentRestaurantDialog = true,
                                pendingDifferentRestaurantError = error
                            )
                        }
                    } else if (error is CartError.MaxQuantityExceeded) {
                        _uiEffect.send(
                            RestaurantDetailEffect.ShowSnackbar("Limite de 20 unidades por item atingido.")
                        )
                    } else {
                        _uiEffect.send(
                            RestaurantDetailEffect.ShowSnackbar("Erro ao adicionar item à sacola.")
                        )
                    }
                }
            }
        }
    }

    private fun resolveSelectedOptions(
        dish: Dish,
        selectedMap: Map<String, Set<String>>
    ): List<Option> {
        val allOptions = dish.optionGroups.flatMap { it.options }.associateBy { it.id }
        return selectedMap.values.flatten().mapNotNull { allOptions[it] }
    }
}
