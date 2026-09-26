package com.samuelbaldasso.ifoodclone.core.database.repository

import com.google.common.truth.Truth.assertThat
import com.samuelbaldasso.ifoodclone.core.database.dao.CartDao
import com.samuelbaldasso.ifoodclone.core.database.dao.RestaurantDao
import com.samuelbaldasso.ifoodclone.core.database.entity.CartItemEntity
import com.samuelbaldasso.ifoodclone.core.database.entity.CartItemOptionEntity
import com.samuelbaldasso.ifoodclone.core.database.entity.RestaurantEntity
import com.samuelbaldasso.ifoodclone.core.database.relation.CartItemWithOptions
import com.samuelbaldasso.ifoodclone.core.domain.model.AppResult
import com.samuelbaldasso.ifoodclone.core.domain.model.CartError
import com.samuelbaldasso.ifoodclone.core.domain.model.Dish
import com.samuelbaldasso.ifoodclone.core.domain.model.Money
import com.samuelbaldasso.ifoodclone.core.domain.model.Option
import com.samuelbaldasso.ifoodclone.core.domain.model.Restaurant
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class RoomCartRepositoryTest {

    private val cartDao: CartDao = mockk(relaxed = true)
    private val restaurantDao: RestaurantDao = mockk(relaxed = true)
    private val repository = RoomCartRepository(cartDao, restaurantDao)

    private val sampleRestaurant = Restaurant(
        id = "rest_1",
        name = "Burger King",
        category = "Lanches",
        rating = 4.5,
        ratingCount = 100,
        deliveryTimeRange = "30-40 min",
        deliveryFee = Money(500L),
        minOrderValue = Money(2000L),
        distanceKm = 2.0,
        imageUrl = "http://example.com/bk.png"
    )

    private val sampleDish = Dish(
        id = "dish_1",
        restaurantId = "rest_1",
        name = "Whopper",
        description = "Grelhado no fogo",
        basePrice = Money(3000L),
        promoPrice = null,
        isAvailable = true
    )

    private val sampleOption = Option(
        id = "opt_bacon",
        name = "Bacon",
        extraPrice = Money(450L)
    )

    @Test
    fun `GIVEN empty cart WHEN getCart observed THEN returns empty Cart domain model`() = runTest {
        coEvery { cartDao.observeCartItemsWithOptions() } returns flowOf(emptyList())

        val cart = repository.getCart().first()

        assertThat(cart.isEmpty).isTrue()
        assertThat(cart.items).isEmpty()
        assertThat(cart.subtotal).isEqualTo(Money.ZERO)
        assertThat(cart.total).isEqualTo(Money.ZERO)
    }

    @Test
    fun `GIVEN cart with items from another restaurant WHEN addToCart called without forceClear THEN returns DifferentRestaurant error`() = runTest {
        coEvery { cartDao.getCurrentRestaurantId() } returns "rest_2"
        coEvery { restaurantDao.getRestaurantById("rest_2") } returns RestaurantEntity(
            id = "rest_2",
            name = "Sushibar",
            category = "Japonesa",
            rating = 4.8,
            ratingCount = 200,
            deliveryTimeRange = "40-50 min",
            deliveryFeeCents = 600L,
            minOrderValueCents = 3000L,
            distanceKm = 3.0,
            imageUrl = "",
            isOpen = true,
            description = "",
            address = ""
        )

        val result = repository.addToCart(
            restaurant = sampleRestaurant,
            dish = sampleDish,
            selectedOptions = emptyList(),
            quantity = 1,
            forceClearIfDifferentRestaurant = false
        )

        assertThat(result).isInstanceOf(AppResult.Error::class.java)
        val error = (result as AppResult.Error).error
        assertThat(error).isInstanceOf(CartError.DifferentRestaurant::class.java)
        val diffError = error as CartError.DifferentRestaurant
        assertThat(diffError.currentRestaurantId).isEqualTo("rest_2")
        assertThat(diffError.currentRestaurantName).isEqualTo("Sushibar")
        assertThat(diffError.newRestaurantId).isEqualTo("rest_1")
    }

    @Test
    fun `GIVEN cart with items from another restaurant WHEN addToCart called WITH forceClear THEN clears cart and adds new item`() = runTest {
        coEvery { cartDao.getCurrentRestaurantId() } returns "rest_2"
        coEvery { cartDao.getCartItemsWithOptions() } returns emptyList()

        val result = repository.addToCart(
            restaurant = sampleRestaurant,
            dish = sampleDish,
            selectedOptions = listOf(sampleOption),
            quantity = 2,
            forceClearIfDifferentRestaurant = true
        )

        assertThat(result).isInstanceOf(AppResult.Success::class.java)
        coVerify(exactly = 1) { cartDao.clearCart() }
        coVerify(exactly = 1) { cartDao.insertCartItemWithOptions(any(), any()) }
    }

    @Test
    fun `GIVEN item already in cart with exact same options WHEN added again THEN increments quantity (RN-CART-02)`() = runTest {
        coEvery { cartDao.getCurrentRestaurantId() } returns "rest_1"
        val existingItem = CartItemWithOptions(
            item = CartItemEntity(
                id = "cart_item_existing",
                restaurantId = "rest_1",
                dishId = "dish_1",
                dishName = "Whopper",
                dishImageUrl = null,
                unitPriceCents = 3450L,
                quantity = 2
            ),
            options = listOf(
                CartItemOptionEntity(
                    id = "opt_entity_1",
                    cartItemId = "cart_item_existing",
                    optionId = "opt_bacon",
                    name = "Bacon",
                    extraPriceCents = 450L
                )
            )
        )
        coEvery { cartDao.getCartItemsWithOptions() } returns listOf(existingItem)

        val result = repository.addToCart(
            restaurant = sampleRestaurant,
            dish = sampleDish,
            selectedOptions = listOf(sampleOption),
            quantity = 3
        )

        assertThat(result).isInstanceOf(AppResult.Success::class.java)
        coVerify(exactly = 1) { cartDao.updateQuantity("cart_item_existing", 5) }
    }

    @Test
    fun `GIVEN item in cart WHEN updateQuantity to 0 THEN removes item from cart`() = runTest {
        val result = repository.updateItemQuantity("cart_item_existing", 0)

        assertThat(result).isInstanceOf(AppResult.Success::class.java)
        coVerify(exactly = 1) { cartDao.deleteCartItem("cart_item_existing") }
    }

    @Test
    fun `GIVEN quantity exceeds 20 WHEN updateQuantity called THEN returns Validation error (RN-CART-03)`() = runTest {
        val result = repository.updateItemQuantity("cart_item_existing", 25)

        assertThat(result).isInstanceOf(AppResult.Error::class.java)
    }
}
