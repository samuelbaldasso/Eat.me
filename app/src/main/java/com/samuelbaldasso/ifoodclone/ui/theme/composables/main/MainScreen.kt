package com.samuelbaldasso.ifoodclone.ui.theme.composables.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.samuelbaldasso.ifoodclone.ui.theme.composables.home.HomeScreen
import com.samuelbaldasso.ifoodclone.ui.theme.composables.home.HomeViewModel

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            NavigationBar {
            }
        }
    ) {
        innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ){
            composable("home") {
                val viewModel: HomeViewModel = hiltViewModel()
                HomeScreen(viewModel = viewModel)
            }
//            composable("search") { SearchScreen() }
//            composable("cart") { CartScreen() }
//            composable("more") { MoreScreen() }
        }
    }

}
