package com.fanchen.mbase.http.converter

import com.fanchen.mbase.util.LogUtil
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okio.Buffer
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.IOException
import java.io.OutputStreamWriter
import java.io.StringReader
import java.lang.reflect.Type
import java.nio.charset.Charset

/**
 * GsonConverterFactory
 * copy from com.squareup.retrofit2:converter-gson:2.1.0
 * Created by fanchen on 2018/11/22.
 */
internal class GsonConverterFactory private constructor(private val gson: Gson) : Converter.Factory() {

    override fun responseBodyConverter(type: Type?, annotations: Array<Annotation>?, retrofit: Retrofit?): Converter<ResponseBody, *> {
        val adapter = gson.getAdapter(TypeToken.get(type))
        return GsonResponseBodyConverter(gson, adapter)
    }

    override fun requestBodyConverter(type: Type?, parameterAnnotations: Array<Annotation>?, methodAnnotations: Array<Annotation>?, retrofit: Retrofit?): Converter<*, RequestBody> {
        val adapter = gson.getAdapter(TypeToken.get(type))
        return GsonRequestBodyConverter(gson, adapter)
    }

    companion object {

        @JvmOverloads
        fun create(gson: Gson = Gson()): GsonConverterFactory {
            return GsonConverterFactory(gson)
        }

    }

    private class GsonRequestBodyConverter<T>(private val gson: Gson, private val adapter: TypeAdapter<T>) : Converter<T, RequestBody> {

        @Throws(IOException::class)
        override fun convert(value: T): RequestBody {
            val buffer = Buffer()
            val writer = OutputStreamWriter(buffer.outputStream(), UTF_8)
            val jsonWriter = gson.newJsonWriter(writer)
            adapter.write(jsonWriter, value)
            jsonWriter.close()
            return RequestBody.create(MEDIA_TYPE, buffer.readByteString())
        }

        companion object {
            private val MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8")
            private val UTF_8 = Charset.forName("UTF-8")
        }

    }

    private class GsonResponseBodyConverter<T>(private val gson: Gson, private val adapter: TypeAdapter<T>) : Converter<ResponseBody, T> {

        @Throws(IOException::class)
        override fun convert(value: ResponseBody): T {
            val string = value.string()
            LogUtil.e("convert","" + string)
            val jsonReader = gson.newJsonReader(StringReader(string))
            try {
                return adapter.read(jsonReader)
            } catch (e: Exception) {
                throw IOException(e)
            } finally {
                value.close()
            }
        }
    }
}