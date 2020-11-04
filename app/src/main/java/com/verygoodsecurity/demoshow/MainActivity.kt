package com.verygoodsecurity.demoshow

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.core.VGSEnvironment
import com.verygoodsecurity.vgsshow.core.listener.VgsShowResponseListener
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity(), VgsShowResponseListener {

    private val showVgs: VGSShow by lazy {
        VGSShow(this, "tntpszqgikn", VGSEnvironment.Sandbox())
    }

    private val vgsForm: VGSCollect by lazy {
        VGSCollect(this, "tntpszqgikn", "sandbox")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupCollect()

        showVgs.addResponseListener(this)
        showVgs.bindView(number)
        showVgs.bindView(expiration)

        requestButton?.setOnClickListener {
            revealData()
        }
    }

    private fun revealData() {
        progressReveal?.visibility = View.VISIBLE
        showVgs.requestAsync(
            VGSRequest.Builder("post", VGSHttpMethod.POST).body(makeJsonObject()).build()
        )
    }

    override fun onResponse(response: VGSResponse) {
        progressReveal?.visibility = View.INVISIBLE
        Log.d(MainActivity::class.simpleName, response.toString())
    }

    private var revealtoken: String = ""
    private var revealtoken2: String = ""

    private fun setupCollect() {
        submitButton?.setOnClickListener {
            submitProgress?.visibility = View.VISIBLE
            cardNumber?.isEnabled = false
            expDate?.isEnabled = false
            vgsForm.asyncSubmit("/post", HTTPMethod.POST)
        }

        vgsForm.addOnResponseListeners(object : VgsCollectResponseListener {
            override fun onResponse(response: CollectResponse) {
                submitProgress?.visibility = View.INVISIBLE
                cardNumber?.isEnabled = true
                expDate?.isEnabled = true

                try {
                    val json = when (response) {
                        is CollectSuccessResponse -> JSONObject(response?.rawResponse)
                        else -> null
                    }


                    parseNumberToken(json)
                    parseDateToken(json)
                } catch (e: JSONException) {
                }
            }
        })
        vgsForm.bindView(cardNumber)
        vgsForm.bindView(expDate)
    }

    private fun parseDateToken(json: JSONObject?) {
        json?.let {
            if (it.has("json") && it.getJSONObject("json").has("expDate")) {
                it.getJSONObject("json").getString("expDate")?.let {
                    tokenView2?.text = "token: $it"
                    revealtoken2 = it
                }
            }
        }
    }

    private fun parseNumberToken(json: JSONObject?) {
        json?.let {
            if (it.has("json") && it.getJSONObject("json").has("cardNumber")) {
                it.getJSONObject("json").getString("cardNumber").let {
                    tokenView1?.text = "token: $it"
                    revealtoken = it
                }
            }
        }
    }

    private fun makeJsonObject(): JSONObject {
        return with(JSONObject()) {
            put("number", revealtoken)
            put("expiration", revealtoken2)
            this
        }
    }

}

typealias CollectResponse = com.verygoodsecurity.vgscollect.core.model.network.VGSResponse?
typealias CollectSuccessResponse = com.verygoodsecurity.vgscollect.core.model.network.VGSResponse.SuccessResponse?