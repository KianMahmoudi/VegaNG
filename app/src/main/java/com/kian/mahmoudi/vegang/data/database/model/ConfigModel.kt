package com.kian.mahmoudi.vegang.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "configs")
data class ConfigModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Meta
    val configVersion: Int = 4,
    val configType: String, // Store enum as String
    val subscriptionId: String = "",
    val addedTime: Long = System.currentTimeMillis(),
    val remarks: String = "",
    val description: String? = null,

    // Server
    val server: String? = null,
    val serverPort: String? = null,

    // Auth
    val password: String? = null,
    val method: String? = null,
    val flow: String? = null,
    val username: String? = null,

    // Transport
    val network: String? = null,
    val headerType: String? = null,
    val host: String? = null,
    val path: String? = null,
    val seed: String? = null,
    val kcpMtu: Int? = null,
    val kcpTti: Int? = null,
    val quicSecurity: String? = null,
    val quicKey: String? = null,
    val mode: String? = null,
    val serviceName: String? = null,
    val authority: String? = null,
    val xhttpMode: String? = null,
    val xhttpExtra: String? = null,
    val finalMask: String? = null,

    // TLS
    val security: String? = null,
    val sni: String? = null,
    val alpn: String? = null,
    val fingerPrint: String? = null,
    val insecure: Boolean? = null,
    val echConfigList: String? = null,
    val verifyPeerCertByName: String? = null,
    val pinnedCA256: String? = null,

    // Reality
    val publicKey: String? = null,
    val shortId: String? = null,
    val spiderX: String? = null,
    val mldsa65Verify: String? = null,

    // WireGuard
    val secretKey: String? = null,
    val preSharedKey: String? = null,
    val localAddress: String? = null,
    val reserved: String? = null,
    val mtu: Int? = null,

    // Hysteria
    val obfsPassword: String? = null,
    val portHopping: String? = null,
    val portHoppingInterval: String? = null,
    val pinSHA256: String? = null,
    val bandwidthDown: String? = null,
    val bandwidthUp: String? = null,

    // Policy
    val policyGroupType: String? = null,
    val policyGroupSubscriptionId: String? = null,
    val policyGroupFilter: String? = null,
    val proxyChainProfiles: String? = null,

    // Extra
    val browserDialerMode: String? = null,

    // App-specific (not in ProfileItem)
    val isFavorite: Boolean = false,
    val latency: Long = 0,
    val lastUsedAt: Long = 0L
)