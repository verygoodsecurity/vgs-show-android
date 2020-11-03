package com.verygoodsecurity.vgsshow.core.helper

import androidx.annotation.MainThread
import androidx.annotation.VisibleForTesting
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse
import com.verygoodsecurity.vgsshow.core.network.model.data.IResponseData
import com.verygoodsecurity.vgsshow.widget.VGSTextView

internal class ViewsStore {

    private val views: MutableSet<VGSTextView> = mutableSetOf()

    fun add(view: VGSTextView) {
        views.add(view)
    }

    fun remove(view: VGSTextView) {
        views.remove(view)
    }

    fun clear() {
        views.clear()
    }

    @MainThread
    fun update(response: VGSResponse) {
        if (response is VGSResponse.Success<*>) {
            updateViews(response.data)
        }
    }

    //region Helper methods for testing
    @VisibleForTesting
    fun getViews() = views
    //endregion

    private fun updateViews(data: IResponseData) {
        views.forEach {
            // TODO: Check if ignore view(ex. view.isIgnore)
            it.setText(data.getValue(it.getFieldName()))
        }
    }
}