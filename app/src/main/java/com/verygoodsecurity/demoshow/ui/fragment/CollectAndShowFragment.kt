package com.verygoodsecurity.demoshow.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.verygoodsecurity.demoshow.R
import com.verygoodsecurity.demoshow.ui.CollectResponse
import com.verygoodsecurity.demoshow.ui.CollectSuccessResponse
import com.verygoodsecurity.demoshow.ui.MainActivity
import com.verygoodsecurity.demoshow.ui.MainActivity.Companion.COLLECT_CUSTOM_HOSTNAME
import com.verygoodsecurity.demoshow.ui.MainActivity.Companion.ENVIRONMENT
import com.verygoodsecurity.demoshow.ui.MainActivity.Companion.TENANT_ID
import com.verygoodsecurity.demoshow.ui.activity.CollectAndShowActivity
import com.verygoodsecurity.demoshow.utils.extension.setVisible
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.widget.ExpirationDateEditText
import com.verygoodsecurity.vgscollect.widget.VGSCardNumberEditText
import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.core.listener.VGSOnResponseListener
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse
import com.verygoodsecurity.vgsshow.widget.VGSTextView
import org.json.JSONObject

class CollectAndShowFragment : Fragment(R.layout.fragment_collect_and_show) {

    private val show: VGSShow by lazy {
        VGSShow.Builder(requireContext(), TENANT_ID).setHostname(COLLECT_CUSTOM_HOSTNAME).build()
    }

    private val collect: VGSCollect by lazy {
        VGSCollect(requireContext(), TENANT_ID, ENVIRONMENT)
    }

    private val tvCardNumber: VGSTextView? by lazy { view?.findViewById(R.id.tvCardNumber) }
    private val tvCardExpiration: VGSTextView? by lazy { view?.findViewById(R.id.tvCardExpiration) }
    private val tvExpDateAlias: TextView? by lazy { view?.findViewById(R.id.tvExpDateAlias) }
    private val tvCardNumberAlias: TextView? by lazy { view?.findViewById(R.id.tvCardNumberAlias) }
    private val pbReveal: ProgressBar? by lazy { view?.findViewById(R.id.pbReveal) }
    private val mbRequest: MaterialButton? by lazy { view?.findViewById(R.id.mbRequest) }
    private val mbSetSecureText: MaterialButton? by lazy { view?.findViewById(R.id.mbSetSecureText) }
    private val etCardNumber: VGSCardNumberEditText? by lazy { view?.findViewById(R.id.etCardNumber) }
    private val etExpDate: ExpirationDateEditText? by lazy { view?.findViewById(R.id.etExpDate) }
    private val pbSubmit: ProgressBar? by lazy { view?.findViewById(R.id.pbReveal) }
    private val mbSubmit: MaterialButton? by lazy { view?.findViewById(R.id.mbSubmit) }

    private var cardNumberAlias: String = ""
    private var expirationDateAlias: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCollect()
        setupShow()
    }

    private fun setupCollect() {

        fun setLoading(isLoading: Boolean) {
            pbSubmit?.setVisible(isLoading)
            etCardNumber?.isEnabled = !isLoading
            etExpDate?.isEnabled = !isLoading
        }

        mbSubmit?.setOnClickListener {
            setLoading(true)
            collect.asyncSubmit("/post", HTTPMethod.POST)
        }

        collect.addOnResponseListeners(object : VgsCollectResponseListener {

            override fun onResponse(response: CollectResponse) {
                setLoading(false)
                try {
                    with(JSONObject((response as? CollectSuccessResponse)?.rawResponse ?: "")) {
                        cardNumberAlias = parseAlias(this, "cardNumber")
                        expirationDateAlias = parseAlias(this, "expDate")
                        tvCardNumberAlias?.text = cardNumberAlias
                        tvExpDateAlias?.text = expirationDateAlias
                    }
                } catch (e: Exception) {
                    Log.e(CollectAndShowFragment::class.java.simpleName, e.message ?: "")
                }
            }
        })

        collect.bindView(etCardNumber)
        collect.bindView(etExpDate)
    }

    @SuppressLint("SetTextI18n")
    private fun setupShow() {
        show.addOnResponseListener(object : VGSOnResponseListener {

            override fun onResponse(response: VGSResponse) {
                pbReveal?.setVisible(false)
                Log.d(CollectAndShowActivity::class.simpleName, response.toString())
            }
        })
        tvCardNumber?.let { show.subscribe(it) }
        tvCardExpiration?.let { show.subscribe(it) }

        tvCardNumber?.addTransformationRegex(
            "(\\d{4})(\\d{4})(\\d{4})(\\d{4})".toRegex(),
            "\$1-\$2-\$3-\$4"
        )
        tvCardNumber?.addTransformationRegex("-".toRegex(), " - ")

        tvCardNumber?.setOnTextChangeListener(object :
            VGSTextView.OnTextChangedListener {
            override fun onTextChange(view: VGSTextView, isEmpty: Boolean) {
                Log.d(MainActivity::class.simpleName, "textIsEmpty: $isEmpty")
            }
        })
        tvCardNumber?.addOnCopyTextListener(object : VGSTextView.OnTextCopyListener {

            override fun onTextCopied(view: VGSTextView, format: VGSTextView.CopyTextFormat) {
                Toast.makeText(
                    requireContext().applicationContext,
                    "Number text copied! format: $format",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        tvCardNumber?.setOnClickListener {
            tvCardNumber?.copyToClipboard(VGSTextView.CopyTextFormat.RAW)
        }
        mbRequest?.setOnClickListener {
            pbReveal?.setVisible(true)
            show.requestAsync(
                VGSRequest.Builder("post", VGSHttpMethod.POST).body(
                    mapOf(
                        "payment_card_number" to cardNumberAlias,
                        "payment_card_expiration_date" to expirationDateAlias
                    )
                ).build()
            )
        }
        mbSetSecureText?.setOnClickListener {
            if (tvCardNumber?.isSecureText == true) {
                tvCardNumber?.isSecureText = false
                mbSetSecureText?.text = "Set secure"
            } else {
                tvCardNumber?.isSecureText = true
                mbSetSecureText?.text = "Reset secure"
            }
        }
    }

    private fun parseAlias(data: JSONObject, path: String): String {
        return if (data.has("json") && data.getJSONObject("json").has(path)) {
            data.getJSONObject("json").getString(path)
        } else {
            ""
        }
    }
}