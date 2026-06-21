package com.tdx.l2analyzer.analyzer

import com.tdx.l2analyzer.data.TdxSocketClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import java.util.PriorityQueue

object LargeOrderMonitor {

    data class LargeOrderAlert(
        val code: String,
        val price: Float,
        val volume: Int,
        val amount: Float,
        val side: TdxSocketClient.TickData.Side,
        val level: AlertLevel,
        val time: Long = System.currentTimeMillis()
    ) {
        enum class AlertLevel { NORMAL, LARGE, SUPER, EXTREME }
    }

    private const val LARGE_THRESHOLD = 100000f
    private const val SUPER_THRESHOLD = 500000f
    private const val EXTREME_THRESHOLD = 2000000f
    private const val MAX_QUEUE_SIZE = 20

    private val orderQueue = PriorityQueue<LargeOrderAlert>(
        compareByDescending { it.amount }
    )

    fun monitor(tickFlow: Flow<TdxSocketClient.TickData>): Flow<LargeOrderAlert> {
        return tickFlow
            .filter { it.amount >= LARGE_THRESHOLD }
            .map { tick ->
                val level = when {
                    tick.amount >= EXTREME_THRESHOLD -> LargeOrderAlert.AlertLevel.EXTREME
                    tick.amount >= SUPER_THRESHOLD -> LargeOrderAlert.AlertLevel.SUPER
                    else -> LargeOrderAlert.AlertLevel.LARGE
                }
                val alert = LargeOrderAlert(
                    tick.code, tick.price, tick.volume, tick.amount,
                    tick.side, level, tick.time
                )
                synchronized(orderQueue) {
                    orderQueue.offer(alert)
                    if (orderQueue.size > MAX_QUEUE_SIZE) orderQueue.poll()
                }
                alert
            }
    }

    fun getRecentAlerts(): List<LargeOrderAlert> =
        synchronized(orderQueue) { orderQueue.toList().sortedByDescending { it.time } }
}
