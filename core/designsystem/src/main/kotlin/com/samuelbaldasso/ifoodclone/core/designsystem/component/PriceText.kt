package com.samuelbaldasso.ifoodclone.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.samuelbaldasso.ifoodclone.core.designsystem.theme.EatMeTheme
import com.samuelbaldasso.ifoodclone.core.domain.model.Money

/**
 * Component for displaying monetary values formatted as Brazilian Real (BRL).
 * Follows RN-REST-04: When promoPrice is present, displays promo price highlighted and original price struck-through.
 */
@Composable
fun PriceText(
    price: Money,
    modifier: Modifier = Modifier,
    promoPrice: Money? = null,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (promoPrice != null && promoPrice < price) {
            Text(
                text = promoPrice.formatBrl(),
                style = textStyle,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary
            )
            Text(
                text = price.formatBrl(),
                style = MaterialTheme.typography.bodySmall,
                textDecoration = TextDecoration.LineThrough,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Text(
                text = price.formatBrl(),
                style = textStyle,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
    }
}

@Preview(name = "Price - Normal", showBackground = true)
@Composable
private fun PriceTextNormalPreview() {
    EatMeTheme {
        PriceText(price = Money(2590L))
    }
}

@Preview(name = "Price - Promotional", showBackground = true)
@Composable
private fun PriceTextPromoPreview() {
    EatMeTheme {
        PriceText(
            price = Money(3290L),
            promoPrice = Money(2490L)
        )
    }
}
