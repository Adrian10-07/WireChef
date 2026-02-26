package com.example.wirechef.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wirechef.features.order.presentation.screens.ChefDashboardScreen
import com.example.wirechef.features.order.presentation.screens.WaiterMenuScreen
import com.example.wirechef.features.user.presentation.screens.LoginScreen

@Composable
fun WireChefNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppScreens.Login.route
    ) {
        composable(AppScreens.Login.route) {
            LoginScreen(
                onLoginSuccess = { role ->
                    if (role == "waiter") {
                        navController.navigate(AppScreens.WaiterMenu.route) {
                            // popUpTo evita que el usuario regrese al login dándole al botón "Atrás" del celular
                            popUpTo(AppScreens.Login.route) { inclusive = true }
                        }
                    } else if (role == "chef") {
                        navController.navigate(AppScreens.ChefDashboard.route) {
                            popUpTo(AppScreens.Login.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(AppScreens.WaiterMenu.route) {
            WaiterMenuScreen()
        }

        composable(AppScreens.ChefDashboard.route) {
            ChefDashboardScreen()
        }
    }
}