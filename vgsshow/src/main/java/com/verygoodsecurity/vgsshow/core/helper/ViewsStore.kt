package com.verygoodsecurity.vgsshow.core.helper

import androidx.annotation.MainThread
import com.verygoodsecurity.vgsshow.core.network.model.data.response.ResponseData
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
    fun update(data: ResponseData?) {
        for (view in views) {
            if (view.ignoreField) {
                continue
            }
            when (view) {
                is VGSTextView -> data?.getValue(view.getContentPath())?.let { view.setText(it) }
                else -> throw IllegalArgumentException("Not implemented yet!")
            }
        }
    }

    fun isEmpty() = views.isEmpty()

    fun getViews() = views
}