package com.kian.mahmoudi.vegang.enums;

import com.kian.mahmoudi.vegang.util.Constants

enum class EConfigType(val value: Int, val protocolScheme: String) {
    VMESS(1, Constants.VMESS),
    CUSTOM(2, Constants.CUSTOM),
    SHADOWSOCKS(3, Constants.SHADOWSOCKS),
    SOCKS(4, Constants.SOCKS),
    VLESS(5, Constants.VLESS),
    TROJAN(6, Constants.TROJAN),
    WIREGUARD(7, Constants.WIREGUARD),

    //    TUIC(8, AppConfig.TUIC),
    HYSTERIA2(9, Constants.HYSTERIA2),
    HYSTERIA(900, Constants.HYSTERIA),
    HTTP(10, Constants.HTTP),
    POLICYGROUP(101, Constants.CUSTOM),
    PROXYCHAIN(102, Constants.CUSTOM);

    companion object {
        fun fromInt(value: Int) = entries.firstOrNull { it.value == value }
    }
}