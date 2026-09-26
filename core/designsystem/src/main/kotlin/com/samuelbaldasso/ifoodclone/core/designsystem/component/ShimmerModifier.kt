package com.samuelbaldasso.ifoodclone.core.designsystem.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * High-performance animated shimmer modifier for loading skeleton states.
 */
fun Modifier.shimmer(
    baseColor: Color? = null,
    highlightColor: Color? = null,
    durationMillis: Int = 1200
): Modifier = composed {
    val base = baseColor ?: MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    val highlight = highlightColor ?: MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)

    val transition = rememberInfiniteTransition(label = "shimmerTransition")
    val translateAnim by transition.animateFloat(
        initialValue = -600f,
        targetValue = 1800f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val brush = Brush.linearGradient(
        colors = listOf(base, highlight, base),
        start = Offset(x = translateAnim - 600f, y = translateAnim - 600f),
        end = Offset(x = translateAnim, y = translateAnim)
    )

    background(brush = brush)
}

/**
 * Reusable skeleton placeholder box with rounded corners and animated shimmer.
 */
@Composable
fun ShimmerPlaceholder(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp),
    baseColor: Color? = null,
    highlightColor: Color? = null
) {
    Box(
        modifier = modifier
            .clip(shape)
            .shimmer(baseColor = baseColor, highlightColor = highlightColor)
    )
}
