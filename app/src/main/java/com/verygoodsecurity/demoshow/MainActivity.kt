package com.verygoodsecurity.demoshow

import android.os.Bundle
import android.util.Log
import androidx.annotation.WorkerThread
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
        sendRequest?.setOnClickListener {
            thread(start = true) {
                makeTestRequest()
            }
        }
        addListener?.setOnClickListener {
                showVgs.addResponseListener(object : VGSResponseListener {

                    override fun onResponse(response: VGSResponse) {

                    }
                })
        }
        addSameListener?.setOnClickListener {
                showVgs.addResponseListener(this)
        }
        removeListener?.setOnClickListener {
                showVgs.removeResponseListener(this)
        }
        clearListener?.setOnClickListener {
                showVgs.clearResponseListeners()
        }
    }

    override fun onResponse(response: VGSResponse) {

    }

    @WorkerThread
    private fun makeTestRequest() {
        val response = showVgs.request("card_number_3", "tok_sandbox_nj9DWPJMFaP8U3HqXQ2DE")
        Log.d(VGSShow::class.simpleName, response.toString())
    }
}