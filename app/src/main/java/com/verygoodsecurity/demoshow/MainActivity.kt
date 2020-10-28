package com.verygoodsecurity.demoshow

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import androidx.appcompat.app.AppCompatActivity
import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.core.Environment
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private val showVgs: VGSShow by lazy {
        VGSShow(this, "tntaq8uft80", Environment.SANDBOX)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showVgs.bindView(vgsSecureView)

        btn?.setOnClickListener {
            thread(start = true) {
                showVgs.request("card_number_3", "tok_sandbox_nj9DWPJMFaP8U3HqXQ2DE")
            }
        }
    }
}