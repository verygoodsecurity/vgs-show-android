package com.verygoodsecurity.demoshow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
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
import com.verygoodsecurity.vgsshow.widget.VGSTextView
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity(), VgsShowResponseListener {

    private val showVgs: VGSShow by lazy {
        VGSShow(this, "tntaq8uft80", VGSEnvironment.Sandbox())
    }

    private val vgsForm: VGSCollect by lazy {
        VGSCollect(this, "tntaq8uft80", "sandbox")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupCollect()

//        number.setTransformationRegex("(\\d{4})(\\d{4})(\\d{4})(\\d{4})", "\$1-\$2-\$3-\$4")

        showVgs.addResponseListener(this)
        showVgs.subscribeView(number)

        number?.setOnTextChangeListener(object : VGSTextView.OnTextChangedListener {
            override fun onTextChange(view: VGSTextView, isEmpty: Boolean) {
                Log.e("test", "state text: $isEmpty")
            }
        })
        number?.setOnCopyTextListener(object : VGSTextView.OnTextCopyListener {

            override fun onTextCopied(view: VGSTextView, format: VGSTextView.CopyTextFormat) {
                Toast.makeText(
                    applicationContext,
                    "Number text copied! format: $format",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        number?.setOnClickListener {
            number.copyToClipboard(VGSTextView.CopyTextFormat.RAW)
        }
        showVgs.subscribeView(expiration)
        showVgs.subscribeView(revealImageUrl)
        showVgs.subscribeView(revealImageBase64)

        requestButton?.setOnClickListener {
            revealData()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        vgsForm.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        vgsForm.onDestroy()
        showVgs.onDestroy()
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
    private var revealtoken3: String = ""
    private var revealtoken4: String = ""

    private fun setupCollect() {
        attachImage?.setOnClickListener {
            if(vgsForm.getFileProvider().getAttachedFiles().isEmpty()) {
                vgsForm.getFileProvider().attachFile("imageBase64")
            } else {
                vgsForm.getFileProvider().detachAll()
            }
        }
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
                    parseImageBase64Token(json)
                    parseImageUrlToken(json)
                } catch (e: JSONException) {
                }
            }
        })
        vgsForm.bindView(cardNumber)
        vgsForm.bindView(expDate)
        vgsForm.bindView(imageUrl)
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

    private fun parseImageUrlToken(json: JSONObject?) {
        json?.let {
            if (it.has("json") && it.getJSONObject("json").has("imageUrl3")) {
                it.getJSONObject("json").getString("imageUrl3")?.let {
                    tokenView3?.text = "token: $it"
                    revealtoken3 = it
                }
            }
        }
    }

    private fun parseImageBase64Token(json: JSONObject?) {
        json?.let {
            if (it.has("json") && it.getJSONObject("json").has("imageBase64")) {
                it.getJSONObject("json").getString("imageBase64")?.let {
                    tokenView4?.text = "token: $it"
                    revealtoken4 = it
                }
            }
        }
    }

    private fun makeJsonObject(): JSONObject {
        return with(JSONObject()) {
            put("number", revealtoken)
            put("expiration", revealtoken2)
            put("revealImageUrl3", revealtoken3)
            put("revealImageBase64", revealtoken4)
            this
        }
    }

}

typealias CollectResponse = com.verygoodsecurity.vgscollect.core.model.network.VGSResponse?
typealias CollectSuccessResponse = com.verygoodsecurity.vgscollect.core.model.network.VGSResponse.SuccessResponse?