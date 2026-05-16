package com.hermes.android.data.websocket

import com.hermes.android.domain.protocol.HermesInbound
import com.hermes.android.domain.protocol.HermesOutbound
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HermesWebSocketClient @Inject constructor() {

    private val client = OkHttpClient.Builder()
        .pingInterval(0, TimeUnit.SECONDS) // We handle pings manually
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.SECONDS)
        .build()

    private var webSocket: WebSocket? = null
    private val _messages = MutableSharedFlow<HermesInbound>(extraBufferCapacity = 64)
    val messages: SharedFlow<HermesInbound> = _messages.asSharedFlow()

    private val json = Json { ignoreUnknownKeys = true }
    private var onCloseCallback: (() -> Unit)? = null

    fun connect(url: String, onClose: () -> Unit): Boolean {
        onCloseCallback = onClose
        val request = Request.Builder().url(url).build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val msg = parseMessage(text)
                    _messages.tryEmit(msg)
                } catch (e: Exception) {
                    _messages.tryEmit(
                        HermesInbound.Error("parse_error", "Failed to parse: ${e.message}")
                    )
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(1000, null)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                onCloseCallback?.invoke()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                onCloseCallback?.invoke()
            }
        })
        return true
    }

    fun send(message: HermesOutbound): Boolean {
        val text = json.encodeToString(message)
        return webSocket?.send(text) ?: false
    }

    fun disconnect() {
        webSocket?.close(1000, "Client disconnect")
        webSocket = null
    }

    private fun parseMessage(text: String): HermesInbound {
        return json.decodeFromString(HermesInbound.serializer(), text)
    }
}
