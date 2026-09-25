package com.samuelbaldasso.ifoodclone.core.domain.usecase

import com.google.common.truth.Truth.assertThat
import com.samuelbaldasso.ifoodclone.core.domain.model.Dish
import com.samuelbaldasso.ifoodclone.core.domain.model.Money
import com.samuelbaldasso.ifoodclone.core.domain.model.Option
import com.samuelbaldasso.ifoodclone.core.domain.model.OptionGroup
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ValidateDishSelectionUseCaseTest {

    private val useCase = ValidateDishSelectionUseCase()

    private val pointGroup = OptionGroup(
        id = "meat_point",
        title = "Ponto da carne",
        minSelect = 1,
        maxSelect = 1,
        isRequired = true,
        options = listOf(
            Option(id = "rare", name = "Mal passado"),
            Option(id = "medium", name = "Ao ponto"),
            Option(id = "well_done", name = "Bem passado")
        )
    )

    private val cheeseGroup = OptionGroup(
        id = "extra_cheese",
        title = "Queijo extra",
        minSelect = 0,
        maxSelect = 2,
        isRequired = false,
        options = listOf(
            Option(id = "cheddar", name = "Cheddar", extraPrice = Money(450L)),
            Option(id = "bacon_cheese", name = "Queijo com Bacon", extraPrice = Money(600L)),
            Option(id = "unavailable_cheese", name = "Gorgonzola", isAvailable = false)
        )
    )

    private val burgerDish = Dish(
        id = "dish_1",
        restaurantId = "rest_1",
        name = "Super Cheeseburger",
        description = "Pão brioche, blend 180g e queijo",
        basePrice = Money(3290L),
        optionGroups = listOf(pointGroup, cheeseGroup)
    )

    @Test
    @DisplayName("GIVEN required point selected and optional cheese within limit WHEN validated THEN result is Valid")
    fun testValidSelection() {
        val selected = mapOf(
            "meat_point" to setOf("medium"),
            "extra_cheese" to setOf("cheddar")
        )

        val result = useCase(burgerDish, selected)
        assertThat(result).isEqualTo(DishValidationResult.Valid)
    }

    @Test
    @DisplayName("GIVEN required group with 0 selections WHEN validated THEN result is Invalid with group error")
    fun testRequiredGroupMissing() {
        val selected = mapOf(
            "extra_cheese" to setOf("cheddar")
        )

        val result = useCase(burgerDish, selected)
        assertThat(result).isInstanceOf(DishValidationResult.Invalid::class.java)
        val errors = (result as DishValidationResult.Invalid).groupErrors
        assertThat(errors).containsKey("meat_point")
    }

    @Test
    @DisplayName("GIVEN selections exceed maxSelect WHEN validated THEN result is Invalid")
    fun testExceedMaxSelect() {
        val selected = mapOf(
            "meat_point" to setOf("medium"),
            "extra_cheese" to setOf("cheddar", "bacon_cheese", "extra3")
        )

        val result = useCase(burgerDish, selected)
        assertThat(result).isInstanceOf(DishValidationResult.Invalid::class.java)
        val errors = (result as DishValidationResult.Invalid).groupErrors
        assertThat(errors).containsKey("extra_cheese")
    }

    @Test
    @DisplayName("GIVEN selected option is not available WHEN validated THEN result is Invalid")
    fun testUnavailableOption() {
        val selected = mapOf(
            "meat_point" to setOf("medium"),
            "extra_cheese" to setOf("unavailable_cheese")
        )

        val result = useCase(burgerDish, selected)
        assertThat(result).isInstanceOf(DishValidationResult.Invalid::class.java)
        val errors = (result as DishValidationResult.Invalid).groupErrors
        assertThat(errors).containsKey("extra_cheese")
    }

    @Test
    @DisplayName("GIVEN dish itself is unavailable WHEN validated THEN returns dish level error")
    fun testUnavailableDish() {
        val unavailableDish = burgerDish.copy(isAvailable = false)
        val result = useCase(unavailableDish, emptyMap())

        assertThat(result).isInstanceOf(DishValidationResult.Invalid::class.java)
        val errors = (result as DishValidationResult.Invalid).groupErrors
        assertThat(errors).containsKey("dish")
    }
}
