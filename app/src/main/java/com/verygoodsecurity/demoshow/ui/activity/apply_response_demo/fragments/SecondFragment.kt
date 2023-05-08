package com.verygoodsecurity.demoshow.ui.activity.apply_response_demo.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.verygoodsecurity.demoshow.R
import com.verygoodsecurity.demoshow.ui.MainActivity
import com.verygoodsecurity.demoshow.ui.activity.apply_response_demo.core.StateHandler
import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.core.VGSEnvironment
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse
import com.verygoodsecurity.vgsshow.widget.VGSTextView

/**
 * Second Fragment where we handle reveal request made by [FirsFragment]
 */
class SecondFragment : Fragment(R.layout.fragment_apply_response_demo_second) {

    private val show: VGSShow by lazy {
        VGSShow(requireContext(), MainActivity.TENANT_ID, VGSEnvironment.Sandbox())
    }

    private lateinit var handler: StateHandler

    override fun onAttach(context: Context) {
        super.onAttach(context)
        handler = context as StateHandler
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Init show
        val textView = view.findViewById<VGSTextView>(R.id.textView)
        show.subscribe(textView)
        (handler.response as? VGSResponse.Success)?.let { show.applyResponse(it) }
    }
}