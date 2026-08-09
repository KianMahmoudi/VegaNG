package com.kian.mahmoudi.vegang.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.kian.mahmoudi.vegang.ui.navigation.AppNavigation

@Composable
fun VpnApp() {
    AppNavigation(rememberNavController())
}