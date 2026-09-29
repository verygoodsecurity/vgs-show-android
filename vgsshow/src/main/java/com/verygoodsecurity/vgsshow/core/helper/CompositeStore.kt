package com.verygoodsecurity.vgsshow.core.helper

import androidx.annotation.MainThread
import androidx.compose.runtime.MutableState
import com.verygoodsecurity.sdk.analytics.model.VGSAnalyticsCopyFormat
import com.verygoodsecurity.vgsshow.core.network.model.data.response.ResponseData
import com.verygoodsecurity.vgsshow.util.extension.toAnalyticTag
import com.verygoodsecurity.vgsshow.util.extension.toAnalyticsFormat
import com.verygoodsecurity.vgsshow.widget.VGSPDFView
import com.verygoodsecurity.vgsshow.widget.VGSTextView
import com.verygoodsecurity.vgsshow.widget.compose.state.VgsTextState
import com.verygoodsecurity.vgsshow.widget.compose.state.core.BaseState
import com.verygoodsecurity.vgsshow.widget.core.VGSView

internal class CompositeStore(
    val onCopy: (fieldType: String, contentPath: String, format: VGSAnalyticsCopyFormat) -> Unit,
    val onSecureTextRangeSet: (fieldType: String, contentPath: String) -> Unit,
    val onRenderComplete: (fieldType: String, contentPath: String) -> Unit,
    val onRenderError: (fieldType: String, contentPath: String) -> Unit,
    val onDocumentShare: (contentPath: String) -> Unit,
) {

    internal val viewsStore = ViewsStore()

    internal val widgetsStore = StatesStore()

    private val onTextCopyListener = object : VGSTextView.OnTextCopyListener {

        override fun onTextCopied(view: VGSTextView, format: VGSTextView.CopyTextFormat) {
            onCopy(
                view.getFieldType().toAnalyticTag(),
                view.getContentPath(),
                format.toAnalyticsFormat()
            )
        }
    }

    val onSecureTextRangeListener = object : VGSTextView.OnSetSecureTextRangeSetListener {

        override fun onSecureTextRangeSet(view: VGSTextView) {
            onSecureTextRangeSet(
                view.getFieldType().toAnalyticTag(),
                view.getContentPath()
            )
        }
    }

    private val onRenderStateChangeListener = object : VGSPDFView.OnRenderStateChangeListener {

        override fun onStart(view: VGSPDFView, pages: Int) {}

        override fun onComplete(view: VGSPDFView, pages: Int) {
            onRenderComplete(
                view.getFieldType().toAnalyticTag(),
                view.getContentPath()
            )
        }

        override fun onError(view: VGSPDFView, t: Throwable) {
            onRenderError(
                view.getFieldType().toAnalyticTag(),
                view.getContentPath()
            )
        }
    }

    private val onShareDocumentListener = object : VGSPDFView.OnShareDocumentListener {

        override fun onShare(view: VGSPDFView) {
            onDocumentShare(view.getContentPath())
        }
    }

    fun add(target: VGSView<*>): Boolean {
        return viewsStore.add(target).also { isAdded ->
            if (!isAdded) return@also
            when (target) {
                is VGSTextView -> {
                    target.addOnCopyTextListener(onTextCopyListener)
                    target.setOnSecureTextRangeSetListener(onSecureTextRangeListener)
                    target.onViewSubscribed()
                }

                is VGSPDFView -> {
                    target.addRenderingStateChangedListener(onRenderStateChangeListener)
                    target.onShareDocumentListener = onShareDocumentListener
                }
            }
        }
    }

    fun add(state: MutableState<BaseState>): Boolean {
        return widgetsStore.add(state).also { isAdded ->
            if (!isAdded) return@also
            val value = state.value
            if (value is VgsTextState && value.isSecure) {
                onSecureTextRangeSet(value.fieldType.toAnalyticTag(), value.contentPath)
            }
        }
    }

    fun remove(target: VGSView<*>): Boolean {
        return viewsStore.remove(target).also { isRemoved ->
            if (!isRemoved) return@also
            when (target) {
                is VGSTextView -> {
                    target.removeOnCopyTextListener(onTextCopyListener)
                    target.setOnSecureTextRangeSetListener(null)
                }

                is VGSPDFView -> {
                    target.removeRenderingStateChangedListener(onRenderStateChangeListener)
                    target.onShareDocumentListener = null
                }
            }
        }
    }

    fun remove(target: MutableState<BaseState>): Boolean = widgetsStore.remove(target)

    @MainThread
    fun update(data: ResponseData?) {
        viewsStore.update(data)
        widgetsStore.update(data)
    }


    fun hasText(): Boolean {
        return viewsStore.getAll().any { it is VGSTextView } || widgetsStore.getAll()
            .any { it.value is VgsTextState }
    }

    fun hasPdf(): Boolean {
        return viewsStore.getAll()
            .any { it is VGSPDFView } // TODO: Implement after composable pdf widget added
    }

    fun clear() {
        viewsStore.getAll().forEach {
            remove(it)
        }
        widgetsStore.clear()
    }
}
