package com.example.wirechef.core.navigation

sealed class AppScreens(val route: String) {
    object Login : AppScreens("login_screen")
    object WaiterMenu : AppScreens("waiter_menu_screen")
    object ChefDashboard : AppScreens("chef_dashboard_screen")
}