package com.kian.mahmoudi.vegang.data.config

import android.util.Log
import com.kian.mahmoudi.vegang.dto.ProfileItem
import com.kian.mahmoudi.vegang.enums.EConfigType
import com.kian.mahmoudi.vegang.fmt.WireguardFmt
import com.kian.mahmoudi.vegang.util.Constants
import com.kian.mahmoudi.vegang.util.Constants.DEFAULT_FINGERPRINT
import com.kian.mahmoudi.vegang.util.Constants.DEFAULT_PORT
import com.kian.mahmoudi.vegang.util.Constants.DEFAULT_SECURITY
import com.kian.mahmoudi.vegang.util.Constants.DEFAULT_SS_METHOD
import com.v2ray.ang.fmt.Hysteria2Fmt
import com.v2ray.ang.fmt.ShadowsocksFmt
import com.v2ray.ang.fmt.SocksFmt
import com.v2ray.ang.fmt.TrojanFmt
import com.v2ray.ang.fmt.VlessFmt
import com.v2ray.ang.fmt.VmessFmt
import org.json.JSONArray
import org.json.JSONObject
import kotlin.getValue

object ConfigParser {

    // Parser mapping for different config types (lazy initialized)
    private val configFmtParsers: Map<String, (String) -> ProfileItem?> by lazy {
        mapOf(
            EConfigType.VMESS.protocolScheme to VmessFmt::parse,
            EConfigType.SHADOWSOCKS.protocolScheme to ShadowsocksFmt::parse,
            EConfigType.SOCKS.protocolScheme to SocksFmt::parse,
            Constants.SOCKS4 to SocksFmt::parse,
            Constants.SOCKS5 to SocksFmt::parse,
            EConfigType.TROJAN.protocolScheme to TrojanFmt::parse,
            EConfigType.VLESS.protocolScheme to VlessFmt::parse,
            EConfigType.WIREGUARD.protocolScheme to WireguardFmt::parse,
            EConfigType.HYSTERIA2.protocolScheme to Hysteria2Fmt::parse,
            Constants.HY2 to Hysteria2Fmt::parse
        )
    }

    /** Dispatches a profile to protocol-specific outbound builder. */
    fun configJsonParser(profileItem: ProfileItem): JSONObject? {
        val outbound = when (profileItem.configType) {
            EConfigType.VMESS -> createVmessOutbound(profileItem)
            EConfigType.SHADOWSOCKS -> createShadowsocksOutbound(profileItem)
            EConfigType.SOCKS -> createSocksOutbound(profileItem)
            EConfigType.VLESS -> createVlessOutbound(profileItem)
            EConfigType.TROJAN -> createTrojanOutbound(profileItem)
            EConfigType.HTTP -> createHttpOutbound(profileItem)
            else -> null
        }

        outbound ?: return null
        return outbound
    }

    fun parseToProfileItem(str: String): ProfileItem? {
        val line = str.trim()
        try {
            if (line.isBlank()) {
                return null
            }

            val config = configFmtParsers.firstNotNullOfOrNull { (scheme, parser) ->
                if (line.startsWith(scheme)) parser(line) else null
            }

            return config
        } catch (e: Exception) {
            Log.e(Constants.TAG, "Failed to parse config", e)
            return null
        }
    }

    fun toV2rayJson(profile: ProfileItem): String {
        val config = JSONObject()

        // Log
        config.put("log", JSONObject().apply {
            put("loglevel", "warning")
        })

        // DNS
        config.put("dns", JSONObject().apply {
            put("servers", JSONArray().apply {
                put("8.8.8.8")
                put("8.8.4.4")
                put("1.1.1.1")
            })
            put("queryStrategy", "UseIPv4")
        })

        // Inbounds
        config.put("inbounds", JSONArray().apply {
            // SOCKS
            put(JSONObject().apply {
                put("tag", "socks")
                put("protocol", "socks")
                put("listen", "127.0.0.1")
                put("port", 10808)
                put("settings", JSONObject().apply {
                    put("auth", "noauth")
                    put("udp", true)
                    put("userLevel", 8)
                })
                put("sniffing", JSONObject().apply {
                    put("enabled", true)
                    put("destOverride", JSONArray().apply {
                        put("http")
                        put("tls")
                    })
                })
            })

            // HTTP
            put(JSONObject().apply {
                put("tag", "http")
                put("protocol", "http")
                put("listen", "127.0.0.1")
                put("port", 10809)
                put("settings", JSONObject().apply {
                    put("userLevel", 8)
                })
            })
        })

        // Outbounds
        config.put("outbounds", JSONArray().apply {
            put(configJsonParser(profile))
            put(createDirectOutbound())
            put(createBlockOutbound())
        })

        // Routing
        config.put("routing", JSONObject().apply {
            put("domainStrategy", "IPIfNonMatch")
            put("rules", JSONArray().apply {
                put(JSONObject().apply {
                    put("type", "field")
                    put("ip", JSONArray().apply {
                        put("geoip:private")
                    })
                    put("outboundTag", "direct")
                })
                put(JSONObject().apply {
                    put("type", "field")
                    put("port", "53")
                    put("outboundTag", "proxy")
                })
            })
        })

        return config.toString(2)
    }

    fun generateConfigForSpeedTest(profile: ProfileItem): String? {
        return try {

            val proxyOutbound = configJsonParser(profile)
                ?: throw IllegalArgumentException("Failed to create proxy outbound")

            proxyOutbound.optJSONObject("mux")?.put("enabled", false)

            if (proxyOutbound.optString("protocol") == "shadowsocks") {
                val servers = proxyOutbound.optJSONObject("settings")?.optJSONArray("servers")
                servers?.optJSONObject(0)?.remove("uot")
            }

            val config = JSONObject().apply {
                put("log", JSONObject().apply { put("loglevel", "warning") })

                put("dns", JSONObject().apply {
                    put("servers", JSONArray().apply {
                        put("1.1.1.1")
                        put("8.8.8.8")
                        put("localhost")
                    })
                    put("queryStrategy", "UseIPv4")
                })

                put("outbounds", JSONArray().apply {
                    put(proxyOutbound)
                    put(JSONObject().apply {
                        put("tag", "direct")
                        put("protocol", "freedom")
                        put("settings", JSONObject())
                    })
                })
            }

            config.toString()
        } catch (e: Exception) {
            Log.e("Outbound Builder", "❌ Failed to generate speed test config", e)
            null
        }
    }

    private fun createVmessOutbound(profile: ProfileItem): JSONObject {
        return JSONObject().apply {
            put("tag", "proxy")
            put("protocol", "vmess")

            put("settings", JSONObject().apply {
                put("vnext", JSONArray().apply {
                    put(JSONObject().apply {
                        put("address", profile.server)
                        put("port", profile.serverPort?.toIntOrNull() ?: DEFAULT_PORT)
                        put("users", JSONArray().apply {
                            put(JSONObject().apply {
                                put("id", profile.password)
                                put("alterId", 0)
                                put("security", profile.method ?: DEFAULT_SECURITY)
                                put("level", 8)
                            })
                        })
                    })
                })
            })

            put("streamSettings", createStreamSettings(profile))
            put("mux", createMuxConfig(false))
        }
    }

    private fun createVlessOutbound(profile: ProfileItem): JSONObject {
        return JSONObject().apply {
            put("tag", "proxy")
            put("protocol", "vless")

            put("settings", JSONObject().apply {
                put("vnext", JSONArray().apply {
                    put(JSONObject().apply {
                        put("address", profile.server)
                        put("port", profile.serverPort?.toIntOrNull() ?: DEFAULT_PORT)
                        put("users", JSONArray().apply {
                            put(JSONObject().apply {
                                put("id", profile.password)
                                put("encryption", "none")
                                put("level", 8)
                                profile.flow?.takeIf { it.isNotEmpty() }?.let { put("flow", it) }
                            })
                        })
                    })
                })
            })

            put("streamSettings", createStreamSettings(profile))
            put("mux", createMuxConfig(false))
        }
    }

    private fun createTrojanOutbound(profile: ProfileItem): JSONObject {
        val secureProfile =
            if (profile.security.isNullOrEmpty() || profile.security == "none") {
                profile.copy(security = "tls")
            } else profile

        return JSONObject().apply {
            put("tag", "proxy")
            put("protocol", "trojan")

            put("settings", JSONObject().apply {
                put("servers", JSONArray().apply {
                    put(JSONObject().apply {
                        put("address", profile.server)
                        put("port", profile.serverPort?.toIntOrNull() ?: DEFAULT_PORT)
                        put("password", profile.password)
                        put("level", 8)
                        profile.flow?.takeIf { it.isNotEmpty() }?.let { put("flow", it) }
                    })
                })
            })

            put("streamSettings", createStreamSettings(secureProfile))
            put("mux", createMuxConfig(false))
        }
    }

    private fun createShadowsocksOutbound(profile: ProfileItem): JSONObject {
        return JSONObject().apply {
            put("tag", "proxy")
            put("protocol", "shadowsocks")

            put("settings", JSONObject().apply {
                put("servers", JSONArray().apply {
                    put(JSONObject().apply {
                        put("address", profile.server)
                        put("port", profile.serverPort?.toIntOrNull() ?: DEFAULT_PORT)
                        put("password", profile.password)
                        put("method", profile.method ?: DEFAULT_SS_METHOD)
                        put("level", 8)
                    })
                })
            })

            if (!profile.network.isNullOrEmpty() && profile.network != "tcp") {
                put("streamSettings", (profile))
            }
        }
    }

    private fun createSocksOutbound(profile: ProfileItem): JSONObject {
        return JSONObject().apply {
            put("tag", "proxy")
            put("protocol", "socks")

            put("settings", JSONObject().apply {
                put("servers", JSONArray().apply {
                    put(JSONObject().apply {
                        put("address", profile.server)
                        put("port", profile.serverPort?.toIntOrNull() ?: 1080)
                        if (!profile.username.isNullOrEmpty() && !profile.password.isNullOrEmpty()) {
                            put("users", JSONArray().apply {
                                put(JSONObject().apply {
                                    put("user", profile.username)
                                    put("pass", profile.password)
                                })
                            })
                        }
                    })
                })
            })

            put("streamSettings", JSONObject().apply {
                put("network", "tcp")
                put("security", "none")
            })
        }
    }

    private fun createHttpOutbound(profile: ProfileItem): JSONObject {
        return JSONObject().apply {
            put("tag", "proxy")
            put("protocol", "http")

            put("settings", JSONObject().apply {
                put("servers", JSONArray().apply {
                    put(JSONObject().apply {
                        put("address", profile.server)
                        put("port", profile.serverPort?.toIntOrNull() ?: 8080)
                        if (!profile.username.isNullOrEmpty() && !profile.password.isNullOrEmpty()) {
                            put("users", JSONArray().apply {
                                put(JSONObject().apply {
                                    put("user", profile.username)
                                    put("pass", profile.password)
                                })
                            })
                        }
                    })
                })
            })

            put("streamSettings", JSONObject().apply {
                put("network", "tcp")
                put("security", "none")
            })
        }
    }

    private fun createStreamSettings(profile: ProfileItem): JSONObject {
        return JSONObject().apply {
            val network = profile.network?.lowercase() ?: "tcp"
            put("network", network)

            populateSecuritySettings(this, profile)

            when (network) {
                "tcp" -> put("tcpSettings", createTcpSettings(profile))
                "ws", "websocket" -> put("wsSettings", createWsSettings(profile))
                "grpc" -> put("grpcSettings", createGrpcSettings(profile))
                "h2", "http" -> put("httpSettings", createH2Settings(profile))
                "kcp", "mkcp" -> put("kcpSettings", createKcpSettings(profile))
                "httpupgrade" -> put("httpupgradeSettings", createHttpUpgradeSettings(profile))
                "quic" -> put("quicSettings", createQuicSettings(profile))
            }
        }
    }

    private fun populateSecuritySettings(streamSettings: JSONObject, profile: ProfileItem) {
        when (profile.security?.lowercase()) {
            "tls" -> {
                streamSettings.put("security", "tls")
                streamSettings.put("tlsSettings", createTlsSettings(profile))
            }

            "reality" -> {
                streamSettings.put("security", "reality")
                streamSettings.put("realitySettings", createRealitySettings(profile))
            }

            "xtls" -> {
                streamSettings.put("security", "tls")
                streamSettings.put("tlsSettings", createTlsSettings(profile))
            }

            else -> {
                streamSettings.put("security", "none")
            }
        }
    }

    private fun createTlsSettings(profile: ProfileItem): JSONObject {
        return JSONObject().apply {
            put("allowInsecure", profile.insecure ?: false)

            val host = profile.host?.takeIf { it.isNotBlank() }
            val rawSni = profile.sni?.takeIf { it.isNotBlank() }
            var serverName = rawSni ?: host ?: profile.server

            if (isIp(serverName) && !host.isNullOrBlank() && isIp(
                    host
                )
            ) {
                serverName = host
            }
            put("serverName", serverName)

            val fp = profile.fingerPrint?.takeIf { it.isNotEmpty() } ?: DEFAULT_FINGERPRINT
            put("fingerprint", fp)

            val network = profile.network?.lowercase() ?: "tcp"
            val finalAlpn: List<String> = when (network) {
                "ws", "websocket", "httpupgrade" -> listOf("http/1.1")
                else -> {
                    profile.alpn
                        ?.takeIf { it.isNotBlank() }
                        ?.split(",")
                        ?.map { it.trim() }
                        ?.filter { it.isNotBlank() }
                        ?: listOf("h2", "http/1.1")
                }
            }

            if (finalAlpn.isNotEmpty()) {
                put("alpn", finalAlpn.toJsonArray())
            }
        }
    }

    private fun createRealitySettings(profile: ProfileItem): JSONObject {
        return JSONObject().apply {
            put("show", false)

            val fp =
                profile.fingerPrint?.takeIf { it.isNotBlank() } ?: DEFAULT_FINGERPRINT
            put("fingerprint", fp)

            val host = profile.host?.takeIf { it.isNotBlank() }
            val rawSni = profile.sni?.takeIf { it.isNotBlank() }
            var serverName = rawSni ?: host ?: profile.server

            if (isIp(serverName) && !host.isNullOrBlank() && !isIp(
                    host
                )
            ) {
                serverName = host
            }
            put("serverName", serverName)

            profile.publicKey?.takeIf { it.isNotEmpty() }?.let { put("publicKey", it) }
            profile.shortId?.takeIf { it.isNotEmpty() }?.let { put("shortId", it) }
            profile.spiderX?.takeIf { it.isNotEmpty() }?.let { put("spiderX", it) }
        }
    }

    private fun createTcpSettings(profile: ProfileItem): JSONObject {
        return JSONObject().apply {
            put("header", JSONObject().apply {
                val headerType = profile.headerType?.lowercase() ?: "none"
                put("type", headerType)
                if (headerType == "http") {
                    put("request", createHttpRequest(profile))
                }
            })
        }
    }

    private fun createHttpRequest(profile: ProfileItem): JSONObject {
        return JSONObject().apply {
            put("version", "1.1")
            put("method", "GET")
            put("path", JSONArray().apply {
                put(profile.path?.takeIf { it.isNotEmpty() } ?: "/")
            })
            put("headers", JSONObject().apply {
                val host = profile.host?.takeIf { it.isNotEmpty() } ?: profile.server.orEmpty()
                put("Host", JSONArray().apply { put(host) })
                put("User-Agent", JSONArray().apply {
                    put("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                })
                put("Accept-Encoding", JSONArray().apply { put("gzip, deflate") })
                put("Connection", JSONArray().apply { put("keep-alive") })
            })
        }
    }

    private fun createWsSettings(profile: ProfileItem): JSONObject {
        return JSONObject().apply {
            val rawPath = profile.path?.takeIf { it.isNotEmpty() } ?: "/"
            val finalPath = if (rawPath.startsWith("/")) rawPath else "/$rawPath"
            put("path", finalPath)

            put("headers", JSONObject().apply {
                // ✅ WebSocket requires a domain Host header for Cloudflare (521 fix)
                val host = profile.host?.takeIf { it.isNotBlank() }
                    ?: profile.sni?.takeIf { it.isNotBlank() }
                    ?: if (!isIp(profile.server)) profile.server else ""

                if (!host.isNullOrBlank()) {
                    put("Host", host)
                }
            })
        }
    }

    private fun createGrpcSettings(profile: ProfileItem): JSONObject {
        return JSONObject().apply {
            put(
                "serviceName",
                profile.serviceName?.takeIf { it.isNotEmpty() }
                    ?: profile.path?.takeIf { it.isNotEmpty() }
                    ?: ""
            )
            put("multiMode", profile.mode == "multi")
            put("idle_timeout", 60)
            put("health_check_timeout", 20)
            profile.authority?.takeIf { it.isNotEmpty() }?.let { put("authority", it) }
        }
    }

    private fun createH2Settings(profile: ProfileItem): JSONObject {
        return JSONObject().apply {
            put("path", profile.path?.takeIf { it.isNotEmpty() } ?: "/")
            put("host", JSONArray().apply {
                val host = profile.host?.takeIf { it.isNotEmpty() } ?: profile.server.orEmpty()
                put(host)
            })
        }
    }

    private fun createDirectOutbound(): JSONObject {
        return JSONObject().apply {
            put("tag", "direct")
            put("protocol", "freedom")
            put("settings", JSONObject())
        }
    }

    private fun createBlockOutbound(): JSONObject {
        return JSONObject().apply {
            put("tag", "block")
            put("protocol", "blackhole")
            put("settings", JSONObject().apply {
                put("response", JSONObject().apply {
                    put("type", "none")
                })
            })
        }
    }

    private fun createKcpSettings(profile: ProfileItem): JSONObject {
        return JSONObject().apply {
            put("mtu", 1350)
            put("tti", 50)
            put("uplinkCapacity", 12)
            put("downlinkCapacity", 100)
            put("congestion", false)
            put("readBufferSize", 2)
            put("writeBufferSize", 2)
            put("header", JSONObject().apply {
                put("type", profile.headerType ?: "none")
            })
            profile.seed?.takeIf { it.isNotEmpty() }?.let { put("seed", it) }
        }
    }

    private fun createHttpUpgradeSettings(profile: ProfileItem): JSONObject {
        return JSONObject().apply {
            put("path", profile.path?.takeIf { it.isNotEmpty() } ?: "/")
            put("host", profile.host?.takeIf { it.isNotEmpty() } ?: profile.server.orEmpty())
        }
    }

    private fun createQuicSettings(profile: ProfileItem): JSONObject {
        return JSONObject().apply {
            put("security", profile.host ?: "none")
            put("key", profile.path ?: "")
            put("header", JSONObject().apply {
                put("type", profile.headerType ?: "none")
            })
        }
    }

    private fun createMuxConfig(enabled: Boolean): JSONObject {
        return JSONObject().apply {
            put("enabled", false)
        }
    }

    private fun List<String>.toJsonArray(): JSONArray {
        return JSONArray().apply {
            this@toJsonArray.forEach { put(it) }
        }
    }

    private fun isIp(value: String?): Boolean {
        val v = value?.trim().orEmpty()
        if (v.isEmpty()) return false
        val ipv4 = Regex("""^\d{1,3}(\.\d{1,3}){3}$""")
        return ipv4.matches(v) || (v.contains(":") && !v.contains("."))
    }

}