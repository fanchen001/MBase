package com.fanchen.mbase.util

import android.app.Activity
import android.content.Intent
import android.os.Environment
import android.support.v4.app.Fragment
import com.leon.lfilepickerlibrary.LFilePicker

object FilePickerUtil {

    fun start(activity: Activity, code: Int = Activity.RESULT_FIRST_USER, path: String = Environment.getExternalStorageDirectory().absolutePath,
              fileFilter: Array<String>? = null, maxNum: Int = 1) {
        LFilePicker().withActivity(activity).withRequestCode(code).withStartPath(path)//指定初始显示路径
                .withFileFilter(fileFilter).withMaxNum(maxNum).start()
    }

    fun start(fragment: Fragment, code: Int = Activity.RESULT_FIRST_USER, path: String = Environment.getExternalStorageDirectory().absolutePath,
              fileFilter: Array<String>? = null, maxNum: Int = 1) {
        LFilePicker().withSupportFragment(fragment).withRequestCode(code).withStartPath(path)//指定初始显示路径
                .withFileFilter(fileFilter).withMaxNum(maxNum).start()
    }

    fun getFileList(data: Intent?): List<String>? {
        return data?.getStringArrayListExtra("paths")
    }

    fun getDir(data: Intent?): String? {
        return data?.getStringExtra("path")
    }

}