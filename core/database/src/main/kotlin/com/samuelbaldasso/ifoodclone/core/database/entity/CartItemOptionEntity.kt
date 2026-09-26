package com.samuelbaldasso.ifoodclone.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cart_item_options",
    foreignKeys = [
        ForeignKey(
            entity = CartItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["cartItemId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("cartItemId")]
)
data class CartItemOptionEntity(
    @PrimaryKey val id: String,
    val cartItemId: String,
    val optionId: String,
    val name: String,
    val extraPriceCents: Long
)
