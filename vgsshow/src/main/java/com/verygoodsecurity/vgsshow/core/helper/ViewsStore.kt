package com.verygoodsecurity.vgsshow.core.helper

import android.util.Base64
import androidx.annotation.MainThread
import com.verygoodsecurity.vgsshow.core.network.model.data.response.ResponseData
import com.verygoodsecurity.vgsshow.pdf.VGSPDFView
import com.verygoodsecurity.vgsshow.util.extension.logStartViewsUpdate
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
        logStartViewsUpdate(views)
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
                    is VGSPDFView -> view.render(Base64.decode(revealedData, Base64.NO_WRAP))
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
}