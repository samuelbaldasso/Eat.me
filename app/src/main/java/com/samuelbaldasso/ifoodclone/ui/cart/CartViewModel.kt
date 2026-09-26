package com.samuelbaldasso.ifoodclone.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samuelbaldasso.ifoodclone.core.domain.model.Money
import com.samuelbaldasso.ifoodclone.core.domain.repository.CartRepository
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
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState(isLoading = true))
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<CartUiEffect>(Channel.BUFFERED)
    val uiEffect = _uiEffect.receiveAsFlow()

    init {
        observeCart()
    }

    private fun observeCart() {
        viewModelScope.launch {
            cartRepository.getCart().collect { cart ->
                _uiState.update { state ->
                    state.copy(
                        cart = cart,
                        isLoading = false,
                        // Reset coupon if cart becomes empty
                        discount = if (cart.isEmpty) Money.ZERO else state.discount,
                        isCouponApplied = if (cart.isEmpty) false else state.isCouponApplied
                    )
                }
            }
        }
    }

    fun handleIntent(intent: CartUiIntent) {
        when (intent) {
            is CartUiIntent.UpdateQuantity -> onUpdateQuantity(intent.cartItemId, intent.newQuantity)
            is CartUiIntent.RemoveItem -> onRemoveItem(intent.cartItemId)
            is CartUiIntent.ClearCart -> onClearCart()
            is CartUiIntent.SetCouponCode -> _uiState.update { it.copy(couponCode = intent.code, couponError = null) }
            is CartUiIntent.ApplyCoupon -> onApplyCoupon()
            is CartUiIntent.RemoveCoupon -> onRemoveCoupon()
        }
    }

    private fun onUpdateQuantity(cartItemId: String, newQuantity: Int) {
        viewModelScope.launch {
            cartRepository.updateItemQuantity(cartItemId, newQuantity)
        }
    }

    private fun onRemoveItem(cartItemId: String) {
        viewModelScope.launch {
            cartRepository.removeItem(cartItemId)
            _uiEffect.send(CartUiEffect.ShowSnackbar("Item removido da sacola"))
        }
    }

    private fun onClearCart() {
        viewModelScope.launch {
            cartRepository.clearCart()
            _uiEffect.send(CartUiEffect.ShowSnackbar("Sacola esvaziada"))
        }
    }

    private fun onApplyCoupon() {
        val code = _uiState.value.couponCode.trim().uppercase()
        when (code) {
            "PURPLE10", "EATME10" -> {
                _uiState.update {
                    it.copy(
                        discount = Money(1000L),
                        isCouponApplied = true,
                        couponError = null
                    )
                }
                viewModelScope.launch {
                    _uiEffect.send(CartUiEffect.ShowSnackbar("Cupom de R$ 10 aplicado com sucesso!"))
                }
            }
            "PURPLE15" -> {
                _uiState.update {
                    it.copy(
                        discount = Money(1500L),
                        isCouponApplied = true,
                        couponError = null
                    )
                }
                viewModelScope.launch {
                    _uiEffect.send(CartUiEffect.ShowSnackbar("Cupom de R$ 15 aplicado com sucesso!"))
                }
            }
            else -> {
                _uiState.update {
                    it.copy(couponError = "Cupom inválido. Tente 'PURPLE10' ou 'PURPLE15'")
                }
            }
        }
    }

    private fun onRemoveCoupon() {
        _uiState.update {
            it.copy(
                couponCode = "",
                discount = Money.ZERO,
                isCouponApplied = false,
                couponError = null
            )
        }
    }
}
