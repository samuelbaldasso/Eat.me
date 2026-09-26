package com.samuelbaldasso.ifoodclone.ui.theme.composables.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.samuelbaldasso.ifoodclone.ui.cart.CartScreen
import com.samuelbaldasso.ifoodclone.ui.restaurant.RestaurantDetailScreen
import com.samuelbaldasso.ifoodclone.ui.theme.composables.home.HomeScreen
import com.samuelbaldasso.ifoodclone.ui.theme.composables.orders.OrdersScreen
import com.samuelbaldasso.ifoodclone.ui.theme.composables.profile.ProfileScreen
import com.samuelbaldasso.ifoodclone.ui.theme.composables.search.SearchScreen

sealed class BottomNavTab(val route: String, val label: String, val icon: ImageVector) {
    data object Home : BottomNavTab("home", "Início", Icons.Default.Home)
    data object Search : BottomNavTab("search", "Busca", Icons.Default.Search)
    data object Orders : BottomNavTab("orders", "Pedidos", Icons.Default.ShoppingCart)
    data object Profile : BottomNavTab("profile", "Perfil", Icons.Default.Person)
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val tabs = listOf(
        BottomNavTab.Home,
        BottomNavTab.Search,
        BottomNavTab.Orders,
        BottomNavTab.Profile
    )

    val shouldShowBottomBar = tabs.any { it.route == currentRoute }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (shouldShowBottomBar) {
                NavigationBar {
                    tabs.forEach { tab ->
                        val isSelected = currentRoute == tab.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != tab.route) {
                                    navController.navigate(tab.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = { Icon(imageVector = tab.icon, contentDescription = tab.label) },
                            label = { Text(text = tab.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavTab.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavTab.Home.route) {
                HomeScreen(
                    onRestaurantClick = { restaurantId ->
                        navController.navigate("restaurant/$restaurantId")
                    },
                    onViewCartClick = {
                        navController.navigate("cart")
                    }
                )
            }
            composable(BottomNavTab.Search.route) {
                SearchScreen(
                    onRestaurantClick = { restaurantId ->
                        navController.navigate("restaurant/$restaurantId")
                    }
                )
            }
            composable(BottomNavTab.Orders.route) {
                OrdersScreen(
                    onTrackOrderClick = { orderId ->
                        navController.navigate("tracking/$orderId")
                    },
                    onRestaurantClick = { restaurantId ->
                        navController.navigate("restaurant/$restaurantId")
                    }
                )
            }
            composable(BottomNavTab.Profile.route) {
                ProfileScreen()
            }
            composable(
                route = "restaurant/{restaurantId}",
                arguments = listOf(
                    navArgument("restaurantId") { type = NavType.StringType }
                )
            ) {
                RestaurantDetailScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onViewCartClick = {
                        navController.navigate("cart")
                    }
                )
            }
            composable("cart") {
                CartScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onAddMoreItems = { restaurantId ->
                        navController.navigate("restaurant/$restaurantId") {
                            popUpTo("cart") { inclusive = true }
                        }
                    },
                    onProceedToCheckout = {
                        navController.navigate("checkout")
                    }
                )
            }
            composable("checkout") {
                com.samuelbaldasso.ifoodclone.ui.checkout.CheckoutScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onOrderPlaced = { orderId ->
                        navController.navigate("tracking/$orderId") {
                            popUpTo(BottomNavTab.Home.route) { inclusive = false }
                        }
                    }
                )
            }
            composable(
                route = "tracking/{orderId}",
                arguments = listOf(
                    navArgument("orderId") { type = NavType.StringType }
                )
            ) {
                com.samuelbaldasso.ifoodclone.ui.tracking.OrderTrackingScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
