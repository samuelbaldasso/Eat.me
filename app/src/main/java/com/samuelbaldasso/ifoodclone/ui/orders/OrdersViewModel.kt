package com.samuelbaldasso.ifoodclone.ui.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samuelbaldasso.ifoodclone.core.domain.model.Order
import com.samuelbaldasso.ifoodclone.core.domain.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OrdersUiState(
    val activeOrders: List<Order> = emptyList(),
    val pastOrders: List<Order> = emptyList(),
    val isLoading: Boolean = true
) {
    val isEmpty: Boolean get() = activeOrders.isEmpty() && pastOrders.isEmpty()
}

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrdersUiState())
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()

    init {
        observeOrders()
    }

    private fun observeOrders() {
        viewModelScope.launch {
            orderRepository.getOrders().collect { orders ->
                val (active, past) = orders.partition { it.isActive }
                _uiState.update {
                    it.copy(
                        activeOrders = active,
                        pastOrders = past,
                        isLoading = false
                    )
                }
            }
        }
    }
}
