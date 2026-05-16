package com.hermes.android.ui.chat.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hermes.android.ui.theme.ErrorRed
import com.hermes.android.ui.theme.SuccessGreen

@Composable
fun ApprovalCard(
    toolName: String,
    argsSummary: String,
    onApprove: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Approval required: $toolName",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = argsSummary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onApprove(true) },
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                ) {
                    Text("Allow")
                }
                OutlinedButton(
                    onClick = { onApprove(false) },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed)
                ) {
                    Text("Deny")
                }
            }
        }
    }
}
