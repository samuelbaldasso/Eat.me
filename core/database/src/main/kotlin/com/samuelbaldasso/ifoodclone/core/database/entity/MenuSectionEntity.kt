package com.samuelbaldasso.ifoodclone.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "menu_sections",
    foreignKeys = [
        ForeignKey(
            entity = RestaurantEntity::class,
            parentColumns = ["id"],
            childColumns = ["restaurantId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("restaurantId")]
)
data class MenuSectionEntity(
    @PrimaryKey val id: String,
    val restaurantId: String,
    val name: String,
    val sortOrder: Int
)
