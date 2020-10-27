package com.verygoodsecurity.vgsshow.util.connection

interface IConnectionHelper {

    /**
     * Implementation of this function is deprecated, but current new way of retrieving
     * connectivity availability is not flexible enough to be used in our case
     *
     * @return - true if any(wifi, 3g etc.)internet connection available,
     * otherwise returns false
     */
    fun isConnectionAvailable(): Boolean
}