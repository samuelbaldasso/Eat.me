package com.samuelbaldasso.ifoodclone.core.database.mapper

import com.samuelbaldasso.ifoodclone.core.database.entity.OptionEntity
import com.samuelbaldasso.ifoodclone.core.database.entity.RestaurantEntity
import com.samuelbaldasso.ifoodclone.core.database.relation.DishWithOptions
import com.samuelbaldasso.ifoodclone.core.database.relation.MenuSectionWithDishes
import com.samuelbaldasso.ifoodclone.core.database.relation.OptionGroupWithOptions
import com.samuelbaldasso.ifoodclone.core.database.relation.RestaurantWithSections
import com.samuelbaldasso.ifoodclone.core.domain.model.Dish
import com.samuelbaldasso.ifoodclone.core.domain.model.MenuSection
import com.samuelbaldasso.ifoodclone.core.domain.model.Money
import com.samuelbaldasso.ifoodclone.core.domain.model.Option
import com.samuelbaldasso.ifoodclone.core.domain.model.OptionGroup
import com.samuelbaldasso.ifoodclone.core.domain.model.Restaurant
import com.samuelbaldasso.ifoodclone.core.domain.model.RestaurantDetails

fun RestaurantEntity.toDomain(): Restaurant = Restaurant(
    id = id,
    name = name,
    category = category,
    rating = rating,
    ratingCount = ratingCount,
    deliveryTimeRange = deliveryTimeRange,
    deliveryFee = Money(deliveryFeeCents),
    minOrderValue = Money(minOrderValueCents),
    distanceKm = distanceKm,
    imageUrl = imageUrl,
    isOpen = isOpen
)

fun OptionEntity.toDomain(): Option = Option(
    id = id,
    name = name,
    extraPrice = Money(extraPriceCents),
    isAvailable = isAvailable
)

fun OptionGroupWithOptions.toDomain(): OptionGroup = OptionGroup(
    id = group.id,
    title = group.title,
    minSelect = group.minSelect,
    maxSelect = group.maxSelect,
    isRequired = group.isRequired,
    options = options.sortedBy { it.sortOrder }.map { it.toDomain() }
)

fun DishWithOptions.toDomain(): Dish = Dish(
    id = dish.id,
    restaurantId = dish.restaurantId,
    name = dish.name,
    description = dish.description,
    imageUrl = dish.imageUrl,
    basePrice = Money(dish.basePriceCents),
    promoPrice = dish.promoPriceCents?.let { Money(it) },
    isAvailable = dish.isAvailable,
    servesPeople = dish.servesPeople,
    optionGroups = optionGroups.sortedBy { it.group.sortOrder }.map { it.toDomain() }
)

fun MenuSectionWithDishes.toDomain(): MenuSection = MenuSection(
    id = section.id,
    name = section.name,
    dishes = dishes.sortedBy { it.dish.sortOrder }.map { it.toDomain() }
)

fun RestaurantWithSections.toDomain(): RestaurantDetails = RestaurantDetails(
    restaurant = restaurant.toDomain(),
    description = restaurant.description,
    address = restaurant.address,
    menuSections = sections.sortedBy { it.section.sortOrder }.map { it.toDomain() }
)
