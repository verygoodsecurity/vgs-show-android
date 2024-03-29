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
        VGSShow.Builder(requireContext(), TENANT_ID).build()
    }

    private val collect: VGSCollect by lazy {
        VGSCollect(requireContext(), TENANT_ID, ENVIRONMENT)
    }

    private var cardNumberAlias: String = ""
    private var expirationDateAlias: String = ""

    private lateinit var pbSubmit: ProgressBar
    private lateinit var mbSubmit: MaterialButton
    private lateinit var etCardNumber: VGSCardNumberEditText
    private lateinit var etExpDate: ExpirationDateEditText
    private lateinit var tvCardNumberAlias: TextView
    private lateinit var tvExpDateAlias: TextView
    private lateinit var pbReveal: ProgressBar
    private lateinit var tvCardNumber: VGSTextView
    private lateinit var tvCardExpiration: VGSTextView
    private lateinit var mbRequest: MaterialButton
    private lateinit var mbSetSecureText: MaterialButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setupCollect()
        setupShow()
    }

    private fun initViews(view: View) {
        pbSubmit = view.findViewById(R.id.pbSubmit)
        mbSubmit = view.findViewById(R.id.mbSubmit)
        etCardNumber = view.findViewById(R.id.etCardNumber)
        etExpDate = view.findViewById(R.id.etExpDate)
        tvCardNumberAlias = view.findViewById(R.id.tvCardNumberAlias)
        tvExpDateAlias = view.findViewById(R.id.tvExpDateAlias)
        pbReveal = view.findViewById(R.id.pbReveal)
        tvCardNumber = view.findViewById(R.id.tvCardNumber)
        tvCardExpiration = view.findViewById(R.id.tvCardExpiration)
        mbRequest = view.findViewById(R.id.mbRequest)
        mbSetSecureText = view.findViewById(R.id.mbSetSecureText)
    }

    private fun setupCollect() {

        fun setLoading(isLoading: Boolean) {
            pbSubmit.setVisible(isLoading)
            etCardNumber.isEnabled = !isLoading
            etExpDate.isEnabled = !isLoading
        }

        mbSubmit.setOnClickListener {
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
                        tvCardNumberAlias.text = cardNumberAlias
                        tvExpDateAlias.text = expirationDateAlias
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
                pbReveal.setVisible(false)
                Log.d(CollectAndShowActivity::class.simpleName, response.toString())
            }
        })
        show.subscribe(tvCardNumber)
        show.subscribe(tvCardExpiration)

        tvCardNumber.addTransformationRegex(
            "(\\d{4})(\\d{4})(\\d{4})(\\d{4})".toRegex(),
            "\$1-\$2-\$3-\$4"
        )
        tvCardNumber.addTransformationRegex("-".toRegex(), " - ")

        tvCardNumber.setOnTextChangeListener(object :
            VGSTextView.OnTextChangedListener {
            override fun onTextChange(view: VGSTextView, isEmpty: Boolean) {
                Log.d(MainActivity::class.simpleName, "textIsEmpty: $isEmpty")
            }
        })
        tvCardNumber.addOnCopyTextListener(object : VGSTextView.OnTextCopyListener {

            override fun onTextCopied(view: VGSTextView, format: VGSTextView.CopyTextFormat) {
                Toast.makeText(
                    requireContext().applicationContext,
                    "Number text copied! format: $format",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        tvCardNumber.setOnClickListener {
            tvCardNumber.copyToClipboard(VGSTextView.CopyTextFormat.RAW)
        }
        mbRequest.setOnClickListener {
            pbReveal.setVisible(true)
            show.requestAsync(
                VGSRequest.Builder("post", VGSHttpMethod.POST).body(
                    mapOf(
                        "payment_card_number" to cardNumberAlias,
                        "payment_card_expiration_date" to expirationDateAlias
                    )
                ).build()
            )
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

    private fun parseAlias(data: JSONObject, path: String): String {
        return if (data.has("json") && data.getJSONObject("json").has(path)) {
            data.getJSONObject("json").getString(path)
        } else {
            ""
        }
    }
}