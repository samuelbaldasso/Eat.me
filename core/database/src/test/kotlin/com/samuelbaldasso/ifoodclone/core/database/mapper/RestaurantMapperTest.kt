package com.samuelbaldasso.ifoodclone.core.database.mapper

import com.google.common.truth.Truth.assertThat
import com.samuelbaldasso.ifoodclone.core.database.entity.DishEntity
import com.samuelbaldasso.ifoodclone.core.database.entity.MenuSectionEntity
import com.samuelbaldasso.ifoodclone.core.database.entity.OptionEntity
import com.samuelbaldasso.ifoodclone.core.database.entity.OptionGroupEntity
import com.samuelbaldasso.ifoodclone.core.database.entity.RestaurantEntity
import com.samuelbaldasso.ifoodclone.core.database.relation.DishWithOptions
import com.samuelbaldasso.ifoodclone.core.database.relation.MenuSectionWithDishes
import com.samuelbaldasso.ifoodclone.core.database.relation.OptionGroupWithOptions
import com.samuelbaldasso.ifoodclone.core.database.relation.RestaurantWithSections
import com.samuelbaldasso.ifoodclone.core.domain.model.Money
import org.junit.Test

class RestaurantMapperTest {

    @Test
    fun `GIVEN RestaurantEntity WHEN toDomain mapped THEN converts cents to Money correctly`() {
        val entity = RestaurantEntity(
            id = "1",
            name = "Burger King",
            category = "Lanches",
            rating = 4.5,
            ratingCount = 100,
            deliveryTimeRange = "30-40 min",
            deliveryFeeCents = 590L,
            minOrderValueCents = 2000L,
            distanceKm = 2.1,
            imageUrl = "http://example.com/logo.png",
            isOpen = true,
            description = "Hambúrgueres",
            address = "Av. Paulista"
        )

        val domain = entity.toDomain()

        assertThat(domain.id).isEqualTo("1")
        assertThat(domain.name).isEqualTo("Burger King")
        assertThat(domain.deliveryFee).isEqualTo(Money(590L))
        assertThat(domain.deliveryFee.formatBrl()).isEqualTo("R$\u00A05,90")
        assertThat(domain.minOrderValue).isEqualTo(Money(2000L))
        assertThat(domain.minOrderValue.formatBrl()).isEqualTo("R$\u00A020,00")
        assertThat(domain.isOpen).isTrue()
    }

    @Test
    fun `GIVEN nested RestaurantWithSections WHEN toDomain mapped THEN preserves hierarchy and sorts sections and dishes`() {
        val restaurant = RestaurantEntity(
            id = "1",
            name = "Burger King",
            category = "Lanches",
            rating = 4.5,
            ratingCount = 100,
            deliveryTimeRange = "30-40 min",
            deliveryFeeCents = 0L,
            minOrderValueCents = 0L,
            distanceKm = 1.0,
            imageUrl = "http://example.com/logo.png",
            isOpen = true,
            description = "Descrição",
            address = "Endereço"
        )

        val option1 = OptionEntity("opt_1", "grp_1", "Opção 1", extraPriceCents = 0L, isAvailable = true, sortOrder = 1)
        val option2 = OptionEntity("opt_2", "grp_1", "Opção 2", extraPriceCents = 250L, isAvailable = true, sortOrder = 2)

        val group = OptionGroupWithOptions(
            group = OptionGroupEntity("grp_1", "dish_1", "Ponto", minSelect = 1, maxSelect = 1, isRequired = true, sortOrder = 1),
            options = listOf(option2, option1) // unsorted
        )

        val dish = DishWithOptions(
            dish = DishEntity(
                id = "dish_1",
                sectionId = "sec_1",
                restaurantId = "1",
                name = "Whopper",
                description = "Delicioso",
                imageUrl = null,
                basePriceCents = 3000L,
                promoPriceCents = 2500L,
                isAvailable = true,
                servesPeople = 1,
                sortOrder = 1
            ),
            optionGroups = listOf(group)
        )

        val section = MenuSectionWithDishes(
            section = MenuSectionEntity("sec_1", "1", "Destaques", sortOrder = 1),
            dishes = listOf(dish)
        )

        val relation = RestaurantWithSections(
            restaurant = restaurant,
            sections = listOf(section)
        )

        val domain = relation.toDomain()

        assertThat(domain.restaurant.name).isEqualTo("Burger King")
        assertThat(domain.menuSections).hasSize(1)

        val domainSection = domain.menuSections.first()
        assertThat(domainSection.name).isEqualTo("Destaques")
        assertThat(domainSection.dishes).hasSize(1)

        val domainDish = domainSection.dishes.first()
        assertThat(domainDish.name).isEqualTo("Whopper")
        assertThat(domainDish.basePrice).isEqualTo(Money(3000L))
        assertThat(domainDish.promoPrice).isEqualTo(Money(2500L))
        assertThat(domainDish.effectivePrice).isEqualTo(Money(2500L))

        val domainGroup = domainDish.optionGroups.first()
        assertThat(domainGroup.title).isEqualTo("Ponto")
        assertThat(domainGroup.options).hasSize(2)
        // Verified sorted by sortOrder
        assertThat(domainGroup.options[0].name).isEqualTo("Opção 1")
        assertThat(domainGroup.options[1].name).isEqualTo("Opção 2")
        assertThat(domainGroup.options[1].extraPrice).isEqualTo(Money(250L))
    }
}
