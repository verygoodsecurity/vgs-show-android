package com.verygoodsecurity.demoshow.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.verygoodsecurity.demoshow.R
import com.verygoodsecurity.demoshow.ui.CollectResponse
import com.verygoodsecurity.demoshow.ui.CollectSuccessResponse
import com.verygoodsecurity.demoshow.ui.MainActivity
import com.verygoodsecurity.demoshow.ui.MainActivity.Companion.COLLECT_CUSTOM_HOSTNAME
import com.verygoodsecurity.demoshow.ui.MainActivity.Companion.ENVIRONMENT
import com.verygoodsecurity.demoshow.ui.MainActivity.Companion.TENANT_ID
import com.verygoodsecurity.demoshow.ui.activity.VGSShowActivity
import com.verygoodsecurity.demoshow.utils.extension.setVisible
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
import kotlinx.android.synthetic.main.fragment_vgs_show.*
import org.json.JSONObject

class VGSShowFragment : Fragment(R.layout.fragment_vgs_show) {

    private val show: VGSShow by lazy {
        VGSShow.Builder(requireContext(), TENANT_ID).setHostname(COLLECT_CUSTOM_HOSTNAME).build()
    }

    private val collect: VGSCollect by lazy {
        VGSCollect(requireContext(), TENANT_ID, ENVIRONMENT)
    }

    private var cardNumberAlias: String = ""
    private var expirationDateAlias: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCollect()
        setupShow()
    }

    private fun setupCollect() {

        fun setLoading(isLoading: Boolean) {
            pbSubmitVGSFragment?.setVisible(isLoading)
            etCardNumberVGSFragment?.isEnabled = !isLoading
            etExpDateVGSFragment?.isEnabled = !isLoading
        }

        btnSubmitVGSFragment?.setOnClickListener {
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
                        tvCardNumberTokenVGSFragment?.text = cardNumberAlias
                        tvExpDateTokenVGSFragment?.text = expirationDateAlias
                    }
                } catch (e: Exception) {
                    Log.e(VGSShowFragment::class.java.simpleName, e.message ?: "")
                }
            }
        })

        collect.bindView(etCardNumberVGSFragment)
        collect.bindView(etExpDateVGSFragment)
    }

    @SuppressLint("SetTextI18n")
    private fun setupShow() {
        show.addOnResponseListener(object : VGSOnResponseListener {

            override fun onResponse(response: VGSResponse) {
                pbRevealVGSFragment?.setVisible(false)
                Log.d(VGSShowActivity::class.simpleName, response.toString())
            }
        })
        show.subscribe(vtvCardNumberVGSFragment)
        show.subscribe(vtvExpirationVGSFragment)

        vtvCardNumberVGSFragment?.addTransformationRegex(
            "(\\d{4})(\\d{4})(\\d{4})(\\d{4})".toRegex(),
            "\$1-\$2-\$3-\$4"
        )
        vtvCardNumberVGSFragment?.addTransformationRegex("-".toRegex(), " - ")

        vtvCardNumberVGSFragment?.setOnTextChangeListener(object : VGSTextView.OnTextChangedListener {
            override fun onTextChange(view: VGSTextView, isEmpty: Boolean) {
                Log.d(MainActivity::class.simpleName, "textIsEmpty: $isEmpty")
            }
        })
        vtvCardNumberVGSFragment?.addOnCopyTextListener(object : VGSTextView.OnTextCopyListener {

            override fun onTextCopied(view: VGSTextView, format: VGSTextView.CopyTextFormat) {
                Toast.makeText(
                    requireContext().applicationContext,
                    "Number text copied! format: $format",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        vtvCardNumberVGSFragment?.setOnClickListener {
            vtvCardNumberVGSFragment.copyToClipboard(VGSTextView.CopyTextFormat.RAW)
        }
        btnRequestVGSFragment?.setOnClickListener {
            pbRevealVGSFragment?.setVisible(true)
            show.requestAsync(
                VGSRequest.Builder("post", VGSHttpMethod.POST).body(
                    mapOf(
                        "payment_card_number" to cardNumberAlias,
                        "payment_card_expiration_date" to expirationDateAlias
                    )
                ).build()
            )
        }
        btnSecureTextVGSFragment?.setOnClickListener {
            if (vtvCardNumberVGSFragment?.isSecureText == true) {
                vtvCardNumberVGSFragment?.isSecureText = false
                btnSecureTextVGSFragment?.text = "Set secure"
            } else {
                vtvCardNumberVGSFragment?.isSecureText = true
                btnSecureTextVGSFragment?.text = "Reset secure"
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