package com.verygoodsecurity.demoshow.ui.activity.apply_response_demo.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.verygoodsecurity.demoshow.R
import com.verygoodsecurity.demoshow.ui.MainActivity
import com.verygoodsecurity.demoshow.ui.activity.apply_response_demo.core.StateHandler
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.widget.VGSEditText
import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.core.VGSEnvironment
import com.verygoodsecurity.vgsshow.core.listener.VGSOnResponseListener
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod
import org.json.JSONObject
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse as VGSCollectResponse
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse as VGSShowResponse

/**
 * First where we will create alias and make reveal request and pass response to another fragment
 */
class FirsFragment : Fragment(R.layout.fragment_apply_response_demo_first), VGSOnResponseListener,
    VgsCollectResponseListener {

    companion object {

        private const val PATH = "/post"
    }

    // Each fragment uses it's own VGSShow instance
    private val show: VGSShow by lazy {
        VGSShow(requireContext(), MainActivity.TENANT_ID, VGSEnvironment.Sandbox()).apply {
            addOnResponseListener(this@FirsFragment)
        }
    }

    private val collect: VGSCollect by lazy {
        VGSCollect(requireContext(), MainActivity.TENANT_ID, MainActivity.ENVIRONMENT).apply {
            addOnResponseListeners(this@FirsFragment)
        }
    }

    private var stateHandler: StateHandler? = null

    private lateinit var actionButton: MaterialButton

    private lateinit var collectView: VGSEditText

    override fun onAttach(context: Context) {
        super.onAttach(context)
        stateHandler = context as? StateHandler
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        actionButton = view.findViewById(R.id.actionButton)
        actionButton.setOnClickListener {
            collectView.hideKeyboard()
            collect()
        }

        // Init VGSEditText
        collectView = view.findViewById(R.id.editText)
        collectView.addOnTextChangeListener(object : InputFieldView.OnTextChangedListener {

            override fun onTextChange(view: InputFieldView, isEmpty: Boolean) {
                actionButton.visibility = if (isEmpty) View.GONE else View.VISIBLE
            }
        })
        collect.bindView(collectView)
    }

    override fun onResponse(response: VGSCollectResponse?) {
        try {
            when (response) {
                is VGSCollectResponse.SuccessResponse -> reveal(readEncryptedValue(response.body))
                is VGSCollectResponse.ErrorResponse -> setError(response.toString())
                else -> {}
            }
        } catch (e: Exception) {
            setError("Can't read encrypted value, e = $e")
        }
    }

    // Disable loading and save reveal response to apply it on another screen
    override fun onResponse(response: VGSShowResponse) {
        stateHandler?.setLoading(false)
        stateHandler?.response = response
    }

    // Collect sensitive data
    private fun collect() {
        stateHandler?.setLoading(true)
        collect.asyncSubmit(PATH, HTTPMethod.POST)
    }

    private fun reveal(alias: String) {
        show.requestAsync(
            PATH,
            VGSHttpMethod.POST,
            mapOf("raw_sensitive_data" to alias)
        )
    }

    @Throws(Exception::class)
    private fun readEncryptedValue(body: String?): String {
        return JSONObject(body!!).getJSONObject("json").getString(collectView.getFieldName()!!)
    }

    private fun setError(message: String) {
        stateHandler?.setLoading(false)
        stateHandler?.setError(message)
    }
}