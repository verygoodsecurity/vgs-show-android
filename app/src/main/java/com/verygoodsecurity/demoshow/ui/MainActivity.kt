package com.verygoodsecurity.demoshow.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.verygoodsecurity.demoshow.R
import com.verygoodsecurity.demoshow.ui.activity.VGSShowActivity
import com.verygoodsecurity.demoshow.ui.activity.VGSShowFragmentActivity
import com.verygoodsecurity.demoshow.ui.activity.VGSShowViewPagerActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_main)
        initListeners()
    }

    private fun initListeners() {
        btnStartActivityMain?.setOnClickListener {
            startActivity(Intent(this, VGSShowActivity::class.java))
        }
        btnStartFragmentMain?.setOnClickListener {
            startActivity(Intent(this, VGSShowFragmentActivity::class.java))
        }
        btnStartViewPagerMain?.setOnClickListener {
            startActivity(Intent(this, VGSShowViewPagerActivity::class.java))
        }
    }

    companion object {

        const val TENANT_ID = "tntpszqgikn"
        const val ENVIRONMENT = "sandbox"
        const val COLLECT_CUSTOM_HOSTNAME = "collect-android-testing.verygoodsecurity.io/test"
    }
}

typealias CollectResponse = com.verygoodsecurity.vgscollect.core.model.network.VGSResponse?
typealias CollectSuccessResponse = com.verygoodsecurity.vgscollect.core.model.network.VGSResponse.SuccessResponse?
