package com.vhennus.general.data

import android.os.Message
import com.google.gson.Gson
import com.vhennus.chat.data.ChatViewModel
import com.vhennus.chat.domain.Chat
import com.vhennus.general.utils.CLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Named

class WebSocketManager @Inject constructor(
    @Named("webSocketUrl") private val url: String,
    private val getUserToken: GetUserToken,
) {
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null


    fun connect(onMessageReceived: (String) -> Unit, onFailure: (Throwable) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch{
            try {
                val request = Request.Builder().url(url).addHeader("Authorization", getUserToken.getUserToken()).build()
                webSocket = client.newWebSocket(request, object : WebSocketListener() {
                    override fun onMessage(webSocket: WebSocket, text: String) {
                        CLog.debug("WE MESSAGE", text)

                        onMessageReceived(text)

                    }
                    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                        CLog.debug("WS","failed ${t.message}.")
                        onFailure(t)
                    }
                    override fun onOpen(webSocket: WebSocket, response: Response) {
                        CLog.debug("WS","ebSocket connection opened.")
                    }
                })
            }catch(e:Exception){
                onFailure(e)
            }
        }

    }

    fun sendMessage(message: String): Boolean {
        return try {
            webSocket?.send(message) ?: false
        } catch (e: Exception) {
            CLog.debug("WS", "Failed to send message: ${e.message}")
            false
        }
    }



    fun disconnect() {
        webSocket?.close(1000, "App closed")
    }
}