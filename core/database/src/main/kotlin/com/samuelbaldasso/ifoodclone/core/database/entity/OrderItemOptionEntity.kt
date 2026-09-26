package com.samuelbaldasso.ifoodclone.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "order_item_options",
    foreignKeys = [
        ForeignKey(
            entity = OrderItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["orderItemId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("orderItemId")]
)
data class OrderItemOptionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderItemId: String,
    val optionId: String,
    val name: String,
    val extraPriceCents: Long
)
