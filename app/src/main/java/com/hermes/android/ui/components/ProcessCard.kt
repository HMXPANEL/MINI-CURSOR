package com.hermes.android.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Composable
fun ProcessCard(
    rawResult: String,
    modifier: Modifier = Modifier
) {
    val processes = try {
        val json = Json { ignoreUnknownKeys = true }
        val obj = json.parseToJsonElement(rawResult).jsonObject
        obj["processes"]?.jsonArray?.map { proc ->
            Triple(
                proc.jsonObject["pid"]?.jsonPrimitive?.content ?: "?",
                proc.jsonObject["name"]?.jsonPrimitive?.content ?: "?",
                proc.jsonObject["cpu"]?.jsonPrimitive?.content ?: "?"
            )
        } ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Processes",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("PID", style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1f))
                Text("Name", style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(2f))
                Text("CPU%", style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1f))
            }
            processes.forEach { (pid, name, cpu) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(pid, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                    Text(name, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(2f))
                    Text(cpu, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
