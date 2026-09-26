package com.samuelbaldasso.ifoodclone.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.samuelbaldasso.ifoodclone.core.database.entity.DishEntity
import com.samuelbaldasso.ifoodclone.core.database.entity.MenuSectionEntity
import com.samuelbaldasso.ifoodclone.core.database.entity.OptionEntity
import com.samuelbaldasso.ifoodclone.core.database.entity.OptionGroupEntity
import com.samuelbaldasso.ifoodclone.core.database.entity.RestaurantEntity
import com.samuelbaldasso.ifoodclone.core.database.relation.RestaurantWithSections

@Dao
interface RestaurantDao {

    @Query("SELECT * FROM restaurants ORDER BY rating DESC, name ASC")
    suspend fun getAllRestaurants(): List<RestaurantEntity>

    @Query("SELECT * FROM restaurants WHERE id = :id")
    suspend fun getRestaurantById(id: String): RestaurantEntity?

    @Transaction
    @Query("SELECT * FROM restaurants WHERE id = :id")
    suspend fun getRestaurantWithDetails(id: String): RestaurantWithSections?

    @Query("SELECT COUNT(*) FROM restaurants")
    suspend fun getRestaurantCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRestaurants(restaurants: List<RestaurantEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuSections(sections: List<MenuSectionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDishes(dishes: List<DishEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOptionGroups(groups: List<OptionGroupEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOptions(options: List<OptionEntity>)

    @Transaction
    suspend fun seedMarketplaceData(
        restaurants: List<RestaurantEntity>,
        sections: List<MenuSectionEntity>,
        dishes: List<DishEntity>,
        groups: List<OptionGroupEntity>,
        options: List<OptionEntity>
    ) {
        insertRestaurants(restaurants)
        insertMenuSections(sections)
        insertDishes(dishes)
        insertOptionGroups(groups)
        insertOptions(options)
    }
}
