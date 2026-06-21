package com.tdx.l2analyzer.analyzer

import com.tdx.l2analyzer.data.TdxSocketClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.ArrayDeque

object RhythmAnalyzer {

    data class RhythmResult(
        val code: String,
        val tickRate: Float,
        val continuityScore: Int,
        val buyStreak: Int,
        val sellStreak: Int,
        val avgInterval: Float,
        val rhythmScore: Int,
        val timestamp: Long = System.currentTimeMillis()
    )

    private const val WINDOW_SIZE = 30

    fun analyze(tickFlow: Flow<TdxSocketClient.TickData>): Flow<RhythmResult> {
        val buffers = HashMap<String, TickBuffer>()

        return tickFlow.map { tick ->
            val buf = buffers.getOrPut(tick.code) { TickBuffer() }
            buf.add(tick)

            val rate = buf.getTickRate()
            val (buyStreak, sellStreak) = buf.getStreaks()
            val avgInterval = buf.getAvgInterval()
            val continuityScore = calcContinuity(buyStreak, sellStreak)
            val rhythmScore = calcRhythmScore(rate, continuityScore, avgInterval)

            RhythmResult(
                tick.code, rate, continuityScore,
                buyStreak, sellStreak, avgInterval, rhythmScore
            )
        }
    }

    private fun calcContinuity(buyStreak: Int, sellStreak: Int): Int {
        val maxStreak = maxOf(buyStreak, sellStreak)
        return (maxStreak * 10).coerceAtMost(100)
    }

    private fun calcRhythmScore(rate: Float, continuity: Int, avgInterval: Float): Int {
        val rateScore = when {
            rate > 10 -> 90
            rate > 5 -> 70
            rate > 2 -> 50
            else -> 30
        }
        return ((rateScore + continuity) / 2).coerceIn(0, 100)
    }

    private class TickBuffer {
        private val deque = ArrayDeque<TdxSocketClient.TickData>(WINDOW_SIZE)

        fun add(tick: TdxSocketClient.TickData) {
            deque.addLast(tick)
            while (deque.size > WINDOW_SIZE) deque.removeFirst()
        }

        fun getTicks(): List<TdxSocketClient.TickData> = deque.toList()

        fun getTickRate(): Float {
            if (deque.size < 2) return 0f
            val first = deque.first().time
            val last = deque.last().time
            val spanSec = (last - first) / 1000f
            return if (spanSec > 0) deque.size / spanSec else 0f
        }

        fun getStreaks(): Pair<Int, Int> {
            if (deque.isEmpty()) return 0 to 0
            var buyStreak = 0
            var sellStreak = 0
            var maxBuy = 0
            var maxSell = 0
            for (t in deque) {
                when (t.side) {
                    TdxSocketClient.TickData.Side.BUY -> {
                        buyStreak++; sellStreak = 0
                        maxBuy = maxOf(maxBuy, buyStreak)
                    }
                    TdxSocketClient.TickData.Side.SELL -> {
                        sellStreak++; buyStreak = 0
                        maxSell = maxOf(maxSell, sellStreak)
                    }
                }
            }
            return maxBuy to maxSell
        }

        fun getAvgInterval(): Float {
            if (deque.size < 2) return 0f
            val list = deque.sortedBy { it.time }
            var total = 0L
            for (i in 1 until list.size) {
                total += (list[i].time - list[i - 1].time)
            }
            return total.toFloat() / (list.size - 1)
        }
    }
}
