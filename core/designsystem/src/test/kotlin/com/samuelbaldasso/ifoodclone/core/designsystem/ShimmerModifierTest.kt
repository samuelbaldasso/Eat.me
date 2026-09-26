package com.samuelbaldasso.ifoodclone.core.designsystem

import androidx.compose.ui.Modifier
import com.google.common.truth.Truth.assertThat
import com.samuelbaldasso.ifoodclone.core.designsystem.component.shimmer
import org.junit.Test

class ShimmerModifierTest {

    @Test
    fun testShimmerModifierChaining() {
        val initialModifier: Modifier = Modifier
        val modified = initialModifier.shimmer()

        assertThat(modified).isNotNull()
        assertThat(modified).isNotSameInstanceAs(initialModifier)
    }

    @Test
    fun testShimmerModifierCustomDuration() {
        val modified = Modifier.shimmer(durationMillis = 800)
        assertThat(modified).isNotNull()
    }
}
