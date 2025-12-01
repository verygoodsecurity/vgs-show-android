package com.verygoodsecurity.vgsshow.core.helper

import android.util.Base64
import androidx.annotation.MainThread
import com.verygoodsecurity.vgsshow.core.network.model.data.response.ResponseData
import com.verygoodsecurity.vgsshow.util.extension.logStartViewsUpdate
import com.verygoodsecurity.vgsshow.util.extension.logWaring
import com.verygoodsecurity.vgsshow.widget.VGSImageView
import com.verygoodsecurity.vgsshow.widget.VGSPDFView
import com.verygoodsecurity.vgsshow.widget.VGSTextView
import com.verygoodsecurity.vgsshow.widget.core.VGSView

/**
 * A helper class for managing a collection of [VGSView]s.
 * @suppress Not for public use.
 */
internal class ViewsStore {

    private val views: MutableSet<VGSView<*>> = mutableSetOf()

    /**
     * Adds a [VGSView] to the store.
     *
     * @param view The view to add.
     * @return `true` if the view was added, `false` otherwise.
     */
    fun add(view: VGSView<*>): Boolean = views.add(view)

    /**
     * Removes a [VGSView] from the store.
     *
     * @param view The view to remove.
     * @return `true` if the view was removed, `false` otherwise.
     */
    fun remove(view: VGSView<*>): Boolean = views.remove(view)

    /**
     * Removes all [VGSView]s from the store.
     */
    fun clear() {
        views.clear()
    }

    /**
     * Updates all [VGSView]s in the store with the provided data.
     * This method should be called on the main thread.
     *
     * @param data The response data to update the views with.
     */
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
            if (revealedData == null || !update(view, revealedData)) {
                unrevealedContentPaths.add(view.getContentPath())
            }
        }
        if (unrevealedContentPaths.isNotEmpty()) {
            logWaring("Cannot reveal data for contentPaths: $unrevealedContentPaths")
        }
    }

    /**
     * Returns `true` if the store is empty, `false` otherwise.
     */
    fun isEmpty() = views.isEmpty()

    /**
     * Returns the set of [VGSView]s in the store.
     */
    fun getViews() = views

    private fun update(view: VGSView<*>, data: String): Boolean {
        return try {
            when (view) {
                is VGSTextView -> view.setText(data)
                is VGSPDFView -> view.render(Base64.decode(data, Base64.NO_WRAP))
                is VGSImageView -> view.setImageByteArray(Base64.decode(data, Base64.NO_WRAP))
            }
            true
        } catch (e: IllegalArgumentException) {
            logWaring("Revealed data cannot be decoded.")
            false
        }
    }
}