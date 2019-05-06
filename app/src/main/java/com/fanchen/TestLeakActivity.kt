package com.fanchen

import android.os.SystemClock
import android.view.View
import com.fanchen.mbase.R
import com.fanchen.mbase.ui.BaseActivity
import com.fanchen.mbase.util.LogUtil
import kotlinx.android.synthetic.main.activity_leak.*

class TestLeakActivity : BaseActivity() {

    private var thread: LeakThread? = null

    override fun getLayout(): Int {
        return R.layout.activity_leak
    }

    fun onClick(v: View) {
        when (v) {
            button1 -> {
                if (thread != null) return
                thread = LeakThread()
                thread?.run = true
                thread?.start()
            }
            button2 -> {
                if (thread == null) return
                thread?.run = false
                thread = null
            }
        }
    }

    private class LeakThread : Thread() {

        var run = true

        override fun run() {
            LogUtil.e("LeakThread","--------- run -------")
            while (run) {
                SystemClock.sleep(1000)
                LogUtil.e("LeakThread","--------- run -------")
            }
        }

    }
}