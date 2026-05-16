package com.hermes.android.data.websocket

import com.hermes.android.data.keystore.ConfigStore
import com.hermes.android.data.termux.TermuxLauncher
import com.hermes.android.domain.protocol.HermesInbound
import com.hermes.android.domain.protocol.HermesOutbound
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.net.Socket
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HermesConnectionManager @Inject constructor(
    private val termuxLauncher: TermuxLauncher,
    private val wsClient: HermesWebSocketClient,
    private val configStore: ConfigStore
) {

    sealed class State {
        object Idle : State()
        object Launching : State()
        object Connecting : State()
        object Ready : State()
        data class Degraded(val attempt: Int) : State()
        data class Dead(val reason: String) : State()
    }

    private val _state = MutableStateFlow<State>(State.Idle)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _events = MutableSharedFlow<HermesInbound>(extraBufferCapacity = 64)
    val events: SharedFlow<HermesInbound> = _events.asSharedFlow()

    private var watchdogJob: Job? = null
    private var reconnectAttempts = 0
    private var currentScope: CoroutineScope? = null

    fun start(scope: CoroutineScope) {
        currentScope = scope
        if (_state.value !is State.Ready) {
            scope.launch { launchAndConnect() }
        }
    }

    private suspend fun launchAndConnect() {
        _state.value = State.Launching
        if (!isPortOpen(7823)) {
            termuxLauncher.launch()
            val ready = withTimeoutOrNull(15_000) {
                while (!isPortOpen(7823)) {
                    delay(500)
                }
                true
            }
            if (ready != true) {
                _state.value = State.Dead("Hermes failed to start within 15s")
                return
            }
        }

        _state.value = State.Connecting
        try {
            wsClient.connect("ws://127.0.0.1:7823") {
                currentScope?.launch { handleDisconnect() }
            }

            // Collect WS messages into our shared flow
            currentScope?.launch {
                wsClient.messages.collect { msg ->
                    _events.tryEmit(msg)
                }
            }

            // Send configure
            wsClient.send(
                HermesOutbound.Configure(
                    provider = configStore.provider,
                    model = configStore.model,
                    apiKey = configStore.getApiKey(),
                    autoApprove = configStore.autoApprove
                )
            )

            _state.value = State.Ready
            reconnectAttempts = 0
            startWatchdog()
        } catch (e: Exception) {
            handleDisconnect()
        }
    }

    private suspend fun handleDisconnect() {
        watchdogJob?.cancel()
        if (reconnectAttempts >= 5) {
            _state.value = State.Dead("Lost after 5 reconnect attempts")
            return
        }
        reconnectAttempts++
        _state.value = State.Degraded(reconnectAttempts)
        delay((1L shl reconnectAttempts) * 1000) // 2s, 4s, 8s, 16s, 32s
        launchAndConnect()
    }

    private fun startWatchdog() {
        watchdogJob = currentScope?.launch {
            while (isActive) {
                delay(8_000)
                val pong = withTimeoutOrNull(3_000) {
                    wsClient.send(HermesOutbound.Ping())
                    _events.first { it is HermesInbound.Pong }
                }
                if (pong == null) {
                    handleDisconnect()
                    break
                }
            }
        }
    }

    fun restart() {
        reconnectAttempts = 0
        currentScope?.launch {
            wsClient.disconnect()
            launchAndConnect()
        }
    }

    fun send(msg: HermesOutbound): Boolean {
        return if (_state.value is State.Ready) {
            wsClient.send(msg)
        } else {
            false
        }
    }

    fun disconnect() {
        watchdogJob?.cancel()
        wsClient.disconnect()
        _state.value = State.Idle
    }

    private fun isPortOpen(port: Int): Boolean {
        return try {
            Socket("127.0.0.1", port).use { it.isConnected }
        } catch (e: Exception) {
            false
        }
    }
}
