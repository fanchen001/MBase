package com.fanchen.mbase.util

import android.content.Context
import android.text.TextUtils
import android.content.Intent
import android.net.Uri
import java.io.File
import android.content.pm.PackageManager
import android.content.ComponentName
import android.app.Activity
import com.fanchen.mbase.warp.showToast

/**
 *
 * Created by fanchen on 2018/9/3.
 */
object SystemUtil {

    /***
     * @param context
     * @param file
     */
    fun installApplication(context: Context?, file: String): Boolean {
        return installApplication(context, File(file))
    }

    /**
     * 安装一个应用程序
     *
     * @param context
     * @param apkfile
     */
    fun installApplication(context: Context?, apkfile: File): Boolean {
        try {
            val intent = Intent()
            intent.action = "android.intent.action.VIEW"
            intent.addCategory("android.intent.category.DEFAULT")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive")
            context?.startActivity(intent)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 卸载应用程序
     *
     * @param context  上下文
     * @param packname 要卸载的包名
     */
    fun uninstallApplication(context: Context?, packname: String): Boolean {
        try {
            val intent = Intent()
            intent.action = "android.intent.action.DELETE"
            // 附加的额外的参数
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.data = Uri.parse("package:" + packname)
            context?.startActivity(intent)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 应用的快捷方式的创建 . 需要如下权限:com.android.launcher.permission.INSTALL_SHORTCUT
     *
     * @param mContext 应用上下文
     * @param name     快捷方式的名称
     * @param clazz    点击快捷方式启动的界面
     * @param drawable 快捷方式的图标
     */
    fun <T : Context> installShortCut(mContext: Context?, name: String, clazz: Class<T>, drawable: Int): Boolean {
        try {
            val shortIntent = Intent()
            // 设置创建快捷方式的过滤器action
            shortIntent.action = "com.android.launcher.action.INSTALL_SHORTCUT"
            // 设置生成的快捷方式的名字
            shortIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name)
            // 设置生成的快捷方式的图标
            shortIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(mContext, drawable))
            val mIntent = Intent(mContext, clazz)
            shortIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, mIntent)
            // 发送广播生成快捷方式
            mContext?.sendBroadcast(shortIntent)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }


    /**
     * 检测是否存在快捷键
     *
     * @param activity Activity
     * @return 是否存在桌面图标
     */
    fun hasShortcut(activity: Activity, name: String): Boolean {
        var isInstallShortcut = false
        try {
            val cr = activity.contentResolver
            val AUTHORITY = "com.android.launcher.settings"
            val CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/favorites?notify=true")
            val c = cr.query(CONTENT_URI, arrayOf("title", "iconResource"), "title=?", arrayOf(name.trim { it <= ' ' }), null)
            if (c != null && c.count > 0) {
                isInstallShortcut = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return isInstallShortcut
    }

    /**
     * 删除程序的快捷方式
     *
     * @param activity Activity
     */
    fun delShortcut(activity: Activity, name: String) {
        try {
            val shortcut = Intent("com.android.launcher.action.UNINSTALL_SHORTCUT")
            // 快捷方式的名称
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, name)
            val appClass = (activity.packageName + "." + activity.localClassName)
            val comp = ComponentName(activity.packageName, appClass)
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, Intent(Intent.ACTION_MAIN).setComponent(comp))
            activity.sendBroadcast(shortcut)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 开启第三方应用
     *
     * @param urlString
     */
    fun startThreeApp(context: Context?, urlString: String) {
        startThreeApp(context, urlString, null, null, null)
    }

    /**
     * @param context
     * @param urlString
     * @param handler
     */
    fun startThreeApp(context: Context?, urlString: String, handler: String) {
        val mIntent = Intent()
        mIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        mIntent.action = "android.intent.action.VIEW"
        mIntent.data = Uri.parse(handler)
        startThreeApp(context, urlString, null, null, mIntent)
    }

    /**
     * @param context
     * @param urlString
     */
    fun startThreeApp(context: Context?, urlString: String, packageName: String, className: String) {
        startThreeApp(context, urlString, packageName, className, null)
    }


    /**
     * 开启一个应用程序
     *
     * @param context  上下文
     * @param packname 要运行的包名
     */
    fun startApplication(context: Context?, packname: String) {
        // 开启这个应用程序的第一个activity. 默认情况下 第一个activity就是具有启动能力的activity.
        try {
            val packinfo = context?.packageManager?.getPackageInfo(packname, PackageManager.GET_ACTIVITIES)
            val activityinfos = packinfo?.activities
            if (activityinfos?.size ?: 0 > 0) {
                val className = activityinfos?.get(0)?.name ?: ""
                val intent = Intent()
                intent.setClassName(packname, className)
                context?.startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 开启第三方应用
     *
     * @param url
     * @param packageName
     * @param className
     */
    fun startThreeApp(context: Context?, url: String, packageName: String?, className: String?, handler: Intent?) {
        var isException = false
        try {
            val mIntent = Intent()
            mIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            mIntent.action = "android.intent.action.VIEW"
            if (!TextUtils.isEmpty(packageName) && !TextUtils.isEmpty(className))
                mIntent.setClassName(packageName!!, className!!)
            if (!TextUtils.isEmpty(url))
                mIntent.data = Uri.parse(url)
            context?.startActivity(mIntent)
        } catch (e: Throwable) {
            isException = true
        } finally {
            if (isException && handler != null) {
                try {
                    context?.showToast("应用未找到，请先下载")
                    context?.startActivity(handler)
                } catch (e: Throwable) {
                    context?.showToast("未找到对应第三方应用")
                }
            } else if (isException && handler == null) {
                context?.showToast("未找到对应第三方应用")
            }
        }
    }

}