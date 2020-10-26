package com.verygoodsecurity.vgsshow.core.network

import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.core.network.client.HttpUrlClient
import com.verygoodsecurity.vgsshow.core.network.client.IHttpClient
import com.verygoodsecurity.vgsshow.core.network.client.OkHttpClient
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse
import com.verygoodsecurity.vgsshow.exception.UrlNotValidException
import com.verygoodsecurity.vgsshow.util.extension.*
import okio.IOException

internal class NetworkManager(vaultId: String, environment: String) : INetworkManager {

    private val client: IHttpClient by lazy {
        with(buildBaseUrl(vaultId, environment) ?: DEFAULT_BASE_URL) {
            if (isLollipopOrGreater) OkHttpClient(this) else HttpUrlClient(this)
        }
    }

    override fun execute(request: VGSRequest): VGSResponse {
        return try {
            val response = client.call(request.toHttpRequest())
            if (response.isSuccessful) {
                VGSResponse.Success(response.code, mapOf(), response.responseBody)
            } else {
                VGSResponse.Error(response.code, response.message)
            }
        } catch (e: UrlNotValidException) {
            VGSResponse.Error(-1, e.message)
        }catch (e: IOException) {
            VGSResponse.Error(-1, e.message)
        } catch (e: Exception) {
            VGSResponse.Error(-1, e.message)
        }
    }

    private fun buildBaseUrl(vaultId: String, environment: String): String? = when {
        !vaultId.isValidTenantId() -> {
            logDebug("Vault id is not valid", VGSShow::class.simpleName)
            null
        }
        !environment.isValidEnvironment() -> {
            logDebug("Environment is not valid", VGSShow::class.simpleName)
            null
        }
        else -> StringBuilder(SCHEME)
            .append(vaultId).append(DIVIDER)
            .append(environment).append(DIVIDER)
            .append(DOMEN)
            .toString()
    }

    companion object {

        private const val DEFAULT_BASE_URL = ""

        private const val SCHEME = "https://"
        private const val DOMEN = "verygoodproxy.com"
        private const val DIVIDER = "."
    }
}