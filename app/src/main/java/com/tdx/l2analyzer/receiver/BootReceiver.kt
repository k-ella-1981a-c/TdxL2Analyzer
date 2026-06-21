package com.tdx.l2analyzer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.tdx.l2analyzer.service.L2DataService

class BootReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "BootReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.i(TAG, "Boot completed")
            // 可选：开机自启
            // val serviceIntent = Intent(context, L2DataService::class.java)
            // context.startForegroundService(serviceIntent)
        }
    }
}
