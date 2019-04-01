package com.fanchen.mbase.http.interceptor

import okhttp3.Request

/**
 * CxcInterceptor
 * Created by fanchen on 2018/11/20.
 */
class CxcInterceptor(private val origin : String ,logger: Logger) : HttpLogInterceptor(logger) {

    companion object {
        val DEFAULT = CxcInterceptor("", HttpLogInterceptor.DEFAULT)
    }

    override fun warpRequest(request: Request): Request {
        return request.newBuilder().addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Accept-Encoding", "gzip, deflate").addHeader("Connection", "keep-alive")
                .addHeader("Accept", "*/*").addHeader("User-Agent", "")
                .addHeader("Origin", origin).build()
    }
}