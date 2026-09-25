package com.samuelbaldasso.ifoodclone.core.designsystem

import androidx.compose.ui.unit.dp
import com.google.common.truth.Truth.assertThat
import com.samuelbaldasso.ifoodclone.core.designsystem.theme.Spacing
import org.junit.Test

class SpacingTest {

    @Test
    fun testSpacingTokensAscendingOrder() {
        val spacing = Spacing()
        assertThat(spacing.none).isEqualTo(0.dp)
        assertThat(spacing.extraSmall).isEqualTo(4.dp)
        assertThat(spacing.small).isEqualTo(8.dp)
        assertThat(spacing.medium).isEqualTo(16.dp)
        assertThat(spacing.large).isEqualTo(24.dp)
        assertThat(spacing.extraLarge).isEqualTo(32.dp)
        assertThat(spacing.huge).isEqualTo(48.dp)

        assertThat(spacing.extraSmall).isLessThan(spacing.small)
        assertThat(spacing.small).isLessThan(spacing.medium)
        assertThat(spacing.medium).isLessThan(spacing.large)
        assertThat(spacing.large).isLessThan(spacing.extraLarge)
        assertThat(spacing.extraLarge).isLessThan(spacing.huge)
    }
}
