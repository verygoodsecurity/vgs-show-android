package com.verygoodsecurity.vgsshow.core.helper

import androidx.annotation.MainThread
import androidx.annotation.VisibleForTesting
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
    fun update(data: IResponseData?) {
        views.forEach {
            if (!it.isIgnored()) it.setText(data?.getValue(it.getFieldName()))
        }
    }

    fun isEmpty() = views.isEmpty()

    //region Helper methods for testing
    @VisibleForTesting
    fun getViews() = views
    //endregion
}