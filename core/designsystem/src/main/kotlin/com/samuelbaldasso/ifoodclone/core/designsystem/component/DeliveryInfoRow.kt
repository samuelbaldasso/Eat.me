package com.samuelbaldasso.ifoodclone.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samuelbaldasso.ifoodclone.core.designsystem.theme.EatMeTheme
import com.samuelbaldasso.ifoodclone.core.domain.model.Money
import java.util.Locale

@Composable
fun DeliveryInfoRow(
    deliveryTimeRange: String,
    deliveryFee: Money,
    modifier: Modifier = Modifier,
    distanceKm: Double? = null
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = deliveryTimeRange,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = "•",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (deliveryFee.isZero) {
            Text(
                text = "Grátis",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Bold
            )
        } else {
            Text(
                text = deliveryFee.formatBrl(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (distanceKm != null) {
            Text(
                text = "•",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = String.format(Locale.forLanguageTag("pt-BR"), "%.1f km", distanceKm),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(name = "Delivery - Free", showBackground = true)
@Composable
private fun DeliveryInfoRowFreePreview() {
    EatMeTheme {
        DeliveryInfoRow(
            deliveryTimeRange = "25-35 min",
            deliveryFee = Money.ZERO,
            distanceKm = 1.8
        )
    }
}

@Preview(name = "Delivery - Paid", showBackground = true)
@Composable
private fun DeliveryInfoRowPaidPreview() {
    EatMeTheme {
        DeliveryInfoRow(
            deliveryTimeRange = "40-50 min",
            deliveryFee = Money(799L),
            distanceKm = 4.2
        )
    }
}
