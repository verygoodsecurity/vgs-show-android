package com.verygoodsecurity.demoshow.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.verygoodsecurity.demoshow.R
import com.verygoodsecurity.demoshow.ui.MainActivity
import com.verygoodsecurity.demoshow.ui.activity.CollectAndShowActivity
import com.verygoodsecurity.demoshow.ui.activity.OnCardAliasChangeListener
import com.verygoodsecurity.demoshow.utils.extension.setVisible
import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.core.listener.VGSOnResponseListener
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse
import com.verygoodsecurity.vgsshow.widget.VGSTextView

class ShowFragment : Fragment(R.layout.show_layout), OnCardAliasChangeListener {

    private val show: VGSShow by lazy {
        VGSShow.Builder(requireContext(), MainActivity.TENANT_ID)
            .build()
    }

    private var cardNumberAlias: String = ""
    private var expirationDateAlias: String = ""

    private lateinit var pbReveal: ProgressBar
    private lateinit var tvCardNumber: VGSTextView
    private lateinit var tvCardExpiration: VGSTextView
    private lateinit var mbRequest: MaterialButton
    private lateinit var mbSetSecureText: MaterialButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setupShow()
    }

    private fun initViews(view: View) {
        pbReveal = view.findViewById(R.id.pbReveal)
        tvCardNumber = view.findViewById(R.id.tvCardNumber)
        tvCardExpiration = view.findViewById(R.id.tvCardExpiration)
        mbRequest = view.findViewById(R.id.mbRequest)
        mbSetSecureText = view.findViewById(R.id.mbSetSecureText)
    }

    override fun onAliasChange(cardNumberAlias: String, expirationDateAlias: String) {
        this.cardNumberAlias = cardNumberAlias
        this.expirationDateAlias = expirationDateAlias
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
}