package com.v2ray.ang.fmt

import com.kian.mahmoudi.vegang.dto.ProfileItem
import com.kian.mahmoudi.vegang.enums.EConfigType
import com.kian.mahmoudi.vegang.fmt.FmtBase
import com.kian.mahmoudi.vegang.dto.V2rayConfig
import com.v2ray.ang.util.JsonUtil

object CustomFmt : FmtBase() {
    /**
     * Parses a JSON string into a ProfileItem object.
     *
     * @param str the JSON string to parse
     * @return the parsed ProfileItem object, or null if parsing fails
     */
    fun parse(str: String): ProfileItem {
        val config = ProfileItem.create(EConfigType.CUSTOM)

        val fullConfig = JsonUtil.fromJson(str, V2rayConfig::class.java)
        val outbound = fullConfig?.getProxyOutbound()

        config.remarks = fullConfig?.remarks ?: System.currentTimeMillis().toString()
        config.server = outbound?.getServerAddress()
        config.serverPort = outbound?.getServerPort()?.toString()

        return config
    }
}