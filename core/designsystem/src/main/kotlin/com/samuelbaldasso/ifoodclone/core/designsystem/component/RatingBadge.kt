package com.samuelbaldasso.ifoodclone.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samuelbaldasso.ifoodclone.core.designsystem.theme.EatMeRatingStar
import com.samuelbaldasso.ifoodclone.core.designsystem.theme.EatMeTheme
import java.util.Locale

@Composable
fun RatingBadge(
    rating: Double,
    modifier: Modifier = Modifier,
    ratingCount: Int? = null
) {
    val formattedRating = String.format(Locale.forLanguageTag("pt-BR"), "%.1f", rating)
    val accessibilityLabel = if (ratingCount != null) {
        "Avaliação $formattedRating estrelas de $ratingCount avaliações"
    } else {
        "Avaliação $formattedRating estrelas"
    }

    Row(
        modifier = modifier.semantics {
            contentDescription = accessibilityLabel
        },
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "★",
            style = MaterialTheme.typography.bodyMedium,
            color = EatMeRatingStar,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = formattedRating,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (ratingCount != null) {
            Text(
                text = "($ratingCount)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(name = "Rating - Without count", showBackground = true)
@Composable
private fun RatingBadgePreview() {
    EatMeTheme {
        RatingBadge(rating = 4.8)
    }
}

@Preview(name = "Rating - With count", showBackground = true)
@Composable
private fun RatingBadgeWithCountPreview() {
    EatMeTheme {
        RatingBadge(rating = 4.6, ratingCount = 340)
    }
}
