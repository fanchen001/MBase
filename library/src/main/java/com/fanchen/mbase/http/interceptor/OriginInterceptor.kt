package com.fanchen.mbase.http.interceptor

import okhttp3.Request

/**
 * CxcInterceptor
 * Created by fanchen on 2018/11/20.
 */
class OriginInterceptor(private val origin: String, private val acceptEncoding: String? = null, logger: Logger) : HttpLogInterceptor(logger) {

    companion object {
        val DEFAULT = OriginInterceptor("", logger = HttpLogInterceptor.DEFAULT)
    }

    override fun warpRequest(request: Request): Request {
        val newBuilder = request.newBuilder()
        if (acceptEncoding != null) newBuilder.addHeader("Accept-Encoding", acceptEncoding)
        return newBuilder.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Connection", "keep-alive")
                .addHeader("Accept", "*/*").addHeader("User-Agent", "")
                .addHeader("Origin", origin).build()
    }
}