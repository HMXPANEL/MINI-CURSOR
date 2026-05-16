package com.hermes.android.ui.chat.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hermes.android.domain.model.ToolCard
import com.hermes.android.ui.theme.ErrorRed
import com.hermes.android.ui.theme.SuccessGreen
import com.hermes.android.ui.theme.WarningAmber

@Composable
fun ToolCardItem(
    card: ToolCard,
    onApprove: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = when (card) {
                is ToolCard.Success -> SuccessGreen.copy(alpha = 0.1f)
                is ToolCard.Error -> ErrorRed.copy(alpha = 0.1f)
                is ToolCard.PendingApproval -> WarningAmber.copy(alpha = 0.1f)
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = when (card) {
                        is ToolCard.Running -> Icons.Default.RadioButtonUnchecked
                        is ToolCard.Success -> Icons.Default.CheckCircle
                        is ToolCard.Error -> Icons.Default.Error
                        is ToolCard.PendingApproval -> Icons.Default.Help
                    },
                    contentDescription = null,
                    tint = when (card) {
                        is ToolCard.Success -> SuccessGreen
                        is ToolCard.Error -> ErrorRed
                        is ToolCard.PendingApproval -> WarningAmber
                        else -> MaterialTheme.colorScheme.primary
                    }
                )
                Text(
                    text = card.toolName,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (card is ToolCard.Running) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                }
            }

            Text(
                text = card.argsSummary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )

            when (card) {
                is ToolCard.PendingApproval -> {
                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onApprove?.invoke(true) },
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                        ) {
                            Text("Allow")
                        }
                        OutlinedButton(
                            onClick = { onApprove?.invoke(false) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed)
                        ) {
                            Text("Deny")
                        }
                    }
                }
                is ToolCard.Success -> {
                    Text(
                        text = card.resultSummary,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                is ToolCard.Error -> {
                    Text(
                        text = card.resultSummary,
                        style = MaterialTheme.typography.bodySmall,
                        color = ErrorRed,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                else -> {}
            }

            if (card !is ToolCard.Running) {
                TextButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(if (expanded) "Hide raw" else "Show raw")
                }
                if (expanded) {
                    Text(
                        text = card.argsSummary + "

" + when (card) {
                            is ToolCard.Success -> card.rawResult
                            is ToolCard.Error -> card.rawResult ?: "No result"
                            is ToolCard.PendingApproval -> card.rawArgs
                            else -> ""
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
