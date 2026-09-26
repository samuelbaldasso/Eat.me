package com.samuelbaldasso.ifoodclone.core.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.samuelbaldasso.ifoodclone.core.database.entity.CartItemEntity
import com.samuelbaldasso.ifoodclone.core.database.entity.CartItemOptionEntity

data class CartItemWithOptions(
    @Embedded val item: CartItemEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "cartItemId"
    )
    val options: List<CartItemOptionEntity>
)
