//package com.fanchen.mbase.boxing
//
//import android.widget.ImageView
//import com.bilibili.boxing.loader.IBoxingCallback
//import com.bilibili.boxing.loader.IBoxingMediaLoader
//import android.graphics.Bitmap
//import com.bumptech.glide.request.RequestListener
//import com.bumptech.glide.Glide
//import com.bumptech.glide.request.target.Target
//import java.lang.Exception
//
///**
// * GlideLoaderImpl
// * Created by fanchen on 2018/11/19.
// */
//class GlideMediaLoaderImpl : IBoxingMediaLoader {
//
//    override fun displayThumbnail(img: ImageView, absPath: String, width: Int, height: Int) {
//        val path = "file://$absPath"
//        try {
//            Glide.with(img.context).load(path).crossFade().centerCrop().override(width, height).into(img)
//        } catch (e: IllegalArgumentException) {
//            e.printStackTrace()
//        }
//    }
//
//    override fun displayRaw(img: ImageView, absPath: String, width: Int, height: Int, callback: IBoxingCallback?) {
//        val path = "file://$absPath"
//        val request = Glide.with(img.context).load(path).asBitmap()
//        if (width > 0 && height > 0) {
//            request.override(width, height)
//        }
//        request.listener(GlideLoaderListener(img, callback)).into(img)
//    }
//
//    class GlideLoaderListener(private val img: ImageView, private val callback: IBoxingCallback?) : RequestListener<String, Bitmap> {
//
//        override fun onResourceReady(resource: Bitmap?, model: String?, target: Target<Bitmap>?, isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
//            if (resource != null && callback != null) {
//                img.setImageBitmap(resource)
//                callback.onSuccess()
//                return true
//            }
//            return false
//        }
//
//        override fun onException(e: Exception?, model: String?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
//            if (callback != null) {
//                callback.onFail(e)
//                return true
//            }
//            return false
//        }
//
//    }
//}