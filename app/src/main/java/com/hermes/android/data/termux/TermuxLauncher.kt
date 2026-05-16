package com.hermes.android.data.termux

import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TermuxLauncher @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun launch() {
        val intent = Intent("com.termux.RUN_COMMAND").apply {
            setClassName("com.termux", "com.termux.app.RunCommandService")
            putExtra("com.termux.RUN_COMMAND_PATH", "/data/data/com.termux/files/usr/bin/hermes")
            putExtra("com.termux.RUN_COMMAND_ARGUMENTS", arrayOf("--android-gateway", "--port", "7823"))
            putExtra("com.termux.RUN_COMMAND_BACKGROUND", true)
        }
        context.startService(intent)
    }

    fun launchLogTail(): Intent {
        return Intent("com.termux.RUN_COMMAND").apply {
            setClassName("com.termux", "com.termux.app.RunCommandService")
            putExtra("com.termux.RUN_COMMAND_PATH", "/data/data/com.termux/files/usr/bin/tail")
            putExtra("com.termux.RUN_COMMAND_ARGUMENTS", arrayOf("-f", "~/.hermes/logs/agent.log"))
            putExtra("com.termux.RUN_COMMAND_BACKGROUND", true)
        }
    }

    fun isTermuxInstalled(): Boolean {
        return try {
            context.packageManager.getPackageInfo("com.termux", 0)
            true
        } catch (e: Exception) {
            false
        }
    }
}
