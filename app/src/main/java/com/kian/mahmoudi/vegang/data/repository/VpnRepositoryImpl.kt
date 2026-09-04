package com.kian.mahmoudi.vegang.data.repository

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import com.kian.mahmoudi.vegang.enums.TrafficInfo
import com.kian.mahmoudi.vegang.enums.VpnState
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.dev7.lib.v2ray.V2rayController
import dev.dev7.lib.v2ray.utils.V2rayConstants
import dev.dev7.lib.v2ray.utils.V2rayConstants.SERVICE_CONNECTION_STATE_BROADCAST_EXTRA
import dev.dev7.lib.v2ray.utils.V2rayConstants.SERVICE_DOWNLOAD_TRAFFIC_BROADCAST_EXTRA
import dev.dev7.lib.v2ray.utils.V2rayConstants.SERVICE_DURATION_BROADCAST_EXTRA
import dev.dev7.lib.v2ray.utils.V2rayConstants.SERVICE_UPLOAD_TRAFFIC_BROADCAST_EXTRA
import dev.dev7.lib.v2ray.utils.V2rayConstants.V2RAY_SERVICE_STATICS_BROADCAST_INTENT
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class VpnRepositoryImpl @Inject constructor(@ApplicationContext private val context: Context) :
    VpnRepository {

    private val _state = MutableStateFlow<VpnState>(VpnState.DISCONNECTED)
    override val state: StateFlow<VpnState> = _state.asStateFlow()

    private val _traffic = MutableStateFlow<TrafficInfo>(TrafficInfo())
    override val traffic: StateFlow<TrafficInfo> = _traffic.asStateFlow()

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (p1 != null) {

                val trafficDownload =
                    p1.getStringExtra(SERVICE_DOWNLOAD_TRAFFIC_BROADCAST_EXTRA) ?: "0 B"
                val trafficUpload =
                    p1.getStringExtra(SERVICE_UPLOAD_TRAFFIC_BROADCAST_EXTRA) ?: "0 B"
                val duration = p1.getStringExtra(SERVICE_DURATION_BROADCAST_EXTRA) ?: "00:00:00"

                _traffic.value = TrafficInfo(upload = trafficUpload, download = trafficDownload, duration = duration)


                val connectionState = p1.getSerializableExtra(
                    SERVICE_CONNECTION_STATE_BROADCAST_EXTRA
                ) as? V2rayConstants.CONNECTION_STATES ?: return

                when (connectionState) {
                    V2rayConstants.CONNECTION_STATES.CONNECTED -> {
                        _state.value = VpnState.CONNECTED
                    }

                    V2rayConstants.CONNECTION_STATES.CONNECTING -> {
                        _state.value = VpnState.CONNECTING
                    }

                    V2rayConstants.CONNECTION_STATES.DISCONNECTED -> {
                        _state.value = VpnState.DISCONNECTED
                    }
                }
            }

        }

    }

    init {
        ContextCompat.registerReceiver(
            context,
            broadcastReceiver,
            IntentFilter(V2RAY_SERVICE_STATICS_BROADCAST_INTENT),
            ContextCompat.RECEIVER_EXPORTED
        )
        V2rayController.queryServiceState(context)
    }


    override fun connect(config: String, configName: String) {
        _state.value = VpnState.CONNECTING
        // Start V2Ray with monitoring - use lifecycle scope
        V2rayController.StartV2ray(
            context,
            configName,
            config,
            null
        );
    }

    override fun stop() {
        // Force stop V2Ray and reset state immediately
        V2rayController.stopV2ray(context)
        _state.value = VpnState.DISCONNECTED
    }

    override fun isPrepared(): Boolean {
        return V2rayController.isPreparedForConnection(context)
    }


}