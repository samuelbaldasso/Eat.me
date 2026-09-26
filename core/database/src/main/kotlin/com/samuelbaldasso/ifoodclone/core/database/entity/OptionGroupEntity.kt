package com.samuelbaldasso.ifoodclone.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "option_groups",
    foreignKeys = [
        ForeignKey(
            entity = DishEntity::class,
            parentColumns = ["id"],
            childColumns = ["dishId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("dishId")]
)
data class OptionGroupEntity(
    @PrimaryKey val id: String,
    val dishId: String,
    val title: String,
    val minSelect: Int,
    val maxSelect: Int,
    val isRequired: Boolean,
    val sortOrder: Int
)
