package com.samuelbaldasso.ifoodclone.core.database.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.samuelbaldasso.ifoodclone.core.database.EatMeDatabase
import com.samuelbaldasso.ifoodclone.core.database.dao.RestaurantDao
import com.samuelbaldasso.ifoodclone.core.database.repository.RoomRestaurantRepository
import com.samuelbaldasso.ifoodclone.core.database.seed.DatabaseSeeder
import com.samuelbaldasso.ifoodclone.core.domain.repository.RestaurantRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideEatMeDatabase(
        @ApplicationContext context: Context
    ): EatMeDatabase {
        lateinit var database: EatMeDatabase
        database = Room.databaseBuilder(
            context,
            EatMeDatabase::class.java,
            EatMeDatabase.DATABASE_NAME
        )
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    CoroutineScope(Dispatchers.IO).launch {
                        seedIfEmpty(database)
                    }
                }

                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    CoroutineScope(Dispatchers.IO).launch {
                        seedIfEmpty(database)
                    }
                }
            })
            .build()
        return database
    }

    private suspend fun seedIfEmpty(database: EatMeDatabase) {
        val dao = database.restaurantDao()
        if (dao.getRestaurantCount() == 0) {
            dao.seedMarketplaceData(
                restaurants = DatabaseSeeder.getRestaurants(),
                sections = DatabaseSeeder.getMenuSections(),
                dishes = DatabaseSeeder.getDishes(),
                groups = DatabaseSeeder.getOptionGroups(),
                options = DatabaseSeeder.getOptions()
            )
        }
    }

    @Provides
    fun provideRestaurantDao(database: EatMeDatabase): RestaurantDao {
        return database.restaurantDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseBindingModule {

    @Binds
    @Singleton
    abstract fun bindRestaurantRepository(
        impl: RoomRestaurantRepository
    ): RestaurantRepository
}
