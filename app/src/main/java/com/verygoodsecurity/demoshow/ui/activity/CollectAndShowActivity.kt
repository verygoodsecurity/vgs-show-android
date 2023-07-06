package com.verygoodsecurity.demoshow.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.verygoodsecurity.demoshow.R
import com.verygoodsecurity.demoshow.ui.CollectResponse
import com.verygoodsecurity.demoshow.ui.CollectSuccessResponse
import com.verygoodsecurity.demoshow.ui.MainActivity
import com.verygoodsecurity.demoshow.ui.MainActivity.Companion.TENANT_ID
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.widget.ExpirationDateEditText
import com.verygoodsecurity.vgscollect.widget.VGSCardNumberEditText
import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.core.VGSEnvironment
import com.verygoodsecurity.vgsshow.core.listener.VGSOnResponseListener
import com.verygoodsecurity.vgsshow.core.logs.VGSShowLogger
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse
import com.verygoodsecurity.vgsshow.widget.VGSTextView
import org.json.JSONException
import org.json.JSONObject

class CollectAndShowActivity : AppCompatActivity(), VGSOnResponseListener {

    private val showVgs: VGSShow by lazy {
        VGSShow.Builder(this, TENANT_ID)
            .setEnvironment(VGSEnvironment.Sandbox())
            .build()
    }

    private val vgsForm: VGSCollect by lazy {
        VGSCollect(this, TENANT_ID, MainActivity.ENVIRONMENT)
    }

    private val tvCardNumber: VGSTextView by lazy { findViewById(R.id.tvCardNumber) }
    private val tvCardExpiration: VGSTextView by lazy { findViewById(R.id.tvCardExpiration) }
    private val etCardNumber: VGSCardNumberEditText by lazy { findViewById(R.id.etCardNumber) }
    private val tvCardNumberAlias: TextView by lazy { findViewById(R.id.tvCardNumberAlias) }
    private val tvExpDateAlias: TextView by lazy { findViewById(R.id.tvExpDateAlias) }
    private val etExpDate: ExpirationDateEditText by lazy { findViewById(R.id.etExpDate) }
    private val pbReveal: ProgressBar by lazy { findViewById(R.id.pbReveal) }
    private val pbSubmit: ProgressBar by lazy { findViewById(R.id.pbSubmit) }
    private val mbSubmit: MaterialButton by lazy { findViewById(R.id.mbSubmit) }
    private val mbRequest: MaterialButton by lazy { findViewById(R.id.mbRequest) }
    private val mbSetSecureText: MaterialButton by lazy { findViewById(R.id.mbSetSecureText) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collect_and_show)
        setupCollect()
        setupShow()
    }

    override fun onDestroy() {
        super.onDestroy()
        vgsForm.onDestroy()
        showVgs.onDestroy()
    }

    private fun revealData() {
        pbReveal.visibility = View.VISIBLE
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
        pbReveal.visibility = View.GONE
        Log.d(CollectAndShowActivity::class.simpleName, response.toString())
    }

    private var revealAlias: String = ""
    private var revealAlias2: String = ""

    private fun setupCollect() {
        mbSubmit.setOnClickListener {
            pbSubmit.visibility = View.VISIBLE
            etCardNumber.isEnabled = false
            etExpDate.isEnabled = false
            vgsForm.asyncSubmit("/post", HTTPMethod.POST)
        }

        vgsForm.addOnResponseListeners(object : VgsCollectResponseListener {
            override fun onResponse(response: CollectResponse) {
                pbSubmit.visibility = View.GONE
                etCardNumber.isEnabled = true
                etExpDate.isEnabled = true

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
        vgsForm.bindView(etCardNumber)
        vgsForm.bindView(etExpDate)
    }

    private fun setupShow() {
        VGSShowLogger.isEnabled = true
        VGSShowLogger.level = VGSShowLogger.Level.DEBUG
        showVgs.addOnResponseListener(this)
        showVgs.subscribe(tvCardNumber)
        showVgs.subscribe(tvCardExpiration)

        tvCardNumber.addTransformationRegex(
            "(\\d{4})(\\d{4})(\\d{4})(\\d{4})".toRegex(),
            "\$1-\$2-\$3-\$4"
        )
        tvCardNumber.addTransformationRegex("-".toRegex(), " - ")

        tvCardNumber.setOnTextChangeListener(object : VGSTextView.OnTextChangedListener {
            override fun onTextChange(view: VGSTextView, isEmpty: Boolean) {
                Log.d(MainActivity::class.simpleName, "textIsEmpty: $isEmpty")
            }
        })
        tvCardNumber.addOnCopyTextListener(object : VGSTextView.OnTextCopyListener {

            override fun onTextCopied(view: VGSTextView, format: VGSTextView.CopyTextFormat) {
                Toast.makeText(
                    applicationContext,
                    "Number text copied! format: $format",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        tvCardNumber.setOnClickListener {
            tvCardNumber.copyToClipboard(VGSTextView.CopyTextFormat.RAW)
        }

        // Setup accessibility
        tvCardNumber.contentDescription = "Revealed"

        mbRequest.setOnClickListener {
            revealData()
        }
        mbSetSecureText.setOnClickListener {
            if (tvCardNumber.isSecureText) {
                tvCardNumber.isSecureText = false
                mbSetSecureText.text = "Set secure"
            } else {
                tvCardNumber.isSecureText = true
                mbSetSecureText.text = "Reset secure"
            }
        }
    }

    private fun parseDateAlias(json: JSONObject?) {
        json?.let {
            if (it.has("json") && it.getJSONObject("json").has("expDate")) {
                it.getJSONObject("json").getString("expDate").let { date ->
                    tvExpDateAlias.text = date
                    revealAlias2 = date
                }
            }
        }
    }

    private fun parseNumberAlias(json: JSONObject?) {
        json?.let {
            if (it.has("json") && it.getJSONObject("json").has("cardNumber")) {
                it.getJSONObject("json").getString("cardNumber").let { number ->
                    tvCardNumberAlias.text = number
                    revealAlias = number
                }
            }
        }
    }
}