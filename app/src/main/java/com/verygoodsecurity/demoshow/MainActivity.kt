package com.verygoodsecurity.demoshow

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.core.Environment
import com.verygoodsecurity.vgsshow.core.listener.VGSResponseListener
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), VGSResponseListener {

    private val showVgs: VGSShow by lazy {
        VGSShow(this, "tntaq8uft80", Environment.SANDBOX)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showVgs.addResponseListener(this)
        showVgs.bindView(vgsSecureView)

        requestButton?.setOnClickListener {
            thread(start = true) {
                showVgs.request("card_number", "tok_sandbox_nj9DWPJMFaP8U3HqXQ2DE")
            }
        }
    }

    override fun onResponse(response: VGSResponse) {
        Log.d(MainActivity::class.simpleName, response.toString())
    }
}