package com.samuelbaldasso.ifoodclone.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.samuelbaldasso.ifoodclone.core.database.dao.CartDao
import com.samuelbaldasso.ifoodclone.core.database.dao.OrderDao
import com.samuelbaldasso.ifoodclone.core.database.dao.RestaurantDao
import com.samuelbaldasso.ifoodclone.core.database.entity.CartItemEntity
import com.samuelbaldasso.ifoodclone.core.database.entity.CartItemOptionEntity
import com.samuelbaldasso.ifoodclone.core.database.entity.DishEntity
import com.samuelbaldasso.ifoodclone.core.database.entity.MenuSectionEntity
import com.samuelbaldasso.ifoodclone.core.database.entity.OptionEntity
import com.samuelbaldasso.ifoodclone.core.database.entity.OptionGroupEntity
import com.samuelbaldasso.ifoodclone.core.database.entity.OrderEntity
import com.samuelbaldasso.ifoodclone.core.database.entity.OrderItemEntity
import com.samuelbaldasso.ifoodclone.core.database.entity.OrderItemOptionEntity
import com.samuelbaldasso.ifoodclone.core.database.entity.RestaurantEntity

@Database(
    entities = [
        RestaurantEntity::class,
        MenuSectionEntity::class,
        DishEntity::class,
        OptionGroupEntity::class,
        OptionEntity::class,
        CartItemEntity::class,
        CartItemOptionEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        OrderItemOptionEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class EatMeDatabase : RoomDatabase() {
    abstract fun restaurantDao(): RestaurantDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao

    companion object {
        const val DATABASE_NAME = "eatme_database.db"
    }
}
