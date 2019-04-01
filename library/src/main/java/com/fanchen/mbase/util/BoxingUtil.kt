package com.fanchen.mbase.util

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v4.app.Fragment
import android.text.TextUtils
import com.bilibili.boxing.Boxing
import com.bilibili.boxing.model.config.BoxingConfig
import com.bilibili.boxing.model.config.BoxingCropOption
import com.bilibili.boxing.model.entity.impl.ImageMedia
import com.bilibili.boxing.utils.BoxingFileHelper
import com.bilibili.boxing.utils.ImageCompressor
import com.bilibili.boxing_impl.ui.BoxingActivity
import com.fanchen.mbase.warp.showToast
import java.io.File
import java.util.*

object BoxingUtil {

    fun singleStart(activity: Activity, camera: Int = 0, default: Int = 0, code: Int = Activity.RESULT_FIRST_USER, hit: String = "权限不足",
                    isCrop: Boolean = true, ratio: Array<Float> = arrayOf(1f, 1f)) {
        val cachePath = BoxingFileHelper.getCacheDir(activity)
        if (TextUtils.isEmpty(cachePath)) {
            activity.showToast(hit)
        } else {
            val format = String.format(Locale.US, "%s.png", System.currentTimeMillis())
            val destUri = Uri.Builder().scheme("file").appendPath(cachePath).appendPath(format).build()
            val single = if (isCrop) {
                val cropOption = BoxingCropOption(destUri).aspectRatio(ratio[0], ratio[1])
                BoxingConfig(BoxingConfig.Mode.SINGLE_IMG).needCamera(camera).withCropOption(cropOption).withMediaPlaceHolderRes(default)
            } else {
                BoxingConfig(BoxingConfig.Mode.SINGLE_IMG).needCamera(camera).withMediaPlaceHolderRes(default)
            }
            Boxing.of(single).withIntent(activity, BoxingActivity::class.java).start(activity, code)
        }
    }

    fun singleStart(fragment: Fragment, camera: Int, default: Int, code: Int = Activity.RESULT_FIRST_USER, hit: String = "权限不足",
                    isCrop: Boolean = true, ratio: Array<Float> = arrayOf(1f, 1f)) {
        val fragmentActivity = fragment.activity ?: return
        val cachePath = BoxingFileHelper.getCacheDir(fragmentActivity)
        if (TextUtils.isEmpty(cachePath)) {
            fragmentActivity.showToast(hit)
        } else {
            val format = String.format(Locale.US, "%s.png", System.currentTimeMillis())
            val destUri = Uri.Builder().scheme("file").appendPath(cachePath).appendPath(format).build()
            val single = if (isCrop) {
                val cropOption = BoxingCropOption(destUri).aspectRatio(ratio[0], ratio[1])
                BoxingConfig(BoxingConfig.Mode.SINGLE_IMG).needCamera(camera).withCropOption(cropOption).withMediaPlaceHolderRes(default)
            } else {
                BoxingConfig(BoxingConfig.Mode.SINGLE_IMG).needCamera(camera).withMediaPlaceHolderRes(default)
            }
            Boxing.of(single).withIntent(fragmentActivity, BoxingActivity::class.java).start(fragment, code)
        }
    }

    fun getFile(fragment: Fragment, data: Intent?): File? {
        val fragmentActivity = fragment.activity ?: return null
        return getFile(fragmentActivity, data)
    }

    fun getFile(activity: Activity, data: Intent?): File? {
        val result = Boxing.getResult(data)
        if (result == null || result.isEmpty()) return null
        val baseMedia = result[0] as? ImageMedia ?: return null
        if (baseMedia.compress(ImageCompressor(activity))) {
            baseMedia.removeExif()
        }
        return File(baseMedia.path)
    }
}