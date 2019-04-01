package com.fanchen.mbase.util

import android.provider.MediaStore
import android.graphics.Bitmap
import android.content.Intent
import android.app.Activity
import android.net.Uri


/**
 * 相机相关
 * Created by fanchen on 2018/9/3.
 */
object CameraUtil {
    /**
     * 进入系统拍照
     * @param activity
     * @param outputUri 照片输出路径 Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/image.jpg"))
     */
    fun startActivityForCamera(activity: Activity, outputUri: Uri,requestCode: Int = Activity.RESULT_OK) {
        try {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            // 制定图片保存路径
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri)
            activity.startActivityForResult(intent, requestCode)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 进入系统拍照 (输出为Bitmap)<br></br>
     *
     * 获得输出
     * 在 @`onActivityResult`中<br></br>
     * 通过@`Bitmap bitmap = (Bitmap)intent.data.getExtras().get("data")`获取<br></br>
     *
     * Tips: 返回的Bitmap并非原图的Bitmap而是经过压缩的Bitmap
     * @param activity
     */
    fun startActivityForCamera(activity: Activity, requestCode: Int= Activity.RESULT_OK) {
        try {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            activity.startActivityForResult(intent, requestCode)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 进入系统图库<br></br>
     * 获得输出<br></br>
     * 在 @`onActivityResult`中通过@`Uri uri = intent.getData()`获取<br></br>
     * Uri返回路径格式为 content://media/external/images/media/32073<br></br>
     * 需要经过转换才能获得绝对路径 [com.wuxiaolong.androidutils.library.UriUtil]
     * @param activity
     */
    fun startActivityForGallery(activity: Activity, requestCode: Int = Activity.RESULT_OK) {
        try {
            // 弹出系统图库
            val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activity.startActivityForResult(i, requestCode)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 进入系统裁剪
     * @param inputUri 需裁剪的图片路径 Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/image.jpg")
     * @param outputUri 裁剪后图片路径 Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/image_cut.jpg")
     * @param width 裁剪后宽度(px)
     * @param height 裁剪后高度(px)
     */
    fun startActivityForImageCut(activity: Activity,inputUri: Uri, outputUri: Uri, width: Int= 1080, height: Int = 1920, requestCode: Int = Activity.RESULT_OK) {
        try {
            val intent = Intent("com.android.camera.action.CROP")
            intent.setDataAndType(inputUri, "image/*")
            // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
            intent.putExtra("crop", "true")
            intent.putExtra("scale", true) // 去黑边
            intent.putExtra("scaleUpIfNeeded", true) // 去黑边
            // aspectX aspectY 裁剪框宽高比例
            intent.putExtra("aspectX", width) // 输出是X方向的比例
            intent.putExtra("aspectY", height)
            // outputX outputY 输出图片宽高，切忌不要再改动下列数字，会卡死
            intent.putExtra("outputX", width) // 输出X方向的像素
            intent.putExtra("outputY", height)
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
            intent.putExtra("noFaceDetection", true)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri)
            intent.putExtra("return-data", false) // 设置为不返回数据
            activity.startActivityForResult(intent, requestCode)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}