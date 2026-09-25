package com.samuelbaldasso.ifoodclone.ui.theme.composables.home

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.samuelbaldasso.ifoodclone.ui.theme.composables.restaurant.RestaurantItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController = rememberNavController(),
    viewModel: HomeViewModel = hiltViewModel(),
    onRestaurantClick: (String) -> Unit = {}
) {
    Scaffold(
        topBar = {
            HomeAppBar(address = "Rua dos Devs, 1234")
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
//            item {
//                CategoryList()
//            }
//
//            item {
//                PromotionsBanners()
//            }

            item {
                Text(
                    text = "Lojas",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            items(viewModel.restaurants.value) { restaurant ->
                RestaurantItem(restaurant = restaurant, onClick = {
                    onRestaurantClick(restaurant.id)
                })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAppBar(address: String) {
    TopAppBar(
        title = { Text(text = address) }
    )
}

