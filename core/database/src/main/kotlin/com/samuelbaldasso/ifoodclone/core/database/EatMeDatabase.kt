package com.samuelbaldasso.ifoodclone.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.samuelbaldasso.ifoodclone.core.database.dao.RestaurantDao
import com.samuelbaldasso.ifoodclone.core.database.entity.DishEntity
import com.samuelbaldasso.ifoodclone.core.database.entity.MenuSectionEntity
import com.samuelbaldasso.ifoodclone.core.database.entity.OptionEntity
import com.samuelbaldasso.ifoodclone.core.database.entity.OptionGroupEntity
import com.samuelbaldasso.ifoodclone.core.database.entity.RestaurantEntity

@Database(
    entities = [
        RestaurantEntity::class,
        MenuSectionEntity::class,
        DishEntity::class,
        OptionGroupEntity::class,
        OptionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class EatMeDatabase : RoomDatabase() {
    abstract fun restaurantDao(): RestaurantDao

    companion object {
        const val DATABASE_NAME = "eatme_database.db"
    }
}
