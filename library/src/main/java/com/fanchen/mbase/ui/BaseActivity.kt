package com.fanchen.mbase.ui

import android.app.Activity
import android.content.*
import android.content.res.Configuration
import android.content.res.Resources
import android.view.LayoutInflater
import android.os.Bundle
import android.os.Build
import android.preference.PreferenceManager
import android.view.View
import com.fanchen.mbase.http.OkHttpUtil
import com.fanchen.mbase.BaseApplication
import com.fanchen.mbase.util.AppUtil
import com.fanchen.mbase.warp.showToast
import com.jude.swipbackhelper.SwipeBackHelper
import com.litesuits.orm.LiteOrm

/**
 * BaseActivity
 * Created by fanchen on 2018/8/31.
 */
abstract class BaseActivity : Activity(), Runnable {
    //http請求工具
    val mHttpUtil: OkHttpUtil by lazy { OkHttpUtil.with(this) }
    val mSingleOrm by lazy { LiteOrm.newSingleInstance(application,AppUtil.getPackageName(application)) }
    val mCascadeOrm by lazy { LiteOrm.newCascadeInstance(application,AppUtil.getPackageName(application)) }
    //序列化Preferences
    val mPreferences: SharedPreferences by lazy { (application as? BaseApplication)?.mPreferences ?: PreferenceManager.getDefaultSharedPreferences(application) }

    //content view
    var mRootView: View? = null
    //SavedState
    var mSavedState: Bundle? = null
    private var mLastTime = System.currentTimeMillis()
    private val mBackPage by lazy { SwipeBackHelper.getCurrentPage(this) }
    private val mBroadcastReceiver by lazy { ArrayList<BroadcastReceiver>() }

    /**
     * 当前activity布局
     *
     * @return
     */
    abstract fun getLayout(): Int

    /**
     * getLayoutView
     * @param inflater
     * @param layout
     * @return
     */
    open fun getLayoutView(inflater: LayoutInflater, layout: Int): View? {
        return if (layout <= 0) null else inflater.inflate(layout, null, false)
    }

    /**
     * onCreate前调用
     * @param intent
     * @param savedState
     */
    open fun beforCreate(intent: Intent?, savedState: Bundle?) {}

    /**
     *
     * post run
     */
    open fun afterRun(intent: Intent?, savedState: Bundle?) {}

    /**
     * 设置监听器
     */
    open fun setListener() {}

    /**
     * 是否开启滑动返回
     */
    open fun openSwipe(): Boolean {
        return (application as? BaseApplication)?.mActivitys?.size ?: 0 > 1
    }

    open fun isDoubleBack(): Boolean {
        return false
    }

    /**
     * 初始化页面数据
     *@param intent
     * @param savedState
     * @param inflater
     */
    open fun initActivity(intent: Intent?, savedState: Bundle?, inflater: LayoutInflater) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        val inte = intent
        beforCreate(inte, savedInstanceState)
        super.onCreate(savedInstanceState.apply { mSavedState = this })
        val layoutInflater = layoutInflater
        mRootView = getLayoutView(layoutInflater, getLayout())
        if(mRootView != null)setContentView(mRootView)
        SwipeBackHelper.onCreate(this)
        mBackPage.setSwipeEdgePercent(getSwipeEdgePercent())
        mBackPage.setSwipeBackEnable(openSwipe())// 设置是否可滑动
        mBackPage.setSwipeRelateEnable(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)// 是否与下一级activity联动(微信效果)仅限5.0以上机器
        mBackPage.setDisallowInterceptTouchEvent(false)
        mRootView?.post(this)
    }

    override fun getResources(): Resources {//还原字体大小
        val res = super.getResources()
        val config = Configuration()
        config.setToDefaults()
        res.updateConfiguration(config, res.displayMetrics)
        return res
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // 使用滑动关闭功能
        SwipeBackHelper.onPostCreate(this)
    }

    override fun onBackPressed() {
        val timeMillis = System.currentTimeMillis()
        if (timeMillis - mLastTime < 1500 || !isDoubleBack()) {
            super.onBackPressed()
        } else {
            mLastTime = timeMillis
            showToast("再按一次退出程序")
        }
    }

    final override fun run() {
        if (isDestroyed) return
        initActivity(intent, mSavedState, layoutInflater)
        setListener()
        afterRun(intent, mSavedState)
    }

    fun finishAndResult(code: Int = Activity.RESULT_OK) {
        setResult(code)
        super.finish()
    }

    fun activitySize(): Int {
        val baseApplication = application as? BaseApplication ?: return 0
        return baseApplication.mActivitys.size
    }

    open fun getSwipeEdgePercent(): Float {
        return 0.2f
    }

    override fun onDestroy() {
        super.onDestroy()
        //webview 页面有时候有 Receiver 注册了，但在onDestroy 没有 unregister 这里为了解决这个问题
        try {
            for (receiver in mBroadcastReceiver) {
                super.unregisterReceiver(receiver)
            }
            mBroadcastReceiver.clear()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        SwipeBackHelper.onDestroy(this)
    }

    override fun onStop() {
        super.onStop()
        // 防止调用onStop后使用fragment出现
        // Can not perform this action after onSaveInstanceState 异常
        onNewIntent(Intent())
    }

    override fun onSaveInstanceState(arg0: Bundle?) {
        super.onSaveInstanceState(arg0)
        // 防止调用onSaveInstanceState后使用fragment出现
        // Can not perform this action after onSaveInstanceState 异常
        onNewIntent(Intent())
    }

    fun getView(): View? {
        return mRootView
    }

    /**
     * post
     */
    fun post(time: Long = 0, call: () -> Unit) {
        if (time <= 0) {
            mRootView?.post(call)
        } else {
            mRootView?.postDelayed(call, time)
        }
    }


    fun remove(call: () -> Unit) {
        mRootView?.removeCallbacks(call)
    }

    fun removeAndPost(time: Long = 0, call: () -> Unit) {
        remove(call)
        post(time,call)
    }

    override fun registerReceiver(receiver: BroadcastReceiver?, filter: IntentFilter): Intent? {
        try {
            if (!mBroadcastReceiver.contains(receiver) && receiver != null) {
                mBroadcastReceiver.add(receiver)
                return super.registerReceiver(receiver, filter)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun unregisterReceiver(receiver: BroadcastReceiver) {
        try {
            if (mBroadcastReceiver.contains(receiver)) {
                mBroadcastReceiver.remove(receiver)
                super.unregisterReceiver(receiver)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}