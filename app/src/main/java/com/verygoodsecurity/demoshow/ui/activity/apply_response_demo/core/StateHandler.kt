package com.verygoodsecurity.demoshow.ui.activity.apply_response_demo.core

import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse

interface StateHandler {

    var response: VGSResponse?

    fun setLoading(isLoading: Boolean)

    fun setError(message: String)
}