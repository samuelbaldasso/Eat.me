package com.samuelbaldasso.ifoodclone.core.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.samuelbaldasso.ifoodclone.core.database.entity.DishEntity
import com.samuelbaldasso.ifoodclone.core.database.entity.MenuSectionEntity
import com.samuelbaldasso.ifoodclone.core.database.entity.OptionEntity
import com.samuelbaldasso.ifoodclone.core.database.entity.OptionGroupEntity
import com.samuelbaldasso.ifoodclone.core.database.entity.RestaurantEntity

data class OptionGroupWithOptions(
    @Embedded val group: OptionGroupEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "groupId"
    )
    val options: List<OptionEntity>
)

data class DishWithOptions(
    @Embedded val dish: DishEntity,
    @Relation(
        entity = OptionGroupEntity::class,
        parentColumn = "id",
        entityColumn = "dishId"
    )
    val optionGroups: List<OptionGroupWithOptions>
)

data class MenuSectionWithDishes(
    @Embedded val section: MenuSectionEntity,
    @Relation(
        entity = DishEntity::class,
        parentColumn = "id",
        entityColumn = "sectionId"
    )
    val dishes: List<DishWithOptions>
)

data class RestaurantWithSections(
    @Embedded val restaurant: RestaurantEntity,
    @Relation(
        entity = MenuSectionEntity::class,
        parentColumn = "id",
        entityColumn = "restaurantId"
    )
    val sections: List<MenuSectionWithDishes>
)
