package com.samuelbaldasso.ifoodclone.ui.profile

import com.google.common.truth.Truth.assertThat
import com.samuelbaldasso.ifoodclone.core.domain.model.Money
import org.junit.Test

class ProfileViewModelTest {

    @Test
    fun `GIVEN initial state WHEN ToggleNotifications intent sent THEN inverts flag`() {
        val viewModel = ProfileViewModel()
        assertThat(viewModel.uiState.value.isNotificationsEnabled).isTrue()

        viewModel.handleIntent(ProfileUiIntent.ToggleNotifications)
        assertThat(viewModel.uiState.value.isNotificationsEnabled).isFalse()

        viewModel.handleIntent(ProfileUiIntent.ToggleNotifications)
        assertThat(viewModel.uiState.value.isNotificationsEnabled).isTrue()
    }

    @Test
    fun `GIVEN initial state WHEN ToggleBiometrics intent sent THEN inverts flag`() {
        val viewModel = ProfileViewModel()
        assertThat(viewModel.uiState.value.isBiometricsEnabled).isTrue()

        viewModel.handleIntent(ProfileUiIntent.ToggleBiometrics)
        assertThat(viewModel.uiState.value.isBiometricsEnabled).isFalse()
    }

    @Test
    fun `GIVEN initial balance WHEN AddWalletBalance intent sent THEN increases balance correctly`() {
        val viewModel = ProfileViewModel()
        val initialBalance = viewModel.uiState.value.walletBalance

        viewModel.handleIntent(ProfileUiIntent.AddWalletBalance(Money(5000L)))
        assertThat(viewModel.uiState.value.walletBalance).isEqualTo(initialBalance + Money(5000L))
    }
}
