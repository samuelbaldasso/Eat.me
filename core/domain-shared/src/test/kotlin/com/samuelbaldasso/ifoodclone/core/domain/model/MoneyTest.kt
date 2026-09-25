package com.samuelbaldasso.ifoodclone.core.domain.model

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.util.Locale

class MoneyTest {

    @Test
    @DisplayName("GIVEN zero cents WHEN instantiated THEN isZero is true and isPositive is false")
    fun testZeroCents() {
        val money = Money.ZERO
        assertThat(money.isZero).isTrue()
        assertThat(money.isPositive).isFalse()
        assertThat(money.isNegative).isFalse()
        assertThat(money.cents).isEqualTo(0L)
    }

    @Test
    @DisplayName("GIVEN positive and negative cents WHEN checking state THEN signs match expectations")
    fun testSignChecks() {
        val positive = Money(150L)
        assertThat(positive.isPositive).isTrue()
        assertThat(positive.isNegative).isFalse()
        assertThat(positive.isZero).isFalse()

        val negative = Money(-150L)
        assertThat(negative.isPositive).isFalse()
        assertThat(negative.isNegative).isTrue()
        assertThat(negative.isZero).isFalse()
    }

    @Test
    @DisplayName("GIVEN two Money values WHEN adding THEN sum of cents is correct")
    fun testAddition() {
        val a = Money(1250L) // R$ 12,50
        val b = Money(750L)  // R$ 7,50
        val result = a + b
        assertThat(result.cents).isEqualTo(2000L)
    }

    @Test
    @DisplayName("GIVEN two Money values WHEN subtracting THEN difference of cents is correct")
    fun testSubtraction() {
        val a = Money(2000L)
        val b = Money(750L)
        val result = a - b
        assertThat(result.cents).isEqualTo(1250L)
    }

    @Test
    @DisplayName("GIVEN negative intermediate Money WHEN coerceAtLeastZero THEN returns ZERO per RN-PRICE-01")
    fun testCoerceAtLeastZero() {
        val subtotal = Money(1000L) // R$ 10,00
        val discount = Money(1500L) // R$ 15,00 cupom
        val intermediate = subtotal - discount // -500L
        val finalTotal = intermediate.coerceAtLeastZero()

        assertThat(intermediate.cents).isEqualTo(-500L)
        assertThat(finalTotal.cents).isEqualTo(0L)
        assertThat(finalTotal).isEqualTo(Money.ZERO)
    }

    @Test
    @DisplayName("GIVEN Money amount WHEN multiplied by quantity THEN product is exact")
    fun testMultiplication() {
        val unitPrice = Money(2490L) // R$ 24,90
        val quantity = 3
        val total = unitPrice * quantity
        assertThat(total.cents).isEqualTo(7470L) // R$ 74,70
    }

    @ParameterizedTest(name = "amount {0} cents with {1}% discount should equal {2} cents")
    @CsvSource(
        "1000, 10, 100",   // 10% of 10.00 = 1.00
        "1000, 15, 150",   // 15% of 10.00 = 1.50
        "999, 10, 100",    // 9.99 * 0.10 = 99.9 -> 100 (HALF_UP rounding)
        "995, 10, 100",    // 9.95 * 0.10 = 99.5 -> 100 (HALF_UP rounding)
        "994, 10, 99",     // 9.94 * 0.10 = 99.4 -> 99 (HALF_UP rounding)
        "0, 50, 0",        // 50% of 0 = 0
        "5000, 0, 0",      // 0% of 50 = 0
        "5000, 100, 5000"  // 100% of 50 = 50
    )
    fun testPercentageCalculation(cents: Long, percent: Int, expectedCents: Long) {
        val money = Money(cents)
        val result = money.percentage(percent)
        assertThat(result.cents).isEqualTo(expectedCents)
    }

    @Test
    @DisplayName("GIVEN invalid percentage WHEN percentage called THEN throws IllegalArgumentException")
    fun testInvalidPercentage() {
        val money = Money(1000L)
        assertThrows(IllegalArgumentException::class.java) {
            money.percentage(-5)
        }
        assertThrows(IllegalArgumentException::class.java) {
            money.percentage(101)
        }
    }

    @Test
    @DisplayName("GIVEN factory fromReais and fromDouble WHEN instantiated THEN cents are exact")
    fun testFactories() {
        val fromReais = Money.fromReais(45)
        assertThat(fromReais.cents).isEqualTo(4500L)

        val fromDouble = Money.fromDouble(19.99)
        assertThat(fromDouble.cents).isEqualTo(1999L)
    }

    @Test
    @DisplayName("GIVEN Money WHEN formatted in pt-BR THEN currency symbol and separators are correct")
    fun testFormatBrl() {
        val money = Money(125050L) // R$ 1.250,50
        val formatted = money.formatBrl(Locale.forLanguageTag("pt-BR"))
        // NumberFormat in Java pt-BR formats as "R$\u00A01.250,50" or "R$ 1.250,50"
        assertThat(formatted.replace('\u00A0', ' ')).isEqualTo("R$ 1.250,50")

        val zero = Money.ZERO.formatBrl(Locale.forLanguageTag("pt-BR"))
        assertThat(zero.replace('\u00A0', ' ')).isEqualTo("R$ 0,00")
    }

    @Test
    @DisplayName("GIVEN comparison between two Money values THEN ordering follows cents")
    fun testComparable() {
        val small = Money(100L)
        val large = Money(200L)
        assertThat(small).isLessThan(large)
        assertThat(large).isGreaterThan(small)
        assertThat(small).isEquivalentAccordingToCompareTo(Money(100L))
    }
}
