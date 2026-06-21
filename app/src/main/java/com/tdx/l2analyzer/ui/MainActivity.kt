package com.tdx.l2analyzer.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tdx.l2analyzer.R
import com.tdx.l2analyzer.service.L2DataService
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var etStockCode: EditText
    private lateinit var btnStart: Button
    private lateinit var btnStop: Button
    private lateinit var tvPressure: TextView
    private lateinit var tvRatio: TextView
    private lateinit var tvRhythm: TextView
    private lateinit var tvLargeOrders: TextView

    private var isRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etStockCode = findViewById(R.id.et_stock_code)
        btnStart = findViewById(R.id.btn_start)
        btnStop = findViewById(R.id.btn_stop)
        tvPressure = findViewById(R.id.tv_pressure)
        tvRatio = findViewById(R.id.tv_ratio)
        tvRhythm = findViewById(R.id.tv_rhythm)
        tvLargeOrders = findViewById(R.id.tv_large_orders)

        btnStop.isEnabled = false

        btnStart.setOnClickListener { startAnalysis() }
        btnStop.setOnClickListener { stopAnalysis() }
    }

    private fun startAnalysis() {
        val code = etStockCode.text.toString().trim()
        if (code.length != 6) {
            Toast.makeText(this, "请输入6位股票代码", Toast.LENGTH_SHORT).show()
            return
        }
        isRunning = true
        btnStart.isEnabled = false
        btnStop.isEnabled = true

        val intent = Intent(this, L2DataService::class.java).apply {
            putExtra("stock_code", code)
        }
        startForegroundService(intent)
        Toast.makeText(this, "开始分析 $code", Toast.LENGTH_SHORT).show()
    }

    private fun stopAnalysis() {
        isRunning = false
        btnStart.isEnabled = true
        btnStop.isEnabled = false
        stopService(Intent(this, L2DataService::class.java))
        Toast.makeText(this, "已停止分析", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isRunning) {
            stopService(Intent(this, L2DataService::class.java))
        }
    }
}
