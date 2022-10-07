package com.verygoodsecurity.demoshow.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.verygoodsecurity.demoshow.R
import com.verygoodsecurity.demoshow.ui.activity.CollectAndShowActivity
import com.verygoodsecurity.demoshow.ui.activity.CollectAndShowFragmentActivity
import com.verygoodsecurity.demoshow.ui.activity.PDFActivity
import com.verygoodsecurity.demoshow.ui.activity.VGSShowViewPagerActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_main)
        initListeners()
    }

    private fun initListeners() {
        findViewById<MaterialButton>(R.id.btnStartActivityMain)?.setOnClickListener {
            startActivity(Intent(this, CollectAndShowActivity::class.java))
        }
        findViewById<MaterialButton>(R.id.btnStartFragmentMain)?.setOnClickListener {
            startActivity(Intent(this, CollectAndShowFragmentActivity::class.java))
        }
        findViewById<MaterialButton>(R.id.btnStartViewPagerMain)?.setOnClickListener {
            startActivity(Intent(this, VGSShowViewPagerActivity::class.java))
        }
        findViewById<MaterialButton>(R.id.btnStartRevelPDF)?.setOnClickListener {
            startActivity(Intent(this, PDFActivity::class.java))
        }
    }

    companion object {

        const val TENANT_ID = "tntpszqgikn"
        const val ENVIRONMENT = "sandbox"
        const val COLLECT_CUSTOM_HOSTNAME = "collect-android-testing.verygoodsecurity.io/test"
    }
}

typealias ShowResponse = com.verygoodsecurity.vgsshow.core.network.model.VGSResponse
typealias CollectResponse = com.verygoodsecurity.vgscollect.core.model.network.VGSResponse?
typealias CollectSuccessResponse = com.verygoodsecurity.vgscollect.core.model.network.VGSResponse.SuccessResponse?
