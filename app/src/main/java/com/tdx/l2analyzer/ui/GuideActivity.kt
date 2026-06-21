package com.tdx.l2analyzer.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tdx.l2analyzer.R
import kotlinx.coroutines.*

class GuideActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide)

        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
            startActivity(Intent(this@GuideActivity, MainActivity::class.java))
            finish()
        }
    }
}
