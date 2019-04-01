package com.fanchen.mbase.warp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.Toast

/**
 * ContextWrap
 *
 * 对Context 进行一些扩展
 *
 * Created by fanchen on 2018/8/31.
 */
/**
 * 可以在子线程显示的Toast
 * showToast
 */
fun Context.showToast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
    if ("main" == Thread.currentThread().name) {
        Toast.makeText(this, text, duration).show()
    } else (this as? Activity)?.runOnUiThread { showToast(text, duration) }
}

/**
 * 可以在子线程显示的Toast
 * showToast
 */
fun Context.showToast(text: Int, duration: Int = Toast.LENGTH_SHORT) {
    if ("main" == Thread.currentThread().name) {
        Toast.makeText(this, text, duration).show()
    } else (this as? Activity)?.runOnUiThread { showToast(text, duration) }
}

/**
 * 可以在子线程显示的showSnackbar
 */
fun Context.showSnackbar(view: View, title: Int, c: Int, duration: Int = Toast.LENGTH_SHORT, l: ((View) -> Unit)? = null) {
    if ("main" == Thread.currentThread().name) {
        Snackbar.make(view, c, duration).setAction(title, l).show();
    } else (this as? Activity)?.runOnUiThread { showSnackbar(view, c, duration, title, l) }
}

/**
 * 可以在子线程显示的showSnackbar
 */
fun Context.showSnackbar(view: View, title: CharSequence = "", c: CharSequence = "", duration: Int = Toast.LENGTH_SHORT, l: ((View) -> Unit)? = null) {
    if ("main" == Thread.currentThread().name) {
        Snackbar.make(view, c, duration).setAction(title, l).show();
    } else (this as? Activity)?.runOnUiThread { showSnackbar(view, title, c, duration, l) }
}

fun Activity.bindService(service: Class<*>, conn: ServiceConnection, flags: Int = 0): Boolean {
    return bindService(Intent(this, service), conn, flags)
}

/**
 * 开启一个Activity
 *
 * @param clazz
 * @param bundle
 */
fun Context.startActivity(clazz: Class<*>, bundle: Bundle? = null,finish:Boolean = false) {
   try {
       val intent = Intent(this, clazz)
       if (bundle != null) intent.putExtras(bundle)
       if (this is Activity) {
           startActivity(intent)
       } else {
           intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
           startActivity(intent)
       }
       if(finish && this is Activity) finish()
   }catch ( e: Exception){
       e.printStackTrace()
   }
}

fun Context.startActivity(intent: Intent,finish:Boolean = false) {
    try {
        if (this is Activity) {
            startActivity(intent)
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        if(finish && this is Activity) finish()
    }catch ( e: Exception){
        e.printStackTrace()
    }
}

/**
 * 开启一个Activity
 */
fun Context.startActivity(clazz: Class<*>, key: String? = null, value: String? = null,finish:Boolean = false) {
    try {
        val intent = Intent(this, clazz)
        if (key != null && value != null) intent.putExtra(key, value)
        if (this is Activity) {
            startActivity(intent)
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        if(finish && this is Activity) finish()
    }catch ( e: Exception){
        e.printStackTrace()
    }
}

fun Context.startActivity(clazz: Class<*>, key: String? = null, value: Parcelable? = null,finish:Boolean = false) {
    try {
        val intent = Intent(this, clazz)
        if (key != null && value != null) intent.putExtra(key, value)
        if (this is Activity) {
            startActivity(intent)
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        if(finish && this is Activity) finish()
    }catch ( e: Exception){
        e.printStackTrace()
    }
}

fun Context.startActivity(clazz: Class<*>, key: String? = null, value: Int = 0,finish:Boolean = false) {
    try {
        val intent = Intent(this, clazz)
        if (key != null) intent.putExtra(key, value)
        if (this is Activity) {
            startActivity(intent)
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        if(finish && this is Activity) finish()
    }catch ( e: Exception){
        e.printStackTrace()
    }
}

/**
 * 开启一个Service
 *
 * @param clazz
 * @param bundle
 */
fun Context.startService(clazz: Class<*>, bundle: Bundle? = null) {
    try {
        val intent = Intent(this, clazz)
        if (bundle != null) intent.putExtras(bundle)
        startService(intent)
    }catch ( e: Exception){
        e.printStackTrace()
    }
}

