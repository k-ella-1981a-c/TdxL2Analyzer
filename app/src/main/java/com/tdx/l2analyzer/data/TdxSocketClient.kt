package com.tdx.l2analyzer.data

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.net.SocketFactory

class TdxSocketClient(
    private val host: String,
    private val port: Int,
    private val accountToken: String
) {
    companion object {
        private const val TAG = "TdxSocketClient"
        private const val CONNECT_TIMEOUT = 10000
        private const val HEARTBEAT_INTERVAL = 30000L
        private const val CMD_LOGIN = 0x0100
        private const val CMD_SUBSCRIBE = 0x0200
        private const val CMD_HEARTBEAT = 0xFF00
        private const val CMD_TICK_DATA = 0x1001
    }

    private var socket: Socket? = null
    private var inputStream: DataInputStream? = null
    private var outputStream: DataOutputStream? = null
    private var heartbeatJob: Job? = null

    private val _tickFlow = MutableSharedFlow<TickData>(replay = 50)
    val tickFlow: SharedFlow<TickData> = _tickFlow

    private val _connectionState = MutableSharedFlow<ConnectionState>(replay = 1)
    val connectionState: SharedFlow<ConnectionState> = _connectionState

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    sealed class ConnectionState {
        object Disconnected : ConnectionState()
        object Connecting : ConnectionState()
        object Connected : ConnectionState()
        data class Error(val message: String) : ConnectionState()
    }

    data class TickData(
        val time: Long,
        val price: Float,
        val volume: Int,
        val amount: Float,
        val side: Side,
        val orderKind: OrderKind,
        val code: String,
        val type: Char
    ) {
        enum class Side { BUY, SELL }
        enum class OrderKind { LIMIT, MARKET }
    }

    fun connect() {
        scope.launch {
            _connectionState.emit(ConnectionState.Connecting)
            try {
                socket = SocketFactory.getDefault().createSocket().apply {
                    connect(java.net.InetSocketAddress(host, port), CONNECT_TIMEOUT)
                    soTimeout = 30000
                }
                inputStream = DataInputStream(socket!!.inputStream)
                outputStream = DataOutputStream(socket!!.outputStream)
                sendLogin()
                _connectionState.emit(ConnectionState.Connected)
                Log.i(TAG, "Connected to $host:$port")
                startHeartbeat()
                receiveLoop()
            } catch (e: Exception) {
                Log.e(TAG, "Connection failed: ${e.message}")
                _connectionState.emit(ConnectionState.Error(e.message ?: "Unknown"))
            }
        }
    }

    fun subscribe(code: String) {
        scope.launch {
            try {
                sendSubscribe(code)
                Log.i(TAG, "Subscribed: $code")
            } catch (e: Exception) {
                Log.e(TAG, "Subscribe failed: ${e.message}")
            }
        }
    }

    fun disconnect() {
        scope.launch {
            heartbeatJob?.cancel()
            try { socket?.close() } catch (_: Exception) { }
            socket = null
            _connectionState.emit(ConnectionState.Disconnected)
            Log.i(TAG, "Disconnected")
        }
    }

    private suspend fun sendLogin() {
        val tokenBytes = accountToken.toByteArray(Charsets.UTF_8)
        val body = ByteBuffer.allocate(2 + tokenBytes.size)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putShort(tokenBytes.size.toShort())
            .put(tokenBytes)
            .array()
        sendPacket(CMD_LOGIN, body)
    }

    private suspend fun sendSubscribe(code: String) {
        val cb = code.toByteArray(Charsets.UTF_8)
        val body = ByteBuffer.allocate(1 + cb.size)
            .order(ByteOrder.LITTLE_ENDIAN)
            .put(cb.size.toByte())
            .put(cb)
            .array()
        sendPacket(CMD_SUBSCRIBE, body)
    }

    private suspend fun sendHeartbeat() {
        sendPacket(CMD_HEARTBEAT, byteArrayOf())
    }

    private suspend fun sendPacket(cmd: Int, body: ByteArray) {
        val header = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN)
            .putShort((16 + body.size).toShort())
            .putShort(0x10)
            .putInt(cmd)
            .putInt(0)
            .putInt(body.size)
            .array()
        outputStream?.write(header)
        outputStream?.write(body)
        outputStream?.flush()
    }

    private suspend fun receiveLoop() {
        try {
            while (socket?.isConnected == true) {
                val hb = ByteArray(16)
                inputStream?.readFully(hb)
                val h = ByteBuffer.wrap(hb).order(ByteOrder.LITTLE_ENDIAN)
                h.short; h.short
                val cmd = h.int
                h.int; val bodyLen = h.int
                val body = ByteArray(bodyLen)
                inputStream?.readFully(body)
                if (cmd == CMD_TICK_DATA) {
                    parseTick(body)?.let { _tickFlow.emit(it) }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Receive error: ${e.message}")
            _connectionState.emit(ConnectionState.Error(e.message ?: "Recv err"))
        }
    }

    private fun parseTick(body: ByteArray): TickData? {
        return try {
            val b = ByteBuffer.wrap(body).order(ByteOrder.LITTLE_ENDIAN)
            val time = b.long
            val price = b.float
            val vol = b.int
            val amt = b.float
            val sideB = b.get().toInt()
            val okB = b.get().toInt()
            val cLen = b.get().toInt()
            val cBytes = ByteArray(cLen); b.get(cBytes)
            val code = String(cBytes, Charsets.UTF_8)
            val type = b.get().toChar()
            TickData(
                time, price, vol, amt,
                if (sideB == 1) TickData.Side.BUY else TickData.Side.SELL,
                if (okB == 1) TickData.OrderKind.LIMIT else TickData.OrderKind.MARKET,
                code, type
            )
        } catch (e: Exception) {
            Log.w(TAG, "Parse tick failed: ${e.message}")
            null
        }
    }

    private fun startHeartbeat() {
        heartbeatJob = scope.launch {
            while (isActive) {
                delay(HEARTBEAT_INTERVAL)
                try { sendHeartbeat() } catch (_: Exception) { }
            }
        }
    }
}
