package com.samuelbaldasso.ifoodclone.core.domain.model

import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

/**
 * Value class representing monetary values in Brazilian Real (BRL) stored in whole cents (Long).
 * Floating-point types (Double/Float) are strictly forbidden for currency per RN-PRICE-01.
 */
@Serializable
@JvmInline
value class Money(val cents: Long) : Comparable<Money> {

    init {
        // Cents can be negative temporarily during intermediate calculation, but coerceAtLeastZero prevents negative totals
    }

    val isZero: Boolean get() = cents == 0L
    val isPositive: Boolean get() = cents > 0L
    val isNegative: Boolean get() = cents < 0L

    operator fun plus(other: Money): Money = Money(Math.addExact(this.cents, other.cents))

    operator fun minus(other: Money): Money = Money(Math.subtractExact(this.cents, other.cents))

    operator fun times(multiplier: Int): Money = Money(Math.multiplyExact(this.cents, multiplier.toLong()))

    operator fun times(multiplier: Long): Money = Money(Math.multiplyExact(this.cents, multiplier))

    /**
     * Calculates percentage of money with strict HALF_UP rounding.
     * e.g., Money(1000).percentage(10) == Money(100)
     */
    fun percentage(percent: Int, roundingMode: RoundingMode = RoundingMode.HALF_UP): Money {
        require(percent in 0..100) { "Percentage must be between 0 and 100, got: $percent" }
        if (percent == 0 || isZero) return ZERO
        if (percent == 100) return this

        val calculatedCents = BigDecimal.valueOf(cents)
            .multiply(BigDecimal.valueOf(percent.toLong()))
            .divide(BigDecimal.valueOf(100L), 0, roundingMode)
            .longValueExact()

        return Money(calculatedCents)
    }

    /**
     * Ensures monetary amount is never negative, as required by RN-PRICE-01 for totals and line items.
     */
    fun coerceAtLeastZero(): Money = if (cents < 0L) ZERO else this

    override fun compareTo(other: Money): Int = this.cents.compareTo(other.cents)

    /**
     * Formats into pt-BR BRL representation (e.g. "R$ 15,50").
     */
    fun formatBrl(locale: Locale = LOCALE_PT_BR): String {
        val format = NumberFormat.getCurrencyInstance(locale)
        val decimalValue = BigDecimal.valueOf(cents).divide(BigDecimal.valueOf(100L), 2, RoundingMode.UNNECESSARY)
        return format.format(decimalValue)
    }

    fun toFormattedBrl(): String = formatBrl()

    companion object {
        val ZERO: Money = Money(0L)
        val LOCALE_PT_BR: Locale = Locale.forLanguageTag("pt-BR")

        fun fromCents(cents: Long): Money = Money(cents)

        fun fromReais(reais: Long): Money = Money(Math.multiplyExact(reais, 100L))

        fun fromDouble(amount: Double): Money {
            val roundedCents = BigDecimal.valueOf(amount)
                .multiply(BigDecimal.valueOf(100L))
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact()
            return Money(roundedCents)
        }
    }
}
