package com.kian.mahmoudi.vegang.ui.screen

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kian.mahmoudi.vegang.R
import com.kian.mahmoudi.vegang.dto.ProfileItem
import com.kian.mahmoudi.vegang.enums.TrafficInfo
import com.kian.mahmoudi.vegang.enums.VpnState
import com.kian.mahmoudi.vegang.ui.component.ConfigCard
import com.kian.mahmoudi.vegang.ui.viewmodel.HomeUiState

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    vpnState: VpnState,
    traffic: TrafficInfo,
    onConnect: (ProfileItem) -> Unit,
    onDisconnect: () -> Unit,
    onGetConfigs: () -> Unit,
    onTestLatency: (ProfileItem) -> Unit,
    onTestLatencyAll: () -> Unit,
    onSortConfigs: () -> Unit,
    onCopyConfig: (ProfileItem) -> Unit,
    onDeleteConfig: (ProfileItem) -> Unit,
    onShareConfig: (ProfileItem) -> Unit,
    onDeleteNonWorking: () -> Unit
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ConnectionStatusBar(
                vpnState,
                traffic.upload,
                traffic.download,
                uiState.configs.size,
                onDisconnect = onDisconnect
            )
            Box(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.fillMaxSize()) {

                    if (uiState.configLoading) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            LinearProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.loading_configs),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    if (uiState.loading && !uiState.configLoading) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                    }

                    if (!uiState.loading && uiState.configs.isEmpty()) {
                        EmptyState(onGetConfigs, Modifier.weight(1f))
                    } else {
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(uiState.configs) { config ->
                                Log.d("MainActivity", "Json Config: $config")
                                ConfigCard(
                                    config,
                                    onConnect = { onConnect(it) },
                                    latencyMs = if (config.latency == 0L) null else config.latency,
                                    onTest = {
                                        onTestLatency(config)
                                    },
                                    onCopy = { onCopyConfig(config) },
                                    onDelete = { onDeleteConfig(config) },
                                    onShare = { onShareConfig(config) }
                                )
                            }
                        }
                        ActionBar(
                            onGetConfigs = onGetConfigs,
                            onTestAll = onTestLatencyAll,
                            onSort = onSortConfigs,
                            onDeleteFailed = onDeleteNonWorking,
                            loading = uiState.loading,
                            modifier = Modifier
                        )
                    }

                }
                if (uiState.loading && uiState.configs.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun ConnectionStatusBar(
    vpnState: VpnState,
    trafficUp: String,
    trafficDown: String,
    serverCount: Int,
    onDisconnect: () -> Unit
) {

    val color = vpnStateAccent(vpnState)
    val text = stringResource(vpnState.text)

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.25f)),
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(color)
                )

                Spacer(Modifier.width(10.dp))

                Icon(
                    imageVector = Icons.Rounded.Shield,
                    tint = color.copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp),
                    contentDescription = stringResource(R.string.shield_icon)
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = color,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDisconnect, enabled = vpnState != VpnState.DISCONNECTED) {
                    Icon(
                        imageVector = Icons.Rounded.Cancel,
                        contentDescription = stringResource(R.string.disconnect),
                        tint = if (vpnState != VpnState.DISCONNECTED) {
                            MaterialTheme.colorScheme.error
                        } else {
                            Color.Transparent
                        }
                    )
                }

            }

            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = color.copy(alpha = 0.1f))
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(14.dp),
                    imageVector = Icons.Rounded.ArrowDownward,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    contentDescription = stringResource(R.string.server_download_traffic)
                )
                Spacer(Modifier.width(8.dp))
                Text(text = trafficDown, style = MaterialTheme.typography.labelSmall)

                Spacer(Modifier.weight(1f))

                Icon(
                    modifier = Modifier.size(14.dp),
                    imageVector = Icons.Rounded.ArrowUpward,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    contentDescription = stringResource(R.string.server_upload_traffic)
                )
                Spacer(Modifier.width(8.dp))
                Text(text = trafficUp, style = MaterialTheme.typography.labelSmall)

                Spacer(Modifier.weight(1f))

                Text(
                    text = stringResource(R.string.servers, serverCount),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

    }
}

@Composable
fun EmptyState(
    onGetConfigs: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.CloudDownload,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.empty_state_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.empty_state_message),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = onGetConfigs) {
            Text(stringResource(R.string.empty_state_button))
        }
    }
}

@Composable
fun ActionBar(
    onGetConfigs: () -> Unit,
    onTestAll: () -> Unit,
    onSort: () -> Unit,
    onDeleteFailed: () -> Unit,
    loading: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ActionChip(
            icon = Icons.Rounded.CloudDownload,
            label = stringResource(R.string.get),
            onClick = onGetConfigs,
            enabled = !loading,
            modifier = Modifier.weight(1f)
        )
        ActionChip(
            icon = Icons.Rounded.Speed,
            label = stringResource(R.string.test),
            onClick = onTestAll,
            enabled = !loading,
            modifier = Modifier.weight(1f)
        )
        ActionChip(
            icon = Icons.Rounded.Sort,
            label = stringResource(R.string.sort),
            onClick = onSort,
            enabled = !loading,
            modifier = Modifier.weight(1f)
        )
        ActionChip(
            icon = Icons.Rounded.DeleteSweep,
            label = stringResource(R.string.clean),
            onClick = onDeleteFailed,
            enabled = !loading,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ActionChip(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        onClick = onClick,
        enabled = enabled
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1
            )
        }
    }
}

@Composable
fun vpnStateAccent(state: VpnState): Color {
    val scheme = MaterialTheme.colorScheme
    return when (state) {
        VpnState.DISCONNECTED -> scheme.outline
        VpnState.CONNECTING -> scheme.tertiary
        VpnState.CONNECTED -> scheme.primary
    }
}