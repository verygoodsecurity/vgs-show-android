package com.verygoodsecurity.vgsshow.core.helper

import androidx.annotation.MainThread
import com.verygoodsecurity.vgsshow.core.network.model.data.response.ResponseData
import com.verygoodsecurity.vgsshow.util.extension.logDebug
import com.verygoodsecurity.vgsshow.util.extension.logWaring
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
        if (views.isEmpty()) {
            logWaring("No subscribed views to reveal data.")
            return
        }
        logStartViewsUpdate()
        val unrevealedContentPaths = mutableListOf<String>()
        for (view in views) {
            if (view.ignoreField) {
                continue
            }
            val revealedData = data?.getValue(view.getContentPath())
            if (revealedData == null) {
                unrevealedContentPaths.add(view.getContentPath())
                continue
            } else {
                when (view) {
                    is VGSTextView -> view.setText(revealedData)
                    else -> throw IllegalArgumentException("Not implemented yet!")
                }
            }
        }
        if (unrevealedContentPaths.isNotEmpty()) {
            logWaring("Cannot reveal data for contentPaths: $unrevealedContentPaths")
        }
    }

    fun isEmpty() = views.isEmpty()

    fun getViews() = views

    private fun logStartViewsUpdate() {
        val contentPaths = views.map {
            with(it.getContentPath()) {
                if (isNullOrEmpty()) "CONTENT PATH NOT SET OR EMPTY" else this
            }
        }
        logDebug("Start decoding revealed data for contentPaths: $contentPaths")
        if (views.any { it.getContentPath().isEmpty() }) {
            logWaring("Some subscribed views seems to have empty content path. Verify `contentPath` property is set for each view.")
        }
    }
}