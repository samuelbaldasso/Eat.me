package com.samuelbaldasso.ifoodclone.ui.profile

import androidx.lifecycle.ViewModel
import com.samuelbaldasso.ifoodclone.core.domain.model.Money
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class ProfileUiState(
    val userName: String = "Samuel Baldasso",
    val userEmail: String = "cliente@eatme.com.br",
    val userPhone: String = "(11) 98765-4321",
    val walletBalance: Money = Money(15000L), // R$ 150,00
    val defaultAddress: String = "Rua dos Desenvolvedores, 1234 • Jardins",
    val activeCouponsCount: Int = 3,
    val isNotificationsEnabled: Boolean = true,
    val isBiometricsEnabled: Boolean = true
)

sealed interface ProfileUiIntent {
    data object ToggleNotifications : ProfileUiIntent
    data object ToggleBiometrics : ProfileUiIntent
    data class AddWalletBalance(val amount: Money) : ProfileUiIntent
}

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun handleIntent(intent: ProfileUiIntent) {
        when (intent) {
            is ProfileUiIntent.ToggleNotifications -> {
                _uiState.update { it.copy(isNotificationsEnabled = !it.isNotificationsEnabled) }
            }
            is ProfileUiIntent.ToggleBiometrics -> {
                _uiState.update { it.copy(isBiometricsEnabled = !it.isBiometricsEnabled) }
            }
            is ProfileUiIntent.AddWalletBalance -> {
                _uiState.update { it.copy(walletBalance = it.walletBalance + intent.amount) }
            }
        }
    }
}
