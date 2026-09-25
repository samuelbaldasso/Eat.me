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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
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

    Scaffold(
        modifier = modifier,
        bottomBar = {
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
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavTab.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavTab.Home.route) {
                HomeScreen(
                    onRestaurantClick = {
                        // Navegação para detalhe do restaurante
                    }
                )
            }
            composable(BottomNavTab.Search.route) {
                SearchScreen()
            }
            composable(BottomNavTab.Orders.route) {
                OrdersScreen()
            }
            composable(BottomNavTab.Profile.route) {
                ProfileScreen()
            }
        }
    }
}
