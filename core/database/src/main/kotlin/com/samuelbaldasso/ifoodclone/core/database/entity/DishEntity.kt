package com.samuelbaldasso.ifoodclone.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "dishes",
    foreignKeys = [
        ForeignKey(
            entity = MenuSectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sectionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sectionId"), Index("restaurantId")]
)
data class DishEntity(
    @PrimaryKey val id: String,
    val sectionId: String,
    val restaurantId: String,
    val name: String,
    val description: String,
    val imageUrl: String?,
    val basePriceCents: Long,
    val promoPriceCents: Long?,
    val isAvailable: Boolean,
    val servesPeople: Int,
    val sortOrder: Int
)
