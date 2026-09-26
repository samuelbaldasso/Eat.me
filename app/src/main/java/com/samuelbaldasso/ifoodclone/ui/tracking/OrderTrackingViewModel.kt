package com.samuelbaldasso.ifoodclone.ui.tracking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samuelbaldasso.ifoodclone.core.domain.model.AppResult
import com.samuelbaldasso.ifoodclone.core.domain.model.OrderStatus
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
class OrderTrackingViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val orderId: String = checkNotNull(savedStateHandle["orderId"])

    private val _uiState = MutableStateFlow(OrderTrackingUiState())
    val uiState: StateFlow<OrderTrackingUiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<OrderTrackingEffect>(Channel.BUFFERED)
    val uiEffect = _uiEffect.receiveAsFlow()

    init {
        loadOrder()
    }

    private fun loadOrder() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            orderRepository.getOrderById(orderId).collect { order ->
                if (order != null) {
                    val canCancel = order.status == OrderStatus.CONFIRMED || order.status == OrderStatus.PREPARING
                    _uiState.update {
                        it.copy(
                            order = order,
                            isLoading = false,
                            canCancel = canCancel
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Pedido não encontrado."
                        )
                    }
                }
            }
        }
    }

    fun handleIntent(intent: OrderTrackingIntent) {
        when (intent) {
            is OrderTrackingIntent.AdvanceSimulationStatus -> onAdvanceStatus()
            is OrderTrackingIntent.CancelOrder -> onCancelOrder()
        }
    }

    private fun onAdvanceStatus() {
        val currentOrder = _uiState.value.order ?: return
        val nextStatus = when (currentOrder.status) {
            OrderStatus.PLACED -> OrderStatus.CONFIRMED
            OrderStatus.CONFIRMED -> OrderStatus.PREPARING
            OrderStatus.PREPARING -> OrderStatus.OUT_FOR_DELIVERY
            OrderStatus.OUT_FOR_DELIVERY -> OrderStatus.DELIVERED
            OrderStatus.DELIVERED, OrderStatus.CANCELLED -> return
        }

        viewModelScope.launch {
            orderRepository.updateOrderStatus(currentOrder.id, nextStatus)
        }
    }

    private fun onCancelOrder() {
        val currentOrder = _uiState.value.order ?: return
        viewModelScope.launch {
            when (orderRepository.cancelOrder(currentOrder.id)) {
                is AppResult.Success -> {
                    _uiEffect.send(OrderTrackingEffect.ShowSnackbar("Pedido cancelado com sucesso."))
                }
                is AppResult.Error -> {
                    _uiEffect.send(OrderTrackingEffect.ShowSnackbar("Não foi possível cancelar o pedido."))
                }
            }
        }
    }
}
