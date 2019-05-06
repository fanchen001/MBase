package com.fanchen.mbase.http.interceptor

import com.fanchen.mbase.util.JsonFormatUtil
import okhttp3.*
import okhttp3.internal.http.HttpHeaders
import okhttp3.internal.platform.Platform
import okio.Buffer
import java.io.EOFException
import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.UnsupportedCharsetException
import java.util.concurrent.TimeUnit
import okhttp3.internal.platform.Platform.WARN
import java.io.File
import java.io.FileOutputStream

/**
 * HttpLoggingInterceptor
 * Created by fanchen on 2018/11/20.
 */
open class HttpLogInterceptor(private val logger: Logger = DEFAULT) : Interceptor {

    @Volatile private var level = Level.BODY

    companion object {

        private val UTF8 = Charset.forName("UTF-8")

        val DEFAULT: Logger = Logger.DefaultLogger()

        internal fun isPlaintext(buffer: Buffer): Boolean {
            try {
                val prefix = Buffer()
                val byteCount = if (buffer.size() < 64) buffer.size() else 64
                buffer.copyTo(prefix, 0, byteCount)
                for (i in 0..15) {
                    if (prefix.exhausted()) break
                    val codePoint = prefix.readUtf8CodePoint()
                    if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) return false
                }
                return true
            } catch (e: EOFException) {
                return false // Truncated UTF-8 sequence.
            }
        }
    }

    fun setLevel(level: Level?): HttpLogInterceptor {
        if (level == null) throw NullPointerException("level == null. Use Level.NONE instead.")
        this.level = level
        return this
    }

    fun getLevel(): Level {
        return level
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val level = this.level
        val request = warpRequest(chain.request())
        if (level == Level.NONE) {
            return chain.proceed(request)
        }
        val logBody = level == Level.BODY
        val logHeaders = logBody || level == Level.HEADERS
        val requestBody = request.body()
        val hasRequestBody = requestBody != null
        val connection = chain.connection()
        val protocol = if (connection != null) connection.protocol() else Protocol.HTTP_1_1
        var requestStartMessage = "--> " + request.method() + ' ' + request.url() + ' ' + protocol
        if (!logHeaders && hasRequestBody && requestBody != null) {
            requestStartMessage += " (" + requestBody.contentLength() + "-byte body)"
        }
        logger.log(requestStartMessage)
        if (logHeaders) {
            if (hasRequestBody && requestBody != null) {
                if (requestBody.contentType() != null) {
                    logger.log("Content-Type: " + requestBody.contentType())
                }
                if (requestBody.contentLength() != (-1).toLong()) {
                    logger.log("Content-Length: " + requestBody.contentLength())
                }
            }
            val headers = request.headers()
            var i = 0
            val count = headers.size()
            while (i < count) {
                val name = headers.name(i)
                if (!"Content-Type".equals(name, ignoreCase = true) && !"Content-Length".equals(name, ignoreCase = true)) {
                    logger.log(name + ": " + headers.value(i))
                }
                i++
            }
            if (!logBody || !hasRequestBody) {
                logger.log("--> END " + request.method())
            } else if (bodyEncoded(request.headers())) {
                logger.log("--> END " + request.method() + " (encoded body omitted)")
            } else {
                logger.log("")
                if (requestBody != null && requestBody.contentLength() < 10 * 1024) {
                    val buffer = Buffer()
                    requestBody.writeTo(buffer)
                    var charset = UTF8
                    val contentType = requestBody.contentType()
                    if (contentType != null) {
                        charset = contentType.charset(UTF8)
                    }
                    if (isPlaintext(buffer)) {
                        logger.log(buffer.readString(charset))
                        logger.log("")
                        logger.log("--> END " + request.method() + " (" + requestBody.contentLength() + "-byte body)")
                    } else {
                        logger.log("--> END " + request.method() + " (binary " + requestBody.contentLength() + "-byte body omitted)")
                    }
                } else if (requestBody != null) {
                    logger.log("data length to long  > ${10 * 1024} ")
                    logger.log("")
                    logger.log("--> END " + request.method() + " (binary " + requestBody.contentLength() + "-byte body omitted)")
                }
            }
        }
        val startNs = System.nanoTime()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            logger.log("<-- HTTP FAILED: $e")
            throw e
        }
        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
        val responseBody = response.body()
        val contentLength = responseBody?.contentLength()
        val bodySize = if (contentLength != (-1).toLong())
            (contentLength).toString() + "-byte"
        else
            "unknown-length"
        logger.log(("<-- " + response.code() + ' ' + response.message() + ' ' + response.request().url() + " (" + tookMs + "ms" + (if (!logHeaders)
            ", $bodySize body"
        else
            "") + ')'))
        if (logHeaders) {
            val headers = response.headers()
            var i = 0
            val count = headers.size()
            while (i < count) {
                logger.log(headers.name(i) + ": " + headers.value(i))
                i++
            }
            if (!logBody || !HttpHeaders.hasBody(response)) {
                logger.log("<-- END HTTP")
            } else if (bodyEncoded(response.headers())) {
                logger.log("<-- END HTTP (encoded body omitted)")
            } else {
                val source = responseBody?.source()
                source?.request(java.lang.Long.MAX_VALUE) // Buffer the entire body.
                val buffer = source?.buffer()
                var charset = UTF8
                val contentType = responseBody?.contentType()
                if (contentType != null) {
                    try {
                        charset = contentType.charset(UTF8)
                    } catch (e: UnsupportedCharsetException) {
                        logger.log("")
                        logger.log("Couldn't decode the response body; charset is likely malformed.")
                        logger.log("<-- END HTTP")
                        return response
                    }
                }
                if (buffer != null && !isPlaintext(buffer)) {
                    logger.log("")
                    logger.log("<-- END HTTP (binary " + buffer.size() + "-byte body omitted)")
                    return response
                }
                if (contentLength != 0L) {
                    logger.log("")
                    if (contentLength != null && contentLength < 10 * 1024) {
                        logger.log(buffer?.clone()?.readString(charset) ?: "")
                    } else {
                        logger.log("data length to long  > ${10 * 1024} ")
                    }
                    logger.log("")
                }
                logger.log("<-- END HTTP (" + buffer?.size() + "-byte body)")
            }
        }
        return response
    }

    open fun warpRequest(request: Request): Request {
        return request
    }

    private fun bodyEncoded(headers: Headers): Boolean {
        val contentEncoding = headers.get("Content-Encoding")
        return contentEncoding != null && !contentEncoding.equals("identity", ignoreCase = true)
    }

    enum class Level {
        NONE,
        BASIC,
        HEADERS,
        BODY
    }

    interface Logger {
        fun log(message: String)

        /**
         * 默认日志
         * @param File
         */
        class DefaultLogger : Logger {

            override fun log(message: String) {
                Platform.get().log(WARN, message, null)
            }

        }

        /**
         * 文件日志
         * @param File
         */
        class FileLogger(private val logFile: File) : Logger {

            override fun log(message: String) {
                var fos: FileOutputStream? = null
                try {
                    fos = FileOutputStream(logFile, true)
                    if (JsonFormatUtil.isJson(message)) {
                        fos.write(JsonFormatUtil.formatJson(message).toByteArray())
                    } else {
                        fos.write(message.toByteArray())
                    }
                    fos.write("\n".toByteArray())
                    fos.flush()
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    try {
                        fos?.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

}