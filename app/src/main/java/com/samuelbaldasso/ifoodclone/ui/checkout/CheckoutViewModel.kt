package com.samuelbaldasso.ifoodclone.ui.checkout

import androidx.lifecycle.SavedStateHandle
import com.samuelbaldasso.ifoodclone.core.domain.model.Money
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samuelbaldasso.ifoodclone.core.domain.model.AppResult
import com.samuelbaldasso.ifoodclone.core.domain.model.OrderAddress
import com.samuelbaldasso.ifoodclone.core.domain.model.PaymentMethod
import com.samuelbaldasso.ifoodclone.core.domain.repository.CartRepository
import com.samuelbaldasso.ifoodclone.core.domain.repository.OrderRepository
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
class CheckoutViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    savedStateHandle: SavedStateHandle = SavedStateHandle()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState(
        discount = Money((savedStateHandle.get<Long>("discountCents") ?: 0L).coerceAtLeast(0L))
    ))
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<CheckoutUiEffect>(Channel.BUFFERED)
    val uiEffect = _uiEffect.receiveAsFlow()

    init {
        observeCart()
    }

    private fun observeCart() {
        viewModelScope.launch {
            cartRepository.getCart().collect { cart ->
                _uiState.update { it.copy(cart = cart) }
            }
        }
    }

    fun handleIntent(intent: CheckoutUiIntent) {
        when (intent) {
            is CheckoutUiIntent.SelectPaymentMethod -> {
                _uiState.update { it.copy(selectedPaymentMethod = intent.method) }
            }
            is CheckoutUiIntent.UpdateChangeForCash -> {
                _uiState.update { it.copy(changeForCash = intent.amount) }
            }
            is CheckoutUiIntent.UpdateCpf -> {
                _uiState.update { it.copy(cpfOnInvoice = intent.cpf) }
            }
            is CheckoutUiIntent.UpdateAddress -> {
                _uiState.update { it.copy(deliveryAddress = intent.address) }
            }
            is CheckoutUiIntent.ConfirmOrder -> onConfirmOrder()
        }
    }

    private fun onConfirmOrder() {
        val currentState = _uiState.value
        if (currentState.isSubmitting || currentState.placedOrder != null) return
        val cart = currentState.cart

        if (cart.isEmpty) {
            viewModelScope.launch {
                _uiEffect.send(CheckoutUiEffect.ShowSnackbar("Sua sacola está vazia."))
            }
            return
        }

        if (!cart.isMinOrderSatisfied) {
            viewModelScope.launch {
                _uiEffect.send(
                    CheckoutUiEffect.ShowSnackbar(
                        "O valor mínimo do pedido é ${cart.minOrderValue.toFormattedBrl()}"
                    )
                )
            }
            return
        }

        _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
        viewModelScope.launch {

            val result = orderRepository.placeOrder(
                cart = cart,
                paymentMethod = currentState.selectedPaymentMethod,
                deliveryAddress = currentState.deliveryAddress,
                discount = currentState.discount
            )

            when (result) {
                is AppResult.Success -> {
                    val order = result.data
                    _uiState.update { it.copy(isSubmitting = false, placedOrder = order) }
                    _uiEffect.send(CheckoutUiEffect.NavigateToOrderTracking(order.id))
                }
                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = "Não foi possível concluir seu pedido. Tente novamente."
                        )
                    }
                    _uiEffect.send(CheckoutUiEffect.ShowSnackbar("Falha ao processar pedido."))
                }
            }
        }
    }
}
