package com.samuelbaldasso.ifoodclone.core.database.repository

import com.google.common.truth.Truth.assertThat
import com.samuelbaldasso.ifoodclone.core.database.dao.RestaurantDao
import com.samuelbaldasso.ifoodclone.core.database.entity.RestaurantEntity
import com.samuelbaldasso.ifoodclone.core.database.relation.RestaurantWithSections
import com.samuelbaldasso.ifoodclone.core.domain.model.AppError
import com.samuelbaldasso.ifoodclone.core.domain.model.AppResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class RoomRestaurantRepositoryTest {

    private val restaurantDao: RestaurantDao = mockk()
    private val repository = RoomRestaurantRepository(restaurantDao)

    private val sampleEntity = RestaurantEntity(
        id = "1",
        name = "Burger King",
        category = "Lanches",
        rating = 4.5,
        ratingCount = 1420,
        deliveryTimeRange = "30-40 min",
        deliveryFeeCents = 0L,
        minOrderValueCents = 2000L,
        distanceKm = 2.1,
        imageUrl = "http://example.com/logo.png",
        isOpen = true,
        description = "Grelhado no fogo",
        address = "Av. Paulista"
    )

    @Test
    fun `GIVEN dao returns restaurants WHEN getRestaurants THEN returns AppResult Success with domain models`() = runTest {
        coEvery { restaurantDao.getAllRestaurants() } returns listOf(sampleEntity)

        val result = repository.getRestaurants()

        assertThat(result).isInstanceOf(AppResult.Success::class.java)
        val success = result as AppResult.Success
        assertThat(success.data).hasSize(1)
        assertThat(success.data.first().name).isEqualTo("Burger King")
    }

    @Test
    fun `GIVEN dao throws exception WHEN getRestaurants THEN returns AppResult Error with AppError Unknown`() = runTest {
        coEvery { restaurantDao.getAllRestaurants() } throws RuntimeException("DB disk I/O error")

        val result = repository.getRestaurants()

        assertThat(result).isInstanceOf(AppResult.Error::class.java)
        val error = result as AppResult.Error
        assertThat(error.error).isInstanceOf(AppError.Unknown::class.java)
    }

    @Test
    fun `GIVEN restaurant found WHEN getRestaurantDetails THEN returns AppResult Success`() = runTest {
        val relation = RestaurantWithSections(
            restaurant = sampleEntity,
            sections = emptyList()
        )
        coEvery { restaurantDao.getRestaurantWithDetails("1") } returns relation

        val result = repository.getRestaurantDetails("1")

        assertThat(result).isInstanceOf(AppResult.Success::class.java)
        val success = result as AppResult.Success
        assertThat(success.data.restaurant.name).isEqualTo("Burger King")
    }

    @Test
    fun `GIVEN restaurant not found WHEN getRestaurantDetails THEN returns AppResult Error NotFound`() = runTest {
        coEvery { restaurantDao.getRestaurantWithDetails("999") } returns null

        val result = repository.getRestaurantDetails("999")

        assertThat(result).isInstanceOf(AppResult.Error::class.java)
        val error = result as AppResult.Error
        assertThat(error.error).isInstanceOf(AppError.NotFound::class.java)
    }
}
