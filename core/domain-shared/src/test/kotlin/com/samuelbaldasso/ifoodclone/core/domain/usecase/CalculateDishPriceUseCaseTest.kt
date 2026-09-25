package com.samuelbaldasso.ifoodclone.core.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.samuelbaldasso.ifoodclone.core.domain.model.Dish
import com.samuelbaldasso.ifoodclone.core.domain.model.Money
import com.samuelbaldasso.ifoodclone.core.domain.model.Option
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class CalculateDishPriceUseCaseTest {

    private val useCase = CalculateDishPriceUseCase()

    private val baseDish = Dish(
        id = "dish_1",
        restaurantId = "rest_1",
        name = "Pizza Calabresa",
        description = "Massa artesanal, molho e calabresa",
        basePrice = Money(4500L), // R$ 45,00
        promoPrice = null
    )

    private val baconOption = Option(id = "opt_1", name = "Bacon", extraPrice = Money(500L)) // + R$ 5,00
    private val crustOption = Option(id = "opt_2", name = "Borda recheada", extraPrice = Money(850L)) // + R$ 8,50

    @Test
    @DisplayName("GIVEN base dish and 0 options WHEN price calculated THEN equals basePrice * quantity")
    fun testBasePriceOnly() {
        val total = useCase(baseDish, emptyList(), quantity = 2)
        assertThat(total).isEqualTo(Money(9000L)) // R$ 90,00
    }

    @Test
    @DisplayName("GIVEN dish with promoPrice WHEN price calculated THEN uses promoPrice instead of basePrice")
    fun testPromoPricePriority() {
        val promoDish = baseDish.copy(promoPrice = Money(3990L)) // R$ 39,90
        val total = useCase(promoDish, emptyList(), quantity = 1)
        assertThat(total).isEqualTo(Money(3990L))
    }

    @Test
    @DisplayName("GIVEN dish with extra options WHEN price calculated THEN sums extras before multiplying quantity")
    fun testExtrasAddition() {
        // (45.00 + 5.00 + 8.50) * 3 = 58.50 * 3 = 175.50
        val total = useCase(
            dish = baseDish,
            selectedOptions = listOf(baconOption, crustOption),
            quantity = 3
        )
        assertThat(total).isEqualTo(Money(17550L)) // R$ 175,50
    }

    @Test
    @DisplayName("GIVEN quantity outside 1..20 bounds WHEN calculated THEN throws IllegalArgumentException per RN-CART-03")
    fun testQuantityLimits() {
        assertThrows(IllegalArgumentException::class.java) {
            useCase(baseDish, emptyList(), quantity = 0)
        }
        assertThrows(IllegalArgumentException::class.java) {
            useCase(baseDish, emptyList(), quantity = 21)
        }
    }
}
