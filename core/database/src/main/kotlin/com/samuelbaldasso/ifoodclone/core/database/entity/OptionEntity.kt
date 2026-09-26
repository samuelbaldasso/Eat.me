package com.samuelbaldasso.ifoodclone.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "options",
    foreignKeys = [
        ForeignKey(
            entity = OptionGroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("groupId")]
)
data class OptionEntity(
    @PrimaryKey val id: String,
    val groupId: String,
    val name: String,
    val extraPriceCents: Long,
    val isAvailable: Boolean,
    val sortOrder: Int
)
