package com.samuelbaldasso.ifoodclone.core.database.seed

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DatabaseSeederTest {

    @Test
    fun `GIVEN DatabaseSeeder WHEN inspected THEN satisfies referential integrity across all entities`() {
        val restaurants = DatabaseSeeder.getRestaurants()
        val sections = DatabaseSeeder.getMenuSections()
        val dishes = DatabaseSeeder.getDishes()
        val groups = DatabaseSeeder.getOptionGroups()
        val options = DatabaseSeeder.getOptions()

        assertThat(restaurants).isNotEmpty()
        assertThat(sections).isNotEmpty()
        assertThat(dishes).isNotEmpty()
        assertThat(groups).isNotEmpty()
        assertThat(options).isNotEmpty()

        val restaurantIds = restaurants.map { it.id }.toSet()
        val sectionIds = sections.map { it.id }.toSet()
        val dishIds = dishes.map { it.id }.toSet()
        val groupIds = groups.map { it.id }.toSet()

        // Check foreign key: MenuSection.restaurantId in Restaurant.id
        for (section in sections) {
            assertThat(restaurantIds).contains(section.restaurantId)
        }

        // Check foreign key: Dish.sectionId in MenuSection.id AND Dish.restaurantId in Restaurant.id
        for (dish in dishes) {
            assertThat(sectionIds).contains(dish.sectionId)
            assertThat(restaurantIds).contains(dish.restaurantId)
            assertThat(dish.basePriceCents).isGreaterThan(0L)
            if (dish.promoPriceCents != null) {
                assertThat(dish.promoPriceCents).isLessThan(dish.basePriceCents)
            }
        }

        // Check foreign key: OptionGroup.dishId in Dish.id
        for (group in groups) {
            assertThat(dishIds).contains(group.dishId)
            assertThat(group.minSelect).isAtLeast(0)
            assertThat(group.maxSelect).isAtLeast(group.minSelect)
        }

        // Check foreign key: Option.groupId in OptionGroup.id
        for (option in options) {
            assertThat(groupIds).contains(option.groupId)
            assertThat(option.extraPriceCents).isAtLeast(0L)
        }
    }
}
