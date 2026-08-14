package com.kian.mahmoudi.vegang.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.WifiTethering
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kian.mahmoudi.vegang.R
import com.kian.mahmoudi.vegang.dto.ProfileItem

@Composable
fun ConfigCard(
    config: ProfileItem,
    latencyMs: Long? = null,
    onConnect: (ProfileItem) -> Unit,
    onTest: () -> Unit = {},
    onCopy: () -> Unit = {},
    onDelete: () -> Unit = {},
    onShare: () -> Unit = {},
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clickable { onConnect(config) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // ── Row 1: Name + Latency + Protocol ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = config.remarks.ifBlank { "Unnamed" },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                LatencyTag(latencyMs)
                Spacer(Modifier.width(6.dp))
                Tag(config.configType.toString(), MaterialTheme.colorScheme.primary)
            }

            Spacer(Modifier.height(6.dp))

            // ── Row 2: Server info ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                InfoItem(
                    Modifier.weight(1f),
                    stringResource(R.string.server),
                    serverDisplay(config)
                )
                InfoItem(
                    Modifier.weight(1f),
                    stringResource(R.string.port),
                    config.serverPort ?: "-"
                )
                InfoItem(
                    Modifier.weight(1f),
                    stringResource(R.string.sni),
                    config.sni ?: "example.com"
                )
            }

            Spacer(Modifier.height(8.dp))

            // ── Row 3: Security + Actions ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Tag(
                        (config.security ?: "none").uppercase(),
                        MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(6.dp))
                    MiniChip(config.flow ?: "none")
                }
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    IconBtn(Icons.Rounded.WifiTethering, stringResource(R.string.test), onTest)
                    IconBtn(Icons.Rounded.ContentCopy, stringResource(R.string.copy), onCopy)
                    IconBtn(Icons.Rounded.Delete, stringResource(R.string.delete), onDelete, true)
                    IconBtn(Icons.Rounded.Share, stringResource(R.string.share), onShare)
                }
            }
        }
    }
}

// ── Sub-components ──

@Composable
private fun LatencyTag(ms: Long?) {
    val m = ms ?: return
    val c = when {
        m <= 0 -> Color(0xFF9E9E9E)
        m < 500 -> Color(0xFF4CAF50)
        m < 1000 -> Color(0xFFFF9800)
        else -> Color(0xFFF44336)
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(5.dp)
                .clip(CircleShape)
                .background(c)
        )
        Spacer(Modifier.width(3.dp))
        Text(
            "${m}ms",
            style = MaterialTheme.typography.labelSmall,
            color = c,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun Tag(label: String, color: Color) {
    Box(
        Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun MiniChip(label: String) {
    Text(
        label, style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        modifier = Modifier
            .clip(RoundedCornerShape(3.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .padding(horizontal = 4.dp, vertical = 1.dp),
    )
}

@Composable
private fun InfoItem(modifier: Modifier, label: String, value: String) {
    Column(modifier = modifier.padding(end = 4.dp)) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
        Text(
            value,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun IconBtn(
    icon: ImageVector,
    desc: String,
    onClick: () -> Unit,
    destructive: Boolean = false
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(30.dp),
        colors = IconButtonDefaults.iconButtonColors(contentColor = if (destructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant)
    ) {
        Icon(icon, desc, modifier = Modifier.size(16.dp))
    }
}

private fun serverDisplay(config: ProfileItem): String {
    val s = config.server ?: return "No server"
    val p = config.serverPort ?: ""
    return if (p.isNotBlank()) "$s:$p" else s
}

