package com.verygoodsecurity.demoshow.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.verygoodsecurity.demoshow.R
import com.verygoodsecurity.demoshow.ui.CollectResponse
import com.verygoodsecurity.demoshow.ui.CollectSuccessResponse
import com.verygoodsecurity.demoshow.ui.MainActivity
import com.verygoodsecurity.demoshow.ui.activity.OnCardAliasChangeListener
import com.verygoodsecurity.demoshow.utils.extension.setVisible
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.widget.ExpirationDateEditText
import com.verygoodsecurity.vgscollect.widget.VGSCardNumberEditText
import org.json.JSONObject

class CollectFragment : Fragment(R.layout.collect_layout) {

    private val collect: VGSCollect by lazy {
        VGSCollect(requireContext(), MainActivity.TENANT_ID, MainActivity.ENVIRONMENT)
    }

    private var aliasChangeListener: OnCardAliasChangeListener? = null

    private lateinit var pbSubmit: ProgressBar
    private lateinit var mbSubmit: MaterialButton
    private lateinit var etCardNumber: VGSCardNumberEditText
    private lateinit var etExpDate: ExpirationDateEditText
    private lateinit var tvCardNumberAlias: TextView
    private lateinit var tvExpDateAlias: TextView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (activity is OnCardAliasChangeListener) {
            aliasChangeListener = activity as OnCardAliasChangeListener
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setupCollect()
    }

    private fun initViews(view: View) {
        pbSubmit = view.findViewById(R.id.pbSubmit)
        mbSubmit = view.findViewById(R.id.mbSubmit)
        etCardNumber = view.findViewById(R.id.etCardNumber)
        etExpDate = view.findViewById(R.id.etExpDate)
        tvCardNumberAlias = view.findViewById(R.id.tvCardNumberAlias)
        tvExpDateAlias = view.findViewById(R.id.tvExpDateAlias)
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
                        val cardNumberAlias = parseAlias(this, "cardNumber")
                        val expirationDateAlias = parseAlias(this, "expDate")
                        tvCardNumberAlias.text = cardNumberAlias
                        tvExpDateAlias.text = expirationDateAlias
                        aliasChangeListener?.onAliasChange(cardNumberAlias, expirationDateAlias)
                    }
                } catch (e: Exception) {
                    Log.e(CollectAndShowFragment::class.java.simpleName, e.message ?: "")
                }
            }
        })

        collect.bindView(etCardNumber)
        collect.bindView(etExpDate)
    }

    private fun parseAlias(data: JSONObject, path: String): String {
        return if (data.has("json") && data.getJSONObject("json").has(path)) {
            data.getJSONObject("json").getString(path)
        } else {
            ""
        }
    }
}