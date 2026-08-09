package com.kian.mahmoudi.vegang.util

import com.kian.mahmoudi.vegang.BuildConfig

object Constants {

    /** The application's package name. */
    const val ANG_PACKAGE = BuildConfig.APPLICATION_ID
    const val TAG = BuildConfig.APPLICATION_ID

    /** Directory names used in the app's file system. */
    const val DIR_ASSETS = "assets"

    const val PORT_SOCKS = "10808"
    const val LOOPBACK = "127.0.0.1"

    const val DEFAULT_PORT = 443
    const val DEFAULT_SECURITY = "auto"
    const val DEFAULT_LEVEL = 8
    const val DEFAULT_NETWORK = "tcp"
    const val TLS = "tls"
    const val REALITY = "reality"

    const val DEFAULT_FINGERPRINT = "chrome"

    const val DEFAULT_SS_METHOD = "aes-256-gcm"

    const val HEADER_TYPE_HTTP = "http"

    /** Protocols Scheme **/
    const val VMESS = "vmess://"
    const val CUSTOM = ""
    const val SHADOWSOCKS = "ss://"
    const val SOCKS = "socks://"
    const val SOCKS4 = "socks4://"
    const val SOCKS5 = "socks5://"
    const val HTTP = "http://"
    const val VLESS = "vless://"
    const val TROJAN = "trojan://"
    const val WIREGUARD = "wireguard://"
    const val TUIC = "tuic://"
    const val HYSTERIA = "hysteria://"
    const val HYSTERIA2 = "hysteria2://"
    const val HY2 = "hy2://"

    const val WIREGUARD_LOCAL_ADDRESS_V4 = "172.16.0.2/32"
    const val WIREGUARD_LOCAL_ADDRESS_V6 = "2606:4700:110:8f81:d551:a0:532e:a2b3/128"
    const val WIREGUARD_LOCAL_MTU = "1420"

}