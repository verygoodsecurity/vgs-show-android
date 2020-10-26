package com.verygoodsecurity.vgsshow.core.network

import com.verygoodsecurity.vgsshow.core.network.client.HttpUrlClient
import com.verygoodsecurity.vgsshow.core.network.client.IHttpClient
import com.verygoodsecurity.vgsshow.core.network.client.OkHttpClient
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse
import com.verygoodsecurity.vgsshow.util.extension.isLollipopOrGreater
import com.verygoodsecurity.vgsshow.util.extension.toHttpRequest
import okio.IOException
import java.net.MalformedURLException

internal class NetworkManager(baseUrl: String) : INetworkManager {

    private val client: IHttpClient by lazy {
        if (isLollipopOrGreater) OkHttpClient(baseUrl) else HttpUrlClient(baseUrl)
    }

    override fun execute(request: VGSRequest): VGSResponse {
        return try {
            val response = client.call(request.toHttpRequest())
            if (response.isSuccessful) {
                VGSResponse.Success(response.code, mapOf(), response.responseBody)
            } else {
                VGSResponse.Error(response.code, response.message)
            }
        } catch (e: MalformedURLException) {
            VGSResponse.Error(-1, e.message)
        } catch (e: IOException) {
            VGSResponse.Error(-1, e.message)
        } catch (e: Exception) {
            VGSResponse.Error(-1, e.message)
        }
    }
}