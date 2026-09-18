package com.verygoodsecurity.vgsshow.widget.compose.state.core

/**
 * Common state contract shared by every VGS Show Compose field state
 * (text, image, PDF).
 *
 * Instances are immutable — a headless legacy view hands each newly revealed
 * value back as a new state object, which is then applied to a `Vgs...` composable.
 *
 * @property contentPath text that's used for data transfer between field and VGS proxy.
 *   Usually, it is similar to field-name in JSON path in your inbound route filters.
 * @property isEmpty `true` when the field has no revealed data yet.
 */
abstract class VgsShowState internal constructor(
    val contentPath: String,
) {

    abstract val isEmpty: Boolean

    /**
     * Deliberately omits the revealed value — matching every `Vgs...` subclass, which never
     * exposes it as a public property either — so logging or debugging a state can't leak it.
     */
    override fun toString(): String =
        "${this::class.simpleName}(contentPath=$contentPath, isEmpty=$isEmpty)"
}
