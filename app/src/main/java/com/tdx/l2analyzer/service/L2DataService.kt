package com.tdx.l2analyzer.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.tdx.l2analyzer.R
import com.tdx.l2analyzer.analyzer.BuySellAnalyzer
import com.tdx.l2analyzer.analyzer.LargeOrderMonitor
import com.tdx.l2analyzer.analyzer.RhythmAnalyzer
import com.tdx.l2analyzer.data.TdxSocketClient
import kotlinx.coroutines.*

class L2DataService : Service() {

    companion object {
        private const val TAG = "L2DataService"
        private const val CHANNEL_ID = "L2DataChannel"
        private const val NOTIF_ID = 10001
    }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var socketClient: TdxSocketClient

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIF_ID, buildNotification("L2数据服务启动中..."))
        Log.i(TAG, "Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val code = intent?.getStringExtra("stock_code") ?: "000001"
        val host = intent?.getStringExtra("host") ?: "hq.tdx.com.cn"
        val port = intent?.getIntExtra("port", 7709) ?: 7709
        val token = intent?.getStringExtra("token") ?: ""

        socketClient = TdxSocketClient(host, port, token)

        serviceScope.launch {
            launch {
                socketClient.connectionState.collect { state ->
                    val msg = when (state) {
                        is TdxSocketClient.ConnectionState.Connected -> "L2已连接"
                        is TdxSocketClient.ConnectionState.Connecting -> "L2连接中..."
                        is TdxSocketClient.ConnectionState.Disconnected -> "L2已断开"
                        is TdxSocketClient.ConnectionState.Error -> "L2错误: ${state.message}"
                    }
                    updateNotification(msg)
                }
            }

            launch {
                BuySellAnalyzer.analyze(socketClient.tickFlow).collect { result ->
                    Log.d(TAG, "BuySell: pressure=${result.pressureIndex}, ratio=${result.ratio}")
                }
            }

            launch {
                LargeOrderMonitor.monitor(socketClient.tickFlow).collect { alert ->
                    Log.i(TAG, "LargeOrder: ${alert.code} ${alert.side} ${alert.amount}")
                }
            }

            launch {
                RhythmAnalyzer.analyze(socketClient.tickFlow).collect { result ->
                    Log.d(TAG, "Rhythm: score=${result.rhythmScore}, rate=${result.tickRate}")
                }
            }

            socketClient.connect()
            delay(1000)
            socketClient.subscribe(code)
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        if (::socketClient.isInitialized) {
            socketClient.disconnect()
        }
        Log.i(TAG, "Service destroyed")
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID, "L2数据服务", NotificationManager.IMPORTANCE_LOW
        ).apply { description = "L2实时数据分析服务" }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun buildNotification(text: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("L2 数据分析")
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_stat_l2)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(text: String) {
        val nm = getSystemService(NotificationManager::class.java)
        nm.notify(NOTIF_ID, buildNotification(text))
    }
}
