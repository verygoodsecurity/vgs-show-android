package com.verygoodsecurity.demoshow.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.verygoodsecurity.demoshow.R
import com.verygoodsecurity.demoshow.ui.CollectResponse
import com.verygoodsecurity.demoshow.ui.CollectSuccessResponse
import com.verygoodsecurity.demoshow.ui.MainActivity
import com.verygoodsecurity.demoshow.ui.activity.OnCardAliasChangeListener
import com.verygoodsecurity.demoshow.utils.extension.setVisible
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import kotlinx.android.synthetic.main.collect_layout.*
import org.json.JSONObject

class CollectFragment : Fragment(R.layout.collect_layout) {

    private val collect: VGSCollect by lazy {
        VGSCollect(requireContext(), MainActivity.TENANT_ID, MainActivity.ENVIRONMENT)
    }

    private var aliasChangeListener: OnCardAliasChangeListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (activity is OnCardAliasChangeListener) {
            aliasChangeListener = activity as OnCardAliasChangeListener
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCollect()
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
                        val cardNumberAlias = parseAlias(this, "cardNumber")
                        val expirationDateAlias = parseAlias(this, "expDate")
                        tvCardNumberAlias?.text = cardNumberAlias
                        tvExpDateAlias?.text = expirationDateAlias
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