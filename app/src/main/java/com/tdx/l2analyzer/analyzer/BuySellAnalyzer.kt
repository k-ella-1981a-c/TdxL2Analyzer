package com.tdx.l2analyzer.analyzer

import com.tdx.l2analyzer.data.TdxSocketClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.ConcurrentHashMap

object BuySellAnalyzer {

    data class BuySellResult(
        val buyVolume: Long,
        val sellVolume: Long,
        val buyAmount: Float,
        val sellAmount: Float,
        val buyLargeVolume: Long,
        val sellLargeVolume: Long,
        val pressureIndex: Int,
        val ratio: Float,
        val timestamp: Long = System.currentTimeMillis()
    )

    private const val LARGE_ORDER_THRESHOLD = 100000f

    private val windows = ConcurrentHashMap<String, WindowData>()

    fun analyze(tickFlow: Flow<TdxSocketClient.TickData>): Flow<BuySellResult> {
        return tickFlow.map { tick ->
            val wd = windows.getOrPut(tick.code) { WindowData() }

            when (tick.side) {
                TdxSocketClient.TickData.Side.BUY -> {
                    wd.buyVol += tick.volume
                    wd.buyAmt += tick.amount
                    if (tick.amount >= LARGE_ORDER_THRESHOLD) {
                        wd.buyLargeVol += tick.volume
                    }
                }
                TdxSocketClient.TickData.Side.SELL -> {
                    wd.sellVol += tick.volume
                    wd.sellAmt += tick.amount
                    if (tick.amount >= LARGE_ORDER_THRESHOLD) {
                        wd.sellLargeVol += tick.volume
                    }
                }
            }

            val total = wd.buyVol + wd.sellVol
            val ratio = if (wd.sellVol > 0) wd.buyVol.toFloat() / wd.sellVol else 999f
            val pressure = if (total > 0) {
                ((wd.buyVol.toFloat() / total) * 100).toInt()
            } else 50

            BuySellResult(
                wd.buyVol, wd.sellVol, wd.buyAmt, wd.sellAmt,
                wd.buyLargeVol, wd.sellLargeVol,
                pressure, ratio
            )
        }
    }

    private class WindowData {
        var buyVol: Long = 0
        var sellVol: Long = 0
        var buyAmt: Float = 0f
        var sellAmt: Float = 0f
        var buyLargeVol: Long = 0
        var sellLargeVol: Long = 0
    }
}
