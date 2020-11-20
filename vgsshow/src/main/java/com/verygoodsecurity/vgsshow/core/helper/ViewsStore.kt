package com.verygoodsecurity.vgsshow.core.helper

import androidx.annotation.MainThread
import com.verygoodsecurity.vgsshow.core.network.model.data.IResponseData
import com.verygoodsecurity.vgsshow.widget.VGSTextView
import com.verygoodsecurity.vgsshow.widget.core.VGSView

internal class ViewsStore {

    private val views: MutableSet<VGSView<*>> = mutableSetOf()

    fun add(view: VGSView<*>): Boolean = views.add(view)

    fun remove(view: VGSView<*>): Boolean = views.remove(view)

    fun clear() {
        views.clear()
    }

    @MainThread
    fun update(data: IResponseData?) {
        views.forEach {
            when (it) {
                is VGSTextView -> it.setText(data?.getValue(it.getFieldName()))
                else -> throw IllegalArgumentException("Not implemented yet!")
            }
        }
    }

    fun isEmpty() = views.isEmpty()

    fun getViews() = views
}