package com.hermes.android

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hermes.android.service.HermesService
import com.hermes.android.ui.chat.ChatScreen
import com.hermes.android.ui.sessions.SessionListScreen
import com.hermes.android.ui.setup.SetupScreen
import com.hermes.android.ui.theme.HermesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startHermesService()

        setContent {
            HermesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "setup"
                    ) {
                        composable("setup") {
                            SetupScreen(
                                onSetupComplete = {
                                    navController.navigate("sessions") {
                                        popUpTo("setup") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("sessions") {
                            SessionListScreen(
                                onSessionClick = { sessionId ->
                                    navController.navigate("chat/$sessionId")
                                },
                                onNewSession = {
                                    navController.navigate("chat/new")
                                }
                            )
                        }
                        composable("chat/{sessionId}") { backStackEntry ->
                            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: "new"
                            ChatScreen(
                                sessionId = sessionId,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun startHermesService() {
        val intent = Intent(this, HermesService::class.java)
        startForegroundService(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Keep service running — only disconnect when app is truly killed
    }
}
