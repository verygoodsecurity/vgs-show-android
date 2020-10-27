package com.verygoodsecurity.vgsshow.core.network.client

private const val HTTP_GET = "GET"
private const val HTTP_POST = "POST"

enum class HttpMethod constructor(val value: String){

    GET(HTTP_GET),
    POST(HTTP_POST)
}
