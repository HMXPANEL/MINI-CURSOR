package com.hermes.android.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hermes.android.data.keystore.ConfigStore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Settings") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Provider
            OutlinedTextField(
                value = state.provider,
                onValueChange = viewModel::setProvider,
                label = { Text("Provider") },
                modifier = Modifier.fillMaxWidth()
            )

            // Model
            OutlinedTextField(
                value = state.model,
                onValueChange = viewModel::setModel,
                label = { Text("Model") },
                modifier = Modifier.fillMaxWidth()
            )

            // API Key
            var showKey by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = state.apiKey,
                onValueChange = viewModel::setApiKey,
                label = { Text("API Key") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (showKey) androidx.compose.ui.text.input.VisualTransformation.None
                else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(onClick = { showKey = !showKey }) {
                        Text(if (showKey) "Hide" else "Show")
                    }
                }
            )

            // Auto-approve
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Auto-approve execute_code")
                Switch(
                    checked = state.autoApprove,
                    onCheckedChange = viewModel::setAutoApprove
                )
            }

            // GitHub PAT
            OutlinedTextField(
                value = state.githubPat,
                onValueChange = viewModel::setGithubPat,
                label = { Text("GitHub PAT (optional)") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
            )

            Button(
                onClick = viewModel::saveSettings,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
    }
}
