//package com.fanchen.mbase.util
//
//import android.app.Activity
//import android.content.Context
//import android.os.Handler
//import android.os.Looper
//import com.kaopiz.kprogresshud.KProgressHUD
//import java.util.HashMap
//
//object HUDUtil{
//    private val map = HashMap<KProgressHUD.Style, KProgressHUD>()
//    private val mapRunnable = HashMap<KProgressHUD.Style, Runnable>()
//    private val mHandler = Handler(Looper.getMainLooper())
//    /**
//     * @param context
//     * @param style
//     * @param cancellable
//     * @param label
//     */
//    fun showHUD(context: Context?, style: KProgressHUD.Style, cancellable: Boolean, label: String) {
//        if (Thread.currentThread().name == "main") {
//            privateShowHUD(context, style, cancellable, label)
//        } else {
//            mHandler.post { privateShowHUD(context, style, cancellable, label) }
//        }
//    }
//
//    /**
//     * @param context
//     * @param style
//     * @param cancellable
//     * @param label
//     */
//    private fun privateShowHUD(context: Context?, style: KProgressHUD.Style, cancellable: Boolean, label: String) {
//        closeHUD(style)
//        if (context == null || context is Activity && context.isFinishing) return
//        map[style] = KProgressHUD.create(context).setStyle(style).setLabel(label).setCancellable(cancellable).show()
//        val closeHUDRunnable = CloseHUDRunnable()
//        mapRunnable[style] = closeHUDRunnable
//        mHandler.postDelayed(closeHUDRunnable, (60 * 1000).toLong())
//    }
//
//    /**
//     * @param context
//     * @param style
//     * @param label
//     */
//    fun showHUD(context: Context?, style: KProgressHUD.Style, label: String) {
//        if (Thread.currentThread().name == "main") {
//            privateShowHUD(context, style, label)
//        } else {
//            mHandler.post { privateShowHUD(context, style, label) }
//        }
//    }
//
//    /**
//     * @param context
//     * @param style
//     * @param label
//     */
//    private fun privateShowHUD(context: Context?, style: KProgressHUD.Style, label: String) {
//        closeHUD(style)
//        if (context == null || context is Activity && context.isFinishing) return
//        map[style] = KProgressHUD.create(context).setStyle(style).setLabel(label).setCancellable(false).show()
//        val closeHUDRunnable = CloseHUDRunnable()
//        mapRunnable[style] = closeHUDRunnable
//        mHandler.postDelayed(closeHUDRunnable, (60 * 1000).toLong())
//    }
//
//    /**
//     * @param context
//     * @param label
//     */
//    fun showHUD(context: Context?, label: String) {
//        if (Thread.currentThread().name == "main") {
//            privateShowHUD(context, label)
//        } else {
//            mHandler.post { privateShowHUD(context, label) }
//        }
//    }
//
//    /**
//     * @param context
//     * @param label
//     */
//    private fun privateShowHUD(context: Context?, label: String) {
//        closeHUD(KProgressHUD.Style.SPIN_INDETERMINATE)
//        if (context == null || context is Activity && context.isFinishing) return
//        map[KProgressHUD.Style.SPIN_INDETERMINATE] = KProgressHUD.create(context).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).setLabel(label).setCancellable(false).show()
//        val closeHUDRunnable = CloseHUDRunnable()
//        mapRunnable[KProgressHUD.Style.SPIN_INDETERMINATE] = closeHUDRunnable
//        mHandler.postDelayed(closeHUDRunnable, (60 * 1000).toLong())
//    }
//
//    /**
//     * @param style
//     */
//    fun closeHUD(style: KProgressHUD.Style? = KProgressHUD.Style.SPIN_INDETERMINATE) {
//        if (style == null) return
//        mHandler.removeCallbacks(mapRunnable.remove(style))
//        if (Thread.currentThread().name == "main") {
//            val kProgressHUD = map.remove(style)
//            if (kProgressHUD != null) {
//                kProgressHUD.dismiss()
//            }
//        } else {
//            mHandler.post {
//                val kProgressHUD = map.remove(style)
//                if (kProgressHUD != null) {
//                    kProgressHUD.dismiss()
//                }
//            }
//        }
//    }
//
//    private class CloseHUDRunnable : Runnable {
//
//        override fun run() {
//            if (mapRunnable.isEmpty()) return
//            for ((key, value) in mapRunnable) {
//                if (value === this) {
//                    closeHUD(key)
//                }
//            }
//        }
//
//    }
//
//}