package com.fanchen

import android.view.View
import com.fanchen.mbase.R
import com.fanchen.mbase.ui.BaseActivity
import com.fanchen.mbase.warp.startActivity
import kotlinx.android.synthetic.main.activity_test.*

class TestActivity : BaseActivity() {

    override fun getLayout(): Int {
        return R.layout.activity_test
    }

    fun onClick(v : View){
        when(v){
            button1 -> startActivity(TestPermissionActivity::class.java)
            button2 -> startActivity(TestHttpActivity::class.java)
            button3 -> startActivity(TestDialogActivity::class.java)
            button4 -> startActivity(TestFragmentActivity::class.java)
            button5 -> startActivity(TestLeakActivity::class.java)
        }
    }

}