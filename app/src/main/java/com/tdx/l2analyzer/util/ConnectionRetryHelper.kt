package com.tdx.l2analyzer.util

import kotlinx.coroutines.*

object ConnectionRetryHelper {
    private const val MAX_RETRIES = 5
    private const val BASE_DELAY_MS = 2000L

    fun retry(action: suspend () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            var attempt = 0
            while (attempt < MAX_RETRIES) {
                delay(BASE_DELAY_MS * (1L shl attempt))
                attempt++
                try {
                    action()
                    break
                } catch (_: Exception) {
                    if (attempt >= MAX_RETRIES) break
                }
            }
        }
    }
}
