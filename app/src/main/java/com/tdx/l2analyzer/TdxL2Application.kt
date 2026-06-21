package com.tdx.l2analyzer

import android.app.Application
import android.util.Log
import com.tdx.l2analyzer.util.CrashGuard

class TdxL2Application : Application() {

    companion object {
        private const val TAG = "TdxL2Application"
    }

    override fun onCreate() {
        super.onCreate()
        // 全局异常捕获
        CrashGuard.init(this)
        Log.i(TAG, "TdxL2Analyzer Application started")
    }
}
