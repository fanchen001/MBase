package com.fanchen.mbase.util

import android.annotation.SuppressLint
import android.content.Context
import java.net.NetworkInterface
import android.net.wifi.WifiManager
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.telephony.TelephonyManager


/**
 * 網絡相關
 * Created by fanchen on 2018/9/3.
 */
object NetworkUtil {
    val TYPE_WIFI = 1
    val TYPE_MOBILE = 2
    val TYPE_ERROR = -1

    /** 获得设备ip地址  */
    fun getLocalAddress(): String {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf.getInetAddresses()
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress()
                    }
                }
            }
        } catch (e: Exception) {
        }
        return ""
    }


    /**
     * 设置是否启用WIFI网络
     *
     * @param context
     * @param status
     */
    @SuppressLint("MissingPermission")
    fun toggleWiFi(context: Context?, status: Boolean) {
        try {
            val wifiManager = context?.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            wifiManager?.isWifiEnabled = status
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /**
     * 判断当前网络类型-1为未知网络0为没有网络连接1
     * 网络断开或关闭
     * 2为以太网
     * 3为WiFi
     * 4为2G
     * 5为3G
     * 6为4G
     */
    @SuppressLint("MissingPermission")
    fun getNetworkType(context: Context?): Int {
        try {
            val connectMgr = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            val networkInfo = connectMgr?.activeNetworkInfo ?: return 0
            if (!networkInfo.isConnected) return 1
            /** 网络断开或关闭  */
            if (networkInfo.type == ConnectivityManager.TYPE_ETHERNET) return 2
            /** 以太网网络  */
            else if (networkInfo.type == ConnectivityManager.TYPE_WIFI) return 3
            /** wifi网络，当激活时，默认情况下，所有的数据流量将使用此连接  */
            else if (networkInfo.type == ConnectivityManager.TYPE_MOBILE)
                when (networkInfo.subtype) {
                    TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN -> return 4
                /** 2G网络  */
                    TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP -> return 5
                /** 3G网络  */
                    TelephonyManager.NETWORK_TYPE_LTE ->
                        /** 4G网络  */
                        return 6
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        /** 未知网络  */
        return -1
    }


    /**
     * 检查当前网络状态是否可用
     *
     * @param context
     * 上下文
     * @return 是否有网络连接
     */
    @SuppressLint("MissingPermission")
    fun isNetWorkAvailable(context: Context?): Boolean {
        try {
            val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            return cm?.activeNetworkInfo?.isConnected ?: false
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
    獲取網絡類型
     * @param context
     * @return
     */
    fun reportNetType(context: Context): Int {
        var netMode = TYPE_ERROR
        try {
            val info = getNetworkInfo(context)
            if (info != null && info.isAvailable) {
                val netType = info.type
                if (netType == ConnectivityManager.TYPE_WIFI) {
                    netMode = TYPE_WIFI
                } else if (netType == ConnectivityManager.TYPE_MOBILE) {
                    netMode = TYPE_MOBILE
                }
            }
        } catch (e: Exception) {
        }
        return netMode
    }

    /**
     * 获取网络连接信息
     */
    @SuppressLint("MissingPermission")
    private fun getNetworkInfo(context: Context?): NetworkInfo? {
        try {
            val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            return connectivityManager?.activeNetworkInfo
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }


    /**
     * @author 获取当前的网络状态 -1：没有网络 1：WIFI网络2：wap网络
     * @param context
     * @return
     */
    @SuppressLint("MissingPermission")
    fun getAPNType(context: Context?): Int {
        var netType = -1
        try {
            val connMgr = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            val networkInfo = connMgr?.activeNetworkInfo ?: return netType
            val nType = networkInfo.type
            if (nType == ConnectivityManager.TYPE_MOBILE) {
                if (networkInfo.extraInfo.toLowerCase() == "cmnet") {
                    netType = 3
                } else {
                    netType = 2
                }
            } else if (nType == ConnectivityManager.TYPE_WIFI) {
                netType = 1
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return netType
    }


    /**
     * // 判断WIFI网络是否可用
     *
     * @param mContext
     * 上下文
     * @return 是否是WiFi连接
     */
    @SuppressLint("MissingPermission")
    fun isWifiConnected(mContext: Context?): Boolean {
        try {
            val mConnectivityManager = mContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            return mConnectivityManager?.getNetworkInfo(ConnectivityManager.TYPE_WIFI)?.isConnected ?: false
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * // 判断MOBILE网络是否可用
     *
     * @param context
     * 上下文
     * @return 是否是2/3G网络
     */
    @SuppressLint("MissingPermission")
    fun isMobileConnected(context: Context?): Boolean {
        try {
            val mConnectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            return mConnectivityManager?.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)?.isConnected ?: false
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 打开手机网络设置界面
     *
     * @param mContext
     * 上下文
     */
    fun openNetworkSetting(mContext: Context) {
        var intent: Intent? = null
        // 判断手机系统的版本 即API大于10 就是3.0或以上版本
        intent = Intent(android.provider.Settings.ACTION_WIFI_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        mContext.startActivity(intent)
    }

    /** 获取当前设备的MAC地址  */
    @SuppressLint("MissingPermission")
    fun getMacAddress(context: Context?): String {
        try {
            val wm = context?.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            val info = wm?.connectionInfo
           return info?.macAddress ?: ""
        } catch (e: Exception) {
        }
        return ""
    }
}