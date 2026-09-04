package com.kian.mahmoudi.vegang.data.config

import com.kian.mahmoudi.vegang.dto.ProfileItem
import dev.dev7.lib.v2ray.V2rayController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.system.measureTimeMillis

object ConfigTester {

    const val TAG = "ConfigTester"

    private val realPingSemaphore = Semaphore(40)

    suspend fun tcpPing(profileItem: ProfileItem): Long {
        val host = profileItem.server ?: return -1L
        val port = profileItem.serverPort?.toIntOrNull() ?: return -1L

        return withContext(Dispatchers.IO) {
            try {
                Socket().use { socket ->
                    val time = measureTimeMillis {
                        socket.connect(
                            InetSocketAddress(host, port),
                            3000
                        )
                    }
                    time
                }
            } catch (e: Exception) {
                -1L
            }
        }
    }

    suspend fun realPing(profileItem: ProfileItem): Long {
        val config = ConfigParser.generateConfigForSpeedTest(profileItem) ?: return -1L
        return withContext(Dispatchers.IO) {
            realPingSemaphore.withPermit {
                try {
                    V2rayController.getV2rayServerDelay(config)
                } catch (e: Exception) {
                    -1L
                }
            }
        }
    }

}