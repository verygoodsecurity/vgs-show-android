package com.verygoodsecurity.demoshow

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
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
        VGSShow.Builder(this, "tntpszqgikn")
            .setEnvironment(VGSEnvironment.Sandbox())
            .setHostname("collect-android-testing.verygoodsecurity.io/test")
            .build()
    }

    private val vgsForm: VGSCollect by lazy {
        VGSCollect(this, "tntpszqgikn", "sandbox")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupCollect()
        setupShow()
    }

    override fun onDestroy() {
        super.onDestroy()
        vgsForm.onDestroy()
        showVgs.onDestroy()
    }

    private fun revealData() {
        progressReveal?.visibility = View.VISIBLE
        showVgs.requestAsync(
            VGSRequest.Builder("post", VGSHttpMethod.POST).body(
                mapOf(
                    "payment_card_number" to revealtoken
                )
            ).build()
        )
    }

    override fun onResponse(response: VGSResponse) {
        progressReveal?.visibility = View.GONE
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
                submitProgress?.visibility = View.GONE
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

    private fun setupShow() {
        showVgs.addResponseListener(this)
        showVgs.subscribeView(number)
        showVgs.subscribeView(expiration)

        number.addTransformationRegex(
            "(\\d{4})(\\d{4})(\\d{4})(\\d{4})".toRegex(),
            "\$1-\$2-\$3-\$4"
        )
        number.addTransformationRegex("-".toRegex(), " - ")

        number?.setOnTextChangeListener(object : VGSTextView.OnTextChangedListener {
            override fun onTextChange(view: VGSTextView, isEmpty: Boolean) {
                Log.e("test", "state text: $isEmpty")
            }
        })
        number?.addOnCopyTextListener(object : VGSTextView.OnTextCopyListener {

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
        requestButton?.setOnClickListener {
            revealData()
        }
        applyResetPasswordType?.setOnClickListener {
            if (number.isPasswordInputType()) {
                number.setInputType(EditorInfo.TYPE_NULL)
                applyResetPasswordType?.text = "Set password"
            } else {
                number.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
                number.setPasswordRange(6, 12)
                applyResetPasswordType?.text = "Reset password"
            }
        }
    }

    private fun parseDateToken(json: JSONObject?) {
        json?.let {
            if (it.has("json") && it.getJSONObject("json").has("expDate")) {
                it.getJSONObject("json").getString("expDate")?.let {
                    tokenView2?.text = it
                    revealtoken2 = it
                }
            }
        }
    }

    private fun parseNumberToken(json: JSONObject?) {
        json?.let {
            if (it.has("json") && it.getJSONObject("json").has("cardNumber")) {
                it.getJSONObject("json").getString("cardNumber").let {
                    tokenView1?.text = it
                    revealtoken = it
                }
            }
        }
    }
}

typealias CollectResponse = com.verygoodsecurity.vgscollect.core.model.network.VGSResponse?
typealias CollectSuccessResponse = com.verygoodsecurity.vgscollect.core.model.network.VGSResponse.SuccessResponse?