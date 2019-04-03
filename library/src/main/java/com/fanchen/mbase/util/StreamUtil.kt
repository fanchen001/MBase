package com.fanchen.mbase.util

import android.graphics.*
import android.graphics.drawable.Drawable
import com.fanchen.mbase.warp.toJson
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import java.util.zip.GZIPInputStream
import java.util.zip.Inflater
import java.util.zip.InflaterInputStream

/**
 * 流操作相关
 * Created by fanchen on 2018/9/3.
 */
object StreamUtil {
    /**
     * 获取ByteArrayInputStream.
     *
     * @param buf the buf
     * @return the input stream
     */
    fun bytes2Stream(buf: ByteArray): InputStream {
        return ByteArrayInputStream(buf)
    }

    /**
     * 从流中读取数据到byte[]..
     *
     * @param inStream the in stream
     * @return the byte[]
     * @throws IOException Signals that an I/O exception has occurred.
     */
    fun stream2bytes(inStream: InputStream?): ByteArray? {
        return inStream?.readBytes()
    }

    fun file2Byte(file: String): ByteArray? {
        return file2Byte(File(file))
    }

    /**
     * 读取文件，返回byte
     */
    fun file2Byte(file: File?): ByteArray? {
        return file?.readBytes()
    }

    /**
     * 将bitmap转成byte
     */
    fun bitmap2Byte(bmp: Bitmap?): ByteArray? {
        var bytes: ByteArray? = null
        ByteArrayOutputStream().use {
            bmp?.compress(Bitmap.CompressFormat.JPEG, 100, it)
            bytes = it.toByteArray()
        }
        return bytes
    }

    /**
     * 将Drawable转成byte
     */
    fun drawable2Byte(drawable: Drawable): ByteArray? {
        return bitmap2Byte(ImageUtil.drawable2Bitmap(drawable))
    }


    /**
     * yuv转Byte
     *
     * @param yuvBytes
     * @param width
     * @param height
     * @return
     */
    fun yuv2Byte(yuvBytes: ByteArray, width: Int, height: Int): ByteArray? {
        var bytes: ByteArray? = null
        val yuvImage = YuvImage(yuvBytes, ImageFormat.NV21, width, height, null)
        ByteArrayOutputStream().use {
            yuvImage.compressToJpeg(Rect(0, 0, width, height), 100, it)
            bytes = it.toByteArray()
        }
        return bytes
    }

    //默认请求头
    private fun getDefHeader(): HashMap<String, String> {
        val map = HashMap<String, String>()
        map.put("User-Agent", "Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Mobile Safari/537.36")
        map.put("Accept", "*/*")
        map.put("Accept-Encoding", "gzip, deflate")
        return map
    }

    //post请求
    fun post(url: String, body: String? = null): ByteArray? {
        return url2Byte(url, "POST", body = body)
    }

    //post方式提交json
    fun postAtJson(url: String, body: Any? = null): ByteArray? {
        var map = getDefHeader()
        map.put("Content-Type", "application/json")
        return url2Byte(url, "POST", map, body?.toJson() ?: "")
    }

    fun url2Byte(url: String, method: String = "GET", header: Map<String, String>? = null, body: String? = null): ByteArray? {
        return url2Byte(URL(url), method, header, body)
    }

    fun url2Byte(httpConnect: HttpURLConnection?, method: String = "GET", header: Map<String, String>? = null, body: String? = null): ByteArray? {
        httpConnect?.let {
            httpConnect.connectTimeout = 10 * 1000  // 设置连接超时时间
            httpConnect.readTimeout = 10 * 1000  //设置从主机读取数据超时
            httpConnect.doOutput = true
            httpConnect.doInput = true
            httpConnect.instanceFollowRedirects = true;
            httpConnect.useCaches = false
            httpConnect.requestMethod = method // 设置为Post请求
        }
        for (kv in header ?: getDefHeader()) {
            httpConnect?.setRequestProperty(kv.key, kv.value)
        }
        httpConnect?.connect() // 开始连接  //Cookie:
        if (body != null) {
            httpConnect?.outputStream?.use {
                it.write(body.toByteArray())
                it.flush()
            }
        }
        val reader: InputStream?
        val inputStream = httpConnect?.inputStream
        val type = httpConnect?.getHeaderField("Content-Encoding")
        if ("gzip".equals(type?.toLowerCase())) {
            reader = GZIPInputStream(inputStream)
        } else if ("deflate".equals(type?.toLowerCase())) {
            reader = InflaterInputStream(inputStream, Inflater(true))
        } else {
            reader = inputStream
        }
        return reader?.readBytes()
    }

    //请求url,获取响应的byte数组
    fun url2Byte(url: URL, method: String = "GET", header: Map<String, String>? = null, body: String? = null): ByteArray? {
        val httpConnect = url.openConnection() as? HttpURLConnection
        return url2Byte(httpConnect, method, header, body)
    }

    //url2String
    fun url2String(url: String, set: Charset = Charsets.UTF_8): String? {
        val byte = url2Byte(url);
        val str = String(byte ?: ByteArray(0), set)
        return str
    }

    fun close(vararg stream: AutoCloseable?) {
        stream.forEach {
            try {
                it?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}