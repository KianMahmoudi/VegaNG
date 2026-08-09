package com.kian.mahmoudi.vegang.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.kian.mahmoudi.vegang.R
import com.kian.mahmoudi.vegang.ui.theme.VpnTheme
import dagger.hilt.android.AndroidEntryPoint
import dev.dev7.lib.v2ray.V2rayController

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        V2rayController.init(this, R.drawable.ic_launcher_background, "V2ray Android");



        enableEdgeToEdge()
        setContent {
            VpnTheme {
                VpnApp()
            }
        }
    }
}