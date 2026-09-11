package com.kian.mahmoudi.vegang.data.config

import android.util.Log
import com.kian.mahmoudi.vegang.dto.ProfileItem
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

data class TestResult(
    val passed: List<ProfileItem>,
    val failed: List<ProfileItem>
)

class ConfigProvider {

    companion object {

        private const val TAG = "ConfigProvider"
        private val CONFIG_PROVIDERS = listOf(
            "https://raw.githubusercontent.com/barry-far/V2ray-Config/refs/heads/main/All_Configs_Sub.txt",
            "https://raw.githubusercontent.com/Epodonios/v2ray-configs/main/All_Configs_Sub.txt",
            "https://raw.githubusercontent.com/4n0nymou3/multi-proxy-config-fetcher/refs/heads/main/configs/proxy_configs.txt",
            "https://raw.githubusercontent.com/flaafix/AetrisVPN-white-list-lite/refs/heads/main/AetrisVPN.txt",
            "https://raw.githubusercontent.com/flaafix/AetrisVPN-black-list/refs/heads/main/configs.txt",
            "https://raw.githubusercontent.com/flaafix/AetrisVPN/refs/heads/main/AetrisVPN.txt",
            "https://raw.githubusercontent.com/igareck/vpn-configs-for-russia/refs/heads/main/Vless-Reality-White-Lists-Rus-Mobile.txt",
            "https://github.com/AvenCores/goida-vpn-configs/raw/refs/heads/main/githubmirror/26.txt",
            "https://raw.githubusercontent.com/ByeWhiteLists/ByeWhiteLists2/refs/heads/main/ByeWhiteLists2.txt",
            "https://raw.githubusercontent.com/kort0881/vpn-checker-backend/main/checked/RU_Best/ru_white_part3.txt",
            "https://raw.githubusercontent.com/ewecrow78-gif/whitelist1/main/list.txt",
            "https://raw.githubusercontent.com/ShadowException/VPN/refs/heads/main/configs/VPN-cat",
            "https://raw.githubusercontent.com/RKPchannel/RKP_bypass_configs/refs/heads/main/whitelist.txt",
            "https://raw.githubusercontent.com/luxxuria/harvester/main/top_600.txt",
            "https://raw.githubusercontent.com/VOID-Anonymity/V.O.I.D-VPN_Bypass/refs/heads/main/url_work.txt"
        )

        private const val MAX_TCP_PING_THREADS = 100
    }

    val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()

    val fetchStatus = MutableStateFlow(FetchStatus())

    suspend fun getConfigs(
        count: Int,
        isDuplicate: suspend (ProfileItem) -> Boolean
    ): List<ProfileItem> {

        val minTarget = maxOf(count, 5)

        fetchStatus.value = FetchStatus(stage = FetchStage.Downloading)
        val allConfigs = getConfigsFromSources()
            .filterNot { isDuplicate(it) }
            .shuffled()

        if (allConfigs.isEmpty()) {
            fetchStatus.value = FetchStatus(stage = FetchStage.Done, healthy = 0)
            return emptyList()
        }
        fetchStatus.update { it.copy(totalFetched = allConfigs.size) }
        Log.d(TAG, "Total unique configs from sources: ${allConfigs.size}")

        val finalConfigs = mutableListOf<ProfileItem>()
        var untested = allConfigs
        var tcpPassed = mutableListOf<ProfileItem>()


        var attempt = 0
        while (finalConfigs.size < minTarget && attempt < 3 && untested.isNotEmpty()) {
            attempt++
            fetchStatus.update {
                it.copy(stage = FetchStage.TcpTesting, attempt = attempt, tested = 0)
            }

            val neededTcp = (minTarget * 10 - tcpPassed.size).coerceAtLeast(1)
            val tcpResult = testTcpConfigs(untested, neededTcp)
            tcpPassed.addAll(tcpResult.passed)
            untested = untested.filterNot { it in tcpResult.passed || it in tcpResult.failed }

            if (tcpPassed.isEmpty()) break

            fetchStatus.update { it.copy(stage = FetchStage.RealPinging) }

            val neededReal = (minTarget - finalConfigs.size).coerceAtLeast(1)
            val realResult = testRealDelayConfigs(tcpPassed, neededReal)
            finalConfigs.addAll(realResult.passed)
            tcpPassed = tcpPassed.filterNot { it in realResult.passed || it in realResult.failed }.toMutableList()

            fetchStatus.update { it.copy(healthy = finalConfigs.size) }

            if (finalConfigs.isEmpty()) break
        }

        Log.d(TAG, "Final configs found: ${finalConfigs.size}")
        return finalConfigs.sortedBy { it.latency }.take(count)
    }

    suspend fun testRealDelayConfigs(
        configs: List<ProfileItem>,
        count: Int
    ): TestResult {
        val realConfigs = mutableListOf<ProfileItem>()
        val realFailed = mutableListOf<ProfileItem>()
        val mutex = Mutex()
        try {
            coroutineScope {
                for (config in configs) {
                    launch(Dispatchers.IO) {
                        val ping = ConfigTester.realPing(config)
                        fetchStatus.update { it.copy(tested = it.tested + 1) }

                        val shouldCancel = mutex.withLock {
                            if (ping == -1L) {
                                realFailed.add(config)
                                false
                            } else if (realConfigs.size >= count) {
                                true
                            } else {
                                realConfigs.add(config.copy(latency = ping))
                                fetchStatus.update { it.copy(healthy = it.healthy + 1) }
                                Log.i(
                                    TAG,
                                    "Added config: ${config.remarks} with ping: $ping to tcpConfigs"
                                )
                                Log.i(TAG, "RealConfigs.Size: ${realConfigs.size}")
                                realConfigs.size >= count
                            }
                        }

                        if (shouldCancel) {
                            this@coroutineScope.cancel()
                        }
                    }
                }
            }
        } catch (_: CancellationException) {

        }

        return TestResult(realConfigs, realFailed)
    }

    suspend fun testTcpConfigs(
        configs: List<ProfileItem>,
        count: Int
    ): TestResult {
        val passed = mutableListOf<ProfileItem>()
        val failed = mutableListOf<ProfileItem>()
        val semaphore = Semaphore(MAX_TCP_PING_THREADS)
        val mutex = Mutex()
        try {
            coroutineScope {
                for (config in configs) {
                    launch(Dispatchers.IO) {
                        semaphore.withPermit {
                            val ping = ConfigTester.tcpPing(config)

                            val shouldCancel = mutex.withLock {
                                if (ping == -1L) {
                                    failed.add(config)
                                    false
                                } else if (passed.size >= count) {
                                    true
                                } else {
                                    passed.add(config)
                                    fetchStatus.update { it.copy(tcpPassed = it.tcpPassed + 1) }
                                    Log.i(
                                        TAG,
                                        "Added config: ${config.remarks} with ping: $ping to tcpConfigs"
                                    )
                                    Log.d(TAG, "TcpConfigs.Size: ${passed.size}")
                                    passed.size >= count
                                }
                            }

                            if (shouldCancel) {
                                this@coroutineScope.cancel()
                            }

                        }
                    }
                }
            }
        } catch (_: kotlin.coroutines.cancellation.CancellationException) {

        }

        return TestResult(passed, failed)
    }

    suspend fun getConfigsFromSources(): List<ProfileItem> = withContext(Dispatchers.IO) {
        coroutineScope {
            CONFIG_PROVIDERS.map { provider ->
                async {
                    try {
                        val request = Request.Builder().url(provider).build()
                        val response = client.newCall(request).execute().body.string()
                        response.trim().splitToSequence('\n')
                            .filter { it.isNotEmpty() }.toList().shuffled()
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to fetch from source: $provider", e)
                        emptyList()
                    }
                }
            }.awaitAll().flatten().mapNotNull { ConfigParser.parseToProfileItem(it) }
        }
    }

}