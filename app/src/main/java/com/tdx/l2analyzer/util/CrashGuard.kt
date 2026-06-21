package com.tdx.l2analyzer.util

import android.app.Application
import android.os.Build
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

object CrashGuard {
    fun init(app: Application) {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, ex ->
            logCrash(app, ex)
            defaultHandler?.uncaughtException(thread, ex)
        }
    }

    private fun logCrash(app: Application, ex: Throwable) {
        try {
            val dir = File(app.getExternalFilesDir(null), "crash")
            if (!dir.exists()) dir.mkdirs()
            val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val file = File(dir, "crash_$ts.txt")
            FileWriter(file).use { w ->
                w.write("Time: ${Date()}\n")
                w.write("Model: ${Build.MODEL}\n")
                w.write("OS: ${Build.VERSION.RELEASE}\n")
                w.write("Exception: ${ex.javaClass.name}\n")
                w.write("Message: ${ex.message}\n")
                ex.stackTrace.forEach { w.write("  at $it\n") }
            }
        } catch (_: Exception) {}
    }
}
