package com.kian.mahmoudi.vegang.enums

import androidx.annotation.StringRes
import com.kian.mahmoudi.vegang.R

sealed class VpnState(@StringRes val text: Int) {
    object DISCONNECTED : VpnState(text = R.string.disconnected)
    object CONNECTING : VpnState(text = R.string.connecting)
    object CONNECTED : VpnState(text = R.string.connected)
}
