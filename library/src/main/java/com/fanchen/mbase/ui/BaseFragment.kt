package com.fanchen.mbase.ui

import android.content.SharedPreferences
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.view.ViewGroup
import com.fanchen.mbase.http.OkHttpUtil
import com.fanchen.mbase.util.AppUtil
import com.litesuits.orm.LiteOrm

/**
 * BaseFragment
 * Created by fanchen on 2018/9/3.
 */
abstract class BaseFragment : Fragment(), Runnable {
    private var mSaveState: Bundle? = null
    // 标志位 标志已经初始化完成。
    var isPrepared = false
    //http請求工具
    val mHttpUtil: OkHttpUtil by lazy { (activity as? BaseActivity)?.mHttpUtil ?: OkHttpUtil.with(activity)}
    val mSingleOrm by lazy { (activity as? BaseActivity)?.mSingleOrm ?: LiteOrm.newSingleInstance(activity?.application, AppUtil.getPackageName(activity?.application)) }
    val mCascadeOrm by lazy { (activity as? BaseActivity)?.mCascadeOrm ?:LiteOrm.newCascadeInstance(activity?.application,AppUtil.getPackageName(activity?.application)) }
    //序列化Preferences
    val mPreferences: SharedPreferences by lazy {(activity as? BaseActivity)?.mPreferences ?: PreferenceManager.getDefaultSharedPreferences(activity)}

    override fun onCreate(savedInstanceState: Bundle?) {
        beforCreate(savedInstanceState?.apply { mSaveState = this }, arguments)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = getLayout()
        isPrepared = false
        return getLayoutView(inflater, layout)
    }

    open fun getLayoutView(inflater: LayoutInflater, layout: Int): View? {
        return if (layout <= 0) null else inflater.inflate(layout, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isAdded || isPrepared) return
        if (isLazy() && userVisibleHint) {//懒加载数据
            view.post(this)
        } else if (!isLazy()) {
            view.post(this)
        }
    }

    final override fun run() {
        initFragment(mSaveState, arguments)
        setListener()
        afterRun(mSaveState, arguments)
        isPrepared = true
    }

    /**
     * Fragment数据的懒加载
     */
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isLazy() && userVisibleHint && !isPrepared && isAdded) {
            view?.post(this)
        }
    }


    /**
     * 是否懒加载数据
     */
    fun isLazy(): Boolean {
        return true
    }

    /**
     * 应用布局文件id
     *
     * @return
     */
    abstract fun getLayout(): Int

    /**
     * 初始化视图控件及数据
     *
     * @param savedInstanceState
     * @param args
     */
    open fun initFragment(savedInstanceState: Bundle?, args: Bundle?) {}

    /**
     * 设置监听器
     */
    open fun setListener() {}

    /**
     * * Create之前调用
     * 一般用来获取Bundle里面的数据
     * @param args
     * @param savedInstanceState
     */
    open fun beforCreate(savedInstanceState: Bundle?, args: Bundle?) {}

    /**
     *
     * post run
     */
    open fun afterRun(savedInstanceState: Bundle?, args: Bundle?) {}

    /**
     * @return
     */
    open fun onBackPressed(): Boolean {
        return false
    }

}