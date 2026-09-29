package com.verygoodsecurity.vgsshow.core.helper

import androidx.annotation.MainThread
import com.verygoodsecurity.vgsshow.core.network.model.data.response.ResponseData

internal interface Store<T> {

    fun add(target: T): Boolean

    fun remove(target: T): Boolean

    @MainThread
    fun update(data: ResponseData?)

    fun clear()

    fun isEmpty(): Boolean

    fun getAll(): Set<T>
}