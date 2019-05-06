package com.fanchen.mbase

import android.app.Activity
import android.app.Application
import android.content.*
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.multidex.MultiDexApplication
import com.bilibili.boxing.BoxingCrop
import com.bilibili.boxing.BoxingMediaLoader
import com.fanchen.mbase.boxing.GlideMediaLoaderImpl
import com.fanchen.mbase.boxing.UcropImpl
import com.fanchen.mbase.dialog.BaseDialog
import com.fanchen.mbase.library.R
import com.fanchen.mbase.warp.startActivity

/**
 * BaseAppliction
 * Created by fanchen on 2018/9/5.
 */
abstract class BaseApplication : MultiDexApplication(), Application.ActivityLifecycleCallbacks {

    companion object {
        var instance: BaseApplication? = null
    }

    val mActivitys by lazy { ArrayList<Activity?>() }// 用来管理activity的列表,实现对程序整体异常的捕获
    val mPreferences: SharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(this) }

    override fun onCreate() {
        super.onCreate()
        BaseApplication.instance = this
        BaseDialog.DEFAULT_STYLE = R.style.BaseDialog
        BoxingMediaLoader.getInstance().init(GlideMediaLoaderImpl())
        BoxingCrop.getInstance().init(UcropImpl())
        registerActivityLifecycleCallbacks(this)
    }

    /**
     * 关闭除Class外的全部activity
     * 并開啟新的activity
     * @param intent
     */
    fun finishActivity(clazz: Class<*>? = null, intent: Intent? = null) {
        val newList = mActivitys.filter { clazz != it?.javaClass }
        if (intent != null) startActivity(intent)
        newList.forEach { it?.finish() }
    }

    fun finishActivity(intent: Class<*>? = null) {
        mActivitys.forEach { it?.finish() }
        if (intent != null) startActivity(intent)
    }

    /**
     * Activity关闭时，删除Activity列表中的Activity对象
     */
    private fun removeActivity(a: Activity?) {
        mActivitys.remove(a)
    }

    /**
     * 向Activity列表中添加Activity对象
     */
    private fun addActivity(a: Activity?) {
        mActivitys.add(a)
    }

    /**
     * 获取最上层的Activity
     */
    fun getTopActivity(): Activity? {
        return if (mActivitys.size >= 1) mActivitys[mActivitys.size - 1] else null
    }

    /**
     * 关闭Activity列表中的所有Activity
     */
    fun exit(code: Int = 0) {
        for (activity in mActivitys.indices) {
            mActivitys.removeAt(activity)?.finish()
        }
        // 杀死该应用进程
        android.os.Process.killProcess(android.os.Process.myPid())
        System.exit(code)
    }

    override fun startService(service: Intent): ComponentName? {
        try {
            return super.startService(service)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun startActivity(intent: Intent) {
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            super.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun startActivities(intents: Array<Intent>) {
        try {
            for (intent in intents) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            super.startActivities(intents)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun startActivities(intents: Array<Intent>, options: Bundle) {
        try {
            for (intent in intents) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            super.startActivities(intents, options)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun startActivity(intent: Intent, options: Bundle?) {
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            super.startActivity(intent, options)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun registerReceiver(receiver: BroadcastReceiver?, filter: IntentFilter): Intent? {
        try {
            return super.registerReceiver(receiver, filter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun registerReceiver(receiver: BroadcastReceiver?, filter: IntentFilter?, flags: Int): Intent? {
        try {
            return super.registerReceiver(receiver, filter, flags)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun registerReceiver(receiver: BroadcastReceiver?, filter: IntentFilter?, broadcastPermission: String?, scheduler: Handler?): Intent? {
        try {
            return super.registerReceiver(receiver, filter, broadcastPermission, scheduler)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun registerReceiver(receiver: BroadcastReceiver?, filter: IntentFilter?, broadcastPermission: String?, scheduler: Handler?, flags: Int): Intent? {
        try {
            return super.registerReceiver(receiver, filter, broadcastPermission, scheduler, flags)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        addActivity(activity)
    }

    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivityResumed(activity: Activity?) {
    }

    override fun onActivityPaused(activity: Activity?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
        removeActivity(activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

}