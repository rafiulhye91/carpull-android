package com.example.carpull.view.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.carpull.presentation.model.CarMake
import com.example.carpull.view.CarMakeDetailsScreen
import com.example.carpull.view.CarMakeFormScreen
import com.example.carpull.view.HomeScreen
import com.example.carpull.view.navigation.Routes.HOME
import kotlinx.serialization.Serializable


@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onMakeClick = { carMake ->
                    navController.navigate(carMake)
                },
                onEditClick = { carMake ->
                    navController.navigate(CarMakeFormRoute(carMake.id))
                },
            )
        }

        composable<CarMake> {
            CarMakeDetailsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<CarMakeFormRoute> {
            CarMakeFormScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}

object Routes {
    const val HOME = "home"
}

@Serializable
data class CarMakeFormRoute(val localId: Long? = null)
