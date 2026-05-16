package com.hermes.android.ui.setup

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hermes.android.data.termux.TermuxLauncher

@Composable
fun SetupScreen(
    onSetupComplete: () -> Unit,
    viewModel: SetupViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Hermes Setup",
            style = MaterialTheme.typography.headlineLarge
        )

        // Step 1: Termux
        SetupStep(
            number = 1,
            title = "Install Termux",
            isComplete = state.isTermuxInstalled
        ) {
            if (!state.isTermuxInstalled) {
                Text("Get Termux from F-Droid (Play Store version is outdated).")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://f-droid.org/packages/com.termux/")))
                }) {
                    Text("Open F-Droid")
                }
            } else {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        }

        // Step 2: Permissions
        SetupStep(
            number = 2,
            title = "Allow External Apps",
            isComplete = state.isPermissionGranted
        ) {
            Text("Run this in Termux:")
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "echo 'allow-external-apps = true' >> ~/.termux/termux.properties",
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                val intent = context.packageManager.getLaunchIntentForPackage("com.termux")
                intent?.let { context.startActivity(it) }
            }) {
                Text("Open Termux")
            }
        }

        // Step 3: Install Hermes
        SetupStep(
            number = 3,
            title = "Install Hermes",
            isComplete = state.isHermesInstalled
        ) {
            Text("Run this in Termux:")
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "curl -fsSL https://raw.githubusercontent.com/.../install.sh | bash",
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { viewModel.checkHermesInstalled() }) {
                Text("I've done this")
            }
        }

        // Step 4: API Key
        SetupStep(
            number = 4,
            title = "API Key",
            isComplete = state.apiKey.isNotBlank()
        ) {
            var provider by remember { mutableStateOf(state.provider) }
            var key by remember { mutableStateOf("") }

            OutlinedTextField(
                value = provider,
                onValueChange = { provider = it },
                label = { Text("Provider (openai/anthropic/openrouter)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = key,
                onValueChange = { key = it },
                label = { Text("API Key") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { viewModel.saveApiKey(provider, key) },
                enabled = key.isNotBlank()
            ) {
                Text("Save & Test")
            }
        }

        // Step 5: Connect
        if (state.apiKey.isNotBlank()) {
            Button(
                onClick = {
                    viewModel.startConnection()
                    onSetupComplete()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Connect to Hermes")
            }
        }
    }
}

@Composable
private fun SetupStep(
    number: Int,
    title: String,
    isComplete: Boolean,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isComplete)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$number.",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                if (isComplete) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.CheckCircle, contentDescription = "Complete", tint = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}
