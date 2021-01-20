package com.verygoodsecurity.demoshow.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.verygoodsecurity.demoshow.R
import com.verygoodsecurity.demoshow.ui.CollectResponse
import com.verygoodsecurity.demoshow.ui.CollectSuccessResponse
import com.verygoodsecurity.demoshow.ui.MainActivity
import com.verygoodsecurity.demoshow.ui.MainActivity.Companion.COLLECT_CUSTOM_HOSTNAME
import com.verygoodsecurity.demoshow.ui.MainActivity.Companion.TENANT_ID
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
        VGSShow.Builder(this, TENANT_ID).setHostname(COLLECT_CUSTOM_HOSTNAME).build()
    }

    private val vgsForm: VGSCollect by lazy {
        VGSCollect(this, TENANT_ID, MainActivity.ENVIRONMENT)
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
        pbRevealVGSActivity?.visibility = View.VISIBLE
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
        pbRevealVGSActivity?.visibility = View.GONE
        Log.d(VGSShowActivity::class.simpleName, response.toString())
    }

    private var revealAlias: String = ""
    private var revealAlias2: String = ""

    private fun setupCollect() {
        mbSubmitVGSActivity?.setOnClickListener {
            pbSubmitVGSActivity?.visibility = View.VISIBLE
            etCardNumberVGSActivity?.isEnabled = false
            etExpDateVGSActivity?.isEnabled = false
            vgsForm.asyncSubmit("/post", HTTPMethod.POST)
        }

        vgsForm.addOnResponseListeners(object : VgsCollectResponseListener {
            override fun onResponse(response: CollectResponse) {
                pbSubmitVGSActivity?.visibility = View.GONE
                etCardNumberVGSActivity?.isEnabled = true
                etExpDateVGSActivity?.isEnabled = true

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
        vgsForm.bindView(etCardNumberVGSActivity)
        vgsForm.bindView(etExpDateVGSActivity)
    }

    private fun setupShow() {
        showVgs.addOnResponseListener(this)
        showVgs.subscribe(tvCardNumberVGSActivity)
        showVgs.subscribe(tvCardExpirationVGSActivity)

        tvCardNumberVGSActivity?.addTransformationRegex(
            "(\\d{4})(\\d{4})(\\d{4})(\\d{4})".toRegex(),
            "\$1-\$2-\$3-\$4"
        )
        tvCardNumberVGSActivity?.addTransformationRegex("-".toRegex(), " - ")

        tvCardNumberVGSActivity?.setOnTextChangeListener(object : VGSTextView.OnTextChangedListener {
            override fun onTextChange(view: VGSTextView, isEmpty: Boolean) {
                Log.d(MainActivity::class.simpleName, "textIsEmpty: $isEmpty")
            }
        })
        tvCardNumberVGSActivity?.addOnCopyTextListener(object : VGSTextView.OnTextCopyListener {

            override fun onTextCopied(view: VGSTextView, format: VGSTextView.CopyTextFormat) {
                Toast.makeText(
                    applicationContext,
                    "Number text copied! format: $format",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        tvCardNumberVGSActivity?.setOnClickListener {
            tvCardNumberVGSActivity.copyToClipboard(VGSTextView.CopyTextFormat.RAW)
        }
        mbRequestVGSActivity?.setOnClickListener {
            revealData()
        }
        mbSetSecureTextVGSActivity?.setOnClickListener {
            if (tvCardNumberVGSActivity?.isSecureText == true) {
                tvCardNumberVGSActivity?.isSecureText = false
                mbSetSecureTextVGSActivity?.text = "Set secure"
            } else {
                tvCardNumberVGSActivity?.isSecureText = true
                mbSetSecureTextVGSActivity?.text = "Reset secure"
            }
        }
    }

    private fun parseDateAlias(json: JSONObject?) {
        json?.let {
            if (it.has("json") && it.getJSONObject("json").has("expDate")) {
                it.getJSONObject("json").getString("expDate").let { date ->
                    tvExpDateAliasVGSActivity?.text = date
                    revealAlias2 = date
                }
            }
        }
    }

    private fun parseNumberAlias(json: JSONObject?) {
        json?.let {
            if (it.has("json") && it.getJSONObject("json").has("cardNumber")) {
                it.getJSONObject("json").getString("cardNumber").let { number ->
                    tvCardNumberAliasVGSActivity?.text = number
                    revealAlias = number
                }
            }
        }
    }
}