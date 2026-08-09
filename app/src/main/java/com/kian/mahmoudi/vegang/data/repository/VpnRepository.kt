package com.kian.mahmoudi.vegang.data.repository

import com.kian.mahmoudi.vegang.enums.TrafficInfo
import com.kian.mahmoudi.vegang.enums.VpnState
import kotlinx.coroutines.flow.StateFlow

interface VpnRepository {

    val state: StateFlow<VpnState>

    val traffic: StateFlow<TrafficInfo>
    fun connect(config: String, configName: String)
    fun stop()
    fun isPrepared(): Boolean
}