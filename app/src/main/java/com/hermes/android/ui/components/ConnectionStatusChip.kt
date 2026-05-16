package com.hermes.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hermes.android.data.websocket.HermesConnectionManager
import com.hermes.android.ui.theme.ErrorRed
import com.hermes.android.ui.theme.SuccessGreen
import com.hermes.android.ui.theme.WarningAmber

@Composable
fun ConnectionStatusChip(
    state: HermesConnectionManager.State,
    modifier: Modifier = Modifier
) {
    val (label, color) = when (state) {
        is HermesConnectionManager.State.Ready -> "Ready" to SuccessGreen
        is HermesConnectionManager.State.Connecting -> "Connecting..." to Color.Gray
        is HermesConnectionManager.State.Launching -> "Launching..." to Color.Gray
        is HermesConnectionManager.State.Degraded -> "Reconnect ${state.attempt}/5" to WarningAmber
        is HermesConnectionManager.State.Dead -> "Dead" to ErrorRed
        else -> "Idle" to Color.Gray
    }

    Box(
        modifier = modifier
            .background(color.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}
