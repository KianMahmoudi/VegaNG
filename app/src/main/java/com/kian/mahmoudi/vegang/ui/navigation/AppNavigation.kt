package com.kian.mahmoudi.vegang.ui.navigation

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kian.mahmoudi.vegang.enums.ConfigSort
import com.kian.mahmoudi.vegang.extention.findActivity
import com.kian.mahmoudi.vegang.ui.screen.HomeScreen
import com.kian.mahmoudi.vegang.ui.viewmodel.HomeUiEvent
import com.kian.mahmoudi.vegang.ui.viewmodel.HomeViewModel
import dev.dev7.lib.v2ray.V2rayController
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        modifier = Modifier,
        navController = navController,
        startDestination = Screen.HOME_SCREEN.route
    ) {
        composable(route = Screen.HOME_SCREEN.route) {
            HomeRoute()
        }
    }
}

@Composable
fun HomeRoute(homeViewModel: HomeViewModel = hiltViewModel()) {

    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val vpnState by homeViewModel.vpnState.collectAsStateWithLifecycle()
    val traffic by homeViewModel.traffic.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(homeViewModel) {
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.event.collect { event ->
                    when (event) {
                        is HomeUiEvent.LaunchVpnPermission -> {
                            V2rayController.startV2ray(
                                context.findActivity() as AppCompatActivity,
                                event.configName,
                                event.config,
                                null
                            )
                        }
                    }
                }
            }
        }
    }

    HomeScreen(
        uiState = uiState,
        vpnState = vpnState,
        traffic = traffic,
        onConnect = { homeViewModel.connect(it) },
        onDisconnect = { homeViewModel.disconnect() },
        onGetConfigs = { homeViewModel.getConfigs(10) },
        onTestLatency = { homeViewModel.testConfig(it) },
        onTestLatencyAll = { homeViewModel.testAllConfigs() },
        onSortConfigs = { homeViewModel.sortConfigs(ConfigSort.LATENCY_ASC) },
        onDeleteConfig = { homeViewModel.deleteConfig(it) },
        onCopyConfig = { homeViewModel.copyToClipboard(it) },
        onDeleteNonWorking = { homeViewModel.deleteNonWorkingConfigs() },
        onShareConfig = { homeViewModel.shareConfig(it) }
    )

}