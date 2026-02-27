package com.example.wirechef.core.di

import android.util.Log
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WireChefWebSocketListener @Inject constructor(
    private val client: OkHttpClient
) {
    private var webSocket: WebSocket? = null

    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 10)
    val messages = _messages.asSharedFlow()

    fun connect(role: String, userId: Int) {
        disconnect()

        val url = "ws://50.16.170.148/ws?role=$role&user_id=$userId"
        val request = Request.Builder().url(url).build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("WebSocket", "Conectado como $role")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WebSocket", "Mensaje recibido: $text")
                _messages.tryEmit(text)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("WebSocket", "Error: ${t.message}")
            }
        })
    }

    fun disconnect() {
        webSocket?.close(1000, "Cerrando sesión")
        webSocket = null
    }
}