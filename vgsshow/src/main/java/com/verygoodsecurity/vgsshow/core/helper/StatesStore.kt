package com.verygoodsecurity.vgsshow.core.helper

import android.util.Base64
import androidx.compose.runtime.MutableState
import com.verygoodsecurity.vgsshow.core.network.model.data.response.ResponseData
import com.verygoodsecurity.vgsshow.util.extension.logStartUIUpdate
import com.verygoodsecurity.vgsshow.util.extension.logWaring
import com.verygoodsecurity.vgsshow.widget.compose.state.VgsImageState
import com.verygoodsecurity.vgsshow.widget.compose.state.VgsPdfState
import com.verygoodsecurity.vgsshow.widget.compose.state.VgsTextState
import com.verygoodsecurity.vgsshow.widget.compose.state.core.BaseState

/**
 * A helper class for managing a collection of [BaseState]s.
 * @suppress Not for public use.
 */
internal class StatesStore : Store<MutableState<BaseState>> {

    private val states: MutableSet<MutableState<BaseState>> = mutableSetOf()

    /**
     * Adds a [BaseState] to the store.
     *
     * @param target The view to add.
     * @return `true` if the view was added, `false` otherwise.
     */
    override fun add(target: MutableState<BaseState>): Boolean = states.add(target)

    /**
     * Removes a [BaseState] from the store.
     *
     * @param target The view to remove.
     * @return `true` if the view was removed, `false` otherwise.
     */
    override fun remove(target: MutableState<BaseState>): Boolean = states.remove(target)

    override fun update(data: ResponseData?) {
        // Whether this is worth warning about is CompositeStore's call — it also has a
        // ViewsStore, so this store alone being empty doesn't mean nothing is subscribed.
        if (states.isEmpty()) {
            return
        }
        logStartUIUpdate(states.map { it.value.contentPath })
        val unrevealedContentPaths = mutableListOf<String>()
        for (state in states.toList()) {
            if (state.value.ignore) {
                continue
            }
            val contentPath = state.value.contentPath
            val revealedData = data?.getValue(contentPath)
            if (revealedData == null || !update(state, revealedData)) {
                unrevealedContentPaths.add(contentPath)
            }
        }
        if (unrevealedContentPaths.isNotEmpty()) {
            logWaring("Cannot reveal data for contentPaths: $unrevealedContentPaths")
        }
    }

    /**
     * Removes all [BaseState]s from the store.
     */
    override fun clear() {
        states.clear()
    }

    /**
     * Returns `true` if the store is empty, `false` otherwise.
     */
    override fun isEmpty(): Boolean = states.isEmpty()

    /**
     * Returns the set of [BaseState]s in the store.
     */
    override fun getAll(): Set<MutableState<BaseState>> = states

    private fun update(state: MutableState<BaseState>, data: String): Boolean {
        var revealedImage: VgsImageState? = null
        try {
            when (val s = state.value) {
                is VgsTextState -> {
                    state.value = s.copy(text = data)
                }

                is VgsImageState -> {
                    val decoded = Base64.decode(data, Base64.NO_WRAP)
                    val revealed = s.copy(image = decoded)
                    state.value = revealed
                    revealedImage = revealed
                }

                is VgsPdfState -> {
                    val decoded = Base64.decode(data, Base64.NO_WRAP)
                    state.value = s.copy(document = decoded)
                }
            }
        } catch (_: IllegalArgumentException) {
            logWaring("Revealed data cannot be decoded.")
            return false
        }
        revealedImage?.notifyLoadResult()
        return true
    }
}