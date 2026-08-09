package com.kian.mahmoudi.vegang.ui.navigation

sealed class Screen(val route: String) {
    object HOME_SCREEN : Screen("HOME_SCREEN")
}