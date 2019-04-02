//package com.fanchen.mbase.boxing
//
//import android.content.Context
//import android.content.Intent
//import android.net.Uri
//import android.support.v4.app.Fragment
//import com.bilibili.boxing.loader.IBoxingCrop
//import com.bilibili.boxing.model.config.BoxingCropOption
//import com.yalantis.ucrop.UCrop
//import android.graphics.Bitmap
//
//
///**
// * UcropImpl
// * Created by fanchen on 2018/11/19.
// */
//class UcropImpl : IBoxingCrop {
//
//    override fun onStartCrop(context: Context, fragment: Fragment, cropConfig: BoxingCropOption, path: String, requestCode: Int) {
//        val uri = Uri.Builder().scheme("file").appendPath(path).build()
//        val crop = UCrop.Options()
//        crop.setCompressionFormat(Bitmap.CompressFormat.PNG)
//        crop.withMaxResultSize(cropConfig.maxWidth, cropConfig.maxHeight)
//        crop.withAspectRatio(cropConfig.aspectRatioX, cropConfig.aspectRatioY)
//        UCrop.of(uri, cropConfig.destination).withOptions(crop).start(context, fragment, requestCode)
//    }
//
//    override fun onCropFinish(resultCode: Int, data: Intent?): Uri? {
//        if (data == null) {
//            return null
//        }
//        val throwable = UCrop.getError(data)
//        return if (throwable != null) {
//            null
//        } else UCrop.getOutput(data)
//    }
//
//}