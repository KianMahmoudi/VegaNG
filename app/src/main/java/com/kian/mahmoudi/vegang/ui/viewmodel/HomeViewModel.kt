package com.kian.mahmoudi.vegang.ui.viewmodel

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kian.mahmoudi.vegang.R
import com.kian.mahmoudi.vegang.data.config.ConfigParser
import com.kian.mahmoudi.vegang.data.repository.ConfigRepository
import com.kian.mahmoudi.vegang.data.repository.VpnRepository
import com.kian.mahmoudi.vegang.dto.ProfileItem
import com.kian.mahmoudi.vegang.enums.ConfigSort
import com.kian.mahmoudi.vegang.enums.TrafficInfo
import com.kian.mahmoudi.vegang.enums.VpnState
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

data class HomeUiState(
    val configs: List<ProfileItem> = emptyList(),
    val errorMessage: String? = null,
    val loading: Boolean = false
)

sealed interface HomeUiEvent {
    data class LaunchVpnPermission(val config: String,val configName: String) : HomeUiEvent
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val configRepository: ConfigRepository,
    private val vpnRepository: VpnRepository,
) :
    ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<HomeUiEvent>()
    val event = _event.asSharedFlow()

    val vpnState: StateFlow<VpnState> = vpnRepository.state

    val traffic: StateFlow<TrafficInfo> = vpnRepository.traffic

    private var currentSort = ConfigSort.DEFAULT

    val mutex = Mutex()

    init {
        observeConfigs()
    }

    fun observeConfigs() {
        viewModelScope.launch {
            configRepository.observeConfigs(currentSort)
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        errorMessage = e.message ?: context.getString(R.string.unknown_error)
                    )

                }
                .collect { configs ->
                    _uiState.value = _uiState.value.copy(configs = configs)
                }

        }
    }

    fun getConfigs(count: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true)
            try {
                configRepository.getConfigs(count)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    errorMessage = e.message ?: context.getString(R.string.unknown_error)
                )
            } finally {
                _uiState.value = _uiState.value.copy(loading = false)
            }
        }
    }

    fun connect(config: ProfileItem) {
        val json = com.kian.mahmoudi.vegang.data.config.ConfigParser.toV2rayJson(config)
        if (vpnRepository.isPrepared()) {
            vpnRepository.connect(json, config.remarks)
        } else {
            viewModelScope.launch {
                _event.emit(HomeUiEvent.LaunchVpnPermission(json,config.remarks))
            }
        }
    }

    fun sortConfigs(sort: ConfigSort) {
        currentSort = sort
        observeConfigs()
    }

    fun copyToClipboard(config: ProfileItem) {
        viewModelScope.launch {
            try {
                val json = ConfigParser.toV2rayJson(config)
                val clipboard =
                    getApplication(context).getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("V2Ray Config", json)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(
                    context,
                    context.getString(R.string.config_copied), Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Log.e("CLIPBOARD", "Copy failed", e)
                Toast.makeText(context, context.getString(R.string.copy_failed), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    fun deleteConfig(config: ProfileItem) {
        viewModelScope.launch {
            try {
                configRepository.deleteConfig(config)
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    context.getString(R.string.delete_failed), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun shareConfig(config: ProfileItem) {
        viewModelScope.launch {
            try {
                val json = ConfigParser.toV2rayJson(config)
                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    putExtra(Intent.EXTRA_TEXT, json)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(
                    sendIntent,
                    context.getString(R.string.share_config)
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(shareIntent)
            } catch (e: Exception) {
                Log.e("HomeViewModel", e.message ?: "")
                Toast.makeText(
                    context,
                    context.getString(R.string.share_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun testAllConfigs() {
        viewModelScope.launch {
            if (mutex.isLocked) return@launch
            mutex.withLock {
                _uiState.value = _uiState.value.copy(loading = true)
                try {
                    configRepository.testAllConfigs()
                } finally {
                    _uiState.value = _uiState.value.copy(loading = false)
                }
            }
        }
    }

    fun testConfig(config: ProfileItem) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true)
            try {
                configRepository.testConfig(config)
            } finally {
                _uiState.value = _uiState.value.copy(loading = false)
            }
        }
    }

    fun deleteNonWorkingConfigs() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true)
            configRepository.deleteNonWorkingConfigs()
            _uiState.value = _uiState.value.copy(loading = false)
        }
    }

    fun disconnect() {
        vpnRepository.stop()
    }

}