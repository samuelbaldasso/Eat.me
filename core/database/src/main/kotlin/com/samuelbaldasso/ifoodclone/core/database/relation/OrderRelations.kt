package com.samuelbaldasso.ifoodclone.core.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.samuelbaldasso.ifoodclone.core.database.entity.OrderEntity
import com.samuelbaldasso.ifoodclone.core.database.entity.OrderItemEntity
import com.samuelbaldasso.ifoodclone.core.database.entity.OrderItemOptionEntity

data class OrderItemWithOptions(
    @Embedded val item: OrderItemEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "orderItemId"
    )
    val options: List<OrderItemOptionEntity>
)

data class OrderWithItems(
    @Embedded val order: OrderEntity,
    @Relation(
        entity = OrderItemEntity::class,
        parentColumn = "id",
        entityColumn = "orderId"
    )
    val items: List<OrderItemWithOptions>
)
