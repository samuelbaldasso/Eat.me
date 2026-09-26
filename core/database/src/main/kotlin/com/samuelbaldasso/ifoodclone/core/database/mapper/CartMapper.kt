package com.samuelbaldasso.ifoodclone.core.database.mapper

import com.samuelbaldasso.ifoodclone.core.database.entity.CartItemOptionEntity
import com.samuelbaldasso.ifoodclone.core.database.relation.CartItemWithOptions
import com.samuelbaldasso.ifoodclone.core.domain.model.CartItem
import com.samuelbaldasso.ifoodclone.core.domain.model.CartItemOption
import com.samuelbaldasso.ifoodclone.core.domain.model.Money

fun CartItemOptionEntity.toDomain(): CartItemOption = CartItemOption(
    optionId = optionId,
    name = name,
    extraPrice = Money(extraPriceCents)
)

fun CartItemWithOptions.toDomain(): CartItem = CartItem(
    id = item.id,
    restaurantId = item.restaurantId,
    dishId = item.dishId,
    dishName = item.dishName,
    dishImageUrl = item.dishImageUrl,
    unitPrice = Money(item.unitPriceCents),
    quantity = item.quantity,
    selectedOptions = options.map { it.toDomain() },
    notes = item.notes
)
