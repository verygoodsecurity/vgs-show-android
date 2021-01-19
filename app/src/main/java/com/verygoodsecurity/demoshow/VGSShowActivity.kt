package com.verygoodsecurity.demoshow

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.core.listener.VGSOnResponseListener
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse
import com.verygoodsecurity.vgsshow.widget.VGSTextView
import kotlinx.android.synthetic.main.activity_vgs_show.*
import org.json.JSONException
import org.json.JSONObject

class VGSShowActivity : AppCompatActivity(), VGSOnResponseListener {

    private val showVgs: VGSShow by lazy {
        VGSShow.Builder(this, "tntpszqgikn")
            .setHostname("collect-android-testing.verygoodsecurity.io/test")
            .build()
    }

    private val vgsForm: VGSCollect by lazy {
        VGSCollect(this, "tntpszqgikn", "sandbox")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vgs_show)
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
                    "payment_card_number" to revealAlias,
                    "payment_card_expiration_date" to revealAlias2
                )
            ).build()
        )
    }

    override fun onResponse(response: VGSResponse) {
        progressReveal?.visibility = View.GONE
        Log.d(MainActivity::class.simpleName, response.toString())
    }

    private var revealAlias: String = ""
    private var revealAlias2: String = ""

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

                    parseNumberAlias(json)
                    parseDateAlias(json)
                } catch (e: JSONException) {
                }
            }
        })
        vgsForm.bindView(cardNumber)
        vgsForm.bindView(expDate)
    }

    private fun setupShow() {
        showVgs.addOnResponseListener(this)
        showVgs.subscribe(number)
        showVgs.subscribe(expiration)

        number?.addTransformationRegex(
            "(\\d{4})(\\d{4})(\\d{4})(\\d{4})".toRegex(),
            "\$1-\$2-\$3-\$4"
        )
        number?.addTransformationRegex("-".toRegex(), " - ")

        number?.setOnTextChangeListener(object : VGSTextView.OnTextChangedListener {
            override fun onTextChange(view: VGSTextView, isEmpty: Boolean) {
                Log.d(MainActivity::class.simpleName, "textIsEmpty: $isEmpty")
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
            if (number?.isSecureText == true) {
                number?.isSecureText = false
                applyResetPasswordType?.text = "Set secure"
            } else {
                number?.isSecureText = true
                applyResetPasswordType?.text = "Reset secure"
            }
        }
    }

    private fun parseDateAlias(json: JSONObject?) {
        json?.let {
            if (it.has("json") && it.getJSONObject("json").has("expDate")) {
                it.getJSONObject("json").getString("expDate").let { date ->
                    tokenView2?.text = date
                    revealAlias2 = date
                }
            }
        }
    }

    private fun parseNumberAlias(json: JSONObject?) {
        json?.let {
            if (it.has("json") && it.getJSONObject("json").has("cardNumber")) {
                it.getJSONObject("json").getString("cardNumber").let { number ->
                    tokenView1?.text = number
                    revealAlias = number
                }
            }
        }
    }
}

typealias CollectResponse = com.verygoodsecurity.vgscollect.core.model.network.VGSResponse?
typealias CollectSuccessResponse = com.verygoodsecurity.vgscollect.core.model.network.VGSResponse.SuccessResponse?