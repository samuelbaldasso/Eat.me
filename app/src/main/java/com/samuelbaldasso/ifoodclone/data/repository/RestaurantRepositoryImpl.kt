package com.samuelbaldasso.ifoodclone.data.repository

import com.samuelbaldasso.ifoodclone.domain.restaurant.Restaurant
import javax.inject.Inject

class RestaurantRepositoryImpl @Inject constructor() : RestaurantRepository {
    override suspend fun getRestaurants(): List<Restaurant> {
        return listOf(
            Restaurant(
                id = "1",
                name = "Burger King",
                category = "Lanches",
                deliveryFee = "Grátis",
                deliveryTime = "30-40 min",
                rating = 4.5,
                imageUrl = "https://logodownload.org/wp-content/uploads/2014/07/burger-king-logo-6.png"
            ),
            Restaurant(
                id = "2",
                name = "Sushibar Oriental",
                category = "Japonesa",
                deliveryFee = "R$ 5,99",
                deliveryTime = "50-60 min",
                rating = 4.8,
                imageUrl = "https://via.placeholder.com/150/FF0000/FFFFFF?text=Sushi"
            ),
            Restaurant(
                id = "3",
                name = "Pizzaria do Bairro",
                category = "Pizza",
                deliveryFee = "Grátis",
                deliveryTime = "20-30 min",
                rating = 4.2,
                imageUrl = "https://via.placeholder.com/150/FFA500/FFFFFF?text=Pizza"
            ),
            Restaurant(
                id = "4",
                name = "Açaí Frutas e Cia",
                category = "Doces & Bolos",
                deliveryFee = "R$ 2,00",
                deliveryTime = "15-25 min",
                rating = 4.9,
                imageUrl = "https://via.placeholder.com/150/800080/FFFFFF?text=Acai"
            ),
            Restaurant(
                id = "5",
                name = "Churrascaria Gaúcha",
                category = "Carnes",
                deliveryFee = "R$ 12,00",
                deliveryTime = "40-55 min",
                rating = 4.6,
                imageUrl = "https://via.placeholder.com/150/8B4513/FFFFFF?text=Churrasco"
            )
        )
    }
}