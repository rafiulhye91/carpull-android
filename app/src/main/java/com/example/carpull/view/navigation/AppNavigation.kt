package com.example.carpull.view.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.carpull.presentation.model.CarMake
import com.example.carpull.view.CarMakeDetailsScreen
import com.example.carpull.view.HomeScreen
import com.example.carpull.view.navigation.Routes.HOME


@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onMakeClick = { carMake ->
                    navController.navigate(carMake)
                }
            )
        }

        composable<CarMake> {
            CarMakeDetailsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}

object Routes {
    const val HOME = "home"
}
