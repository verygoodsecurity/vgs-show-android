package com.verygoodsecurity.vgsshow.core.helper

import android.view.View
import androidx.annotation.MainThread
import androidx.annotation.VisibleForTesting
import com.verygoodsecurity.vgsshow.core.network.model.data.IResponseData
import com.verygoodsecurity.vgsshow.widget.textview.VGSTextView
import com.verygoodsecurity.vgsshow.widget.VGSView

internal class ViewsStore {

    private val views: MutableSet<VGSView<View>> = mutableSetOf()

    fun add(view: VGSView<View>) {
        views.add(view)
    }

    fun remove(view: VGSView<View>) {
        views.remove(view)
    }

    fun clear() {
        views.clear()
    }

    @MainThread
    fun update(data: IResponseData?) {
        views.forEach {
            when (it) {
                is VGSTextView -> it.setText(data?.getValue(it.getFieldName()))
                else -> TODO("Implement other types")
            }
        }
    }

    fun isEmpty() = views.isEmpty()

    //region Helper methods for testing
    @VisibleForTesting
    fun getViews() = views
    //endregion
}