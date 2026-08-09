package com.kian.mahmoudi.vegang.data.config

import android.util.Log
import com.kian.mahmoudi.vegang.dto.ProfileItem
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

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

        private const val MAX_TCP_PING_THREADS = 50
        private const val MAX_REAL_PING_THREADS = 15
    }

    val client = OkHttpClient()

    suspend fun getConfigs(
        count: Int,
        isDuplicate: suspend (ProfileItem) -> Boolean
    ): List<ProfileItem> {
        val configs = mutableListOf<ProfileItem>()
        val finalConfigs = mutableListOf<ProfileItem>()
        val tcpConfigs = mutableListOf<ProfileItem>()
        val tcpFailed = mutableSetOf<ProfileItem>()
        val realFailed = mutableSetOf<ProfileItem>()

        while (finalConfigs.size < count) {
            if (configs.isEmpty()) {
                val newConfigs = getConfigsFromSources()
                    .filterNot { it in tcpFailed || it in realFailed || it in tcpConfigs || it in finalConfigs }

                if (newConfigs.isEmpty()) break

                newConfigs.forEach { config ->
                    if (isDuplicate(config)) {
                        Log.d(TAG, "Config Duplicate: ${config.remarks}")
                    } else {
                        configs.add(config)
                    }
                }
            }
            Log.d(TAG, "configs.size: ${configs.size}")

            if (tcpConfigs.size < count * 10 && configs.isNotEmpty()) {
                val neededTcp = (count * 10 - tcpConfigs.size).coerceAtLeast(1)
                val newTcpConfigs = testTcpConfigs(configs, neededTcp)

                tcpConfigs.addAll(newTcpConfigs.passed)
                tcpFailed.addAll(newTcpConfigs.failed)
                configs.removeAll(newTcpConfigs.passed)
                configs.removeAll(newTcpConfigs.failed)
                Log.d(TAG, "tcpConfigs.size: ${tcpConfigs.size}")
            }

            val neededReal = (count - finalConfigs.size).coerceAtLeast(1)
            val realConfigs = testRealDelayConfigs(tcpConfigs, neededReal)

            finalConfigs.addAll(realConfigs.passed)
            realFailed.addAll(realConfigs.failed)
            tcpConfigs.removeAll(realConfigs.passed)
            tcpConfigs.removeAll(realConfigs.failed)
            Log.d(TAG, "finalConfigs.size: ${finalConfigs.size}")

        }

        return finalConfigs
    }

    suspend fun testRealDelayConfigs(
        configs: List<ProfileItem>,
        count: Int
    ): TestResult {
        val realConfigs = mutableListOf<ProfileItem>()
        val realFailed = mutableListOf<ProfileItem>()
        val semaphore = Semaphore(MAX_REAL_PING_THREADS)
        val mutex = Mutex()
        try {
            coroutineScope {
                for (config in configs) {
                    launch(Dispatchers.IO) {
                        semaphore.withPermit {
                            val ping = ConfigTester.realPing(config)

                            val shouldCancel = mutex.withLock {
                                if (ping == -1L) {
                                    realFailed.add(config)
                                    false
                                } else if (realConfigs.size >= count) {
                                    true
                                } else {
                                    realConfigs.add(config)
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

    suspend fun getConfigsFromSources(): List<ProfileItem> {
        val configs = mutableListOf<ProfileItem>()
        withContext(Dispatchers.IO) {
            for (provider in CONFIG_PROVIDERS) {
                val request = Request.Builder().url(provider).build()

                val response = client.newCall(request).execute().body.string()


                val configsLine =
                    response.trim().splitToSequence('\n').filter { it.isNotEmpty() }.toList()
                        .shuffled()

                for (config in configsLine) {
//                    Log.i(TAG, "${configsLine.indexOf(config)}'s config is: $config")

                    val profileItem = ConfigParser.parseToProfileItem(config)
                    profileItem?.let { configs.add(it) }

//                    Log.i(TAG, "ProfileItem: $profileItem")
                }

            }
        }
        return configs
    }

}