package com.fanchen

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import com.fanchen.mbase.ui.BaseActivity
import com.fanchen.mbase.util.BoxingUtil

class Test : BaseActivity() {
    override fun getLayout(): Int {
        return -1
    }

    override fun initActivity(intent: Intent?, savedState: Bundle?, inflater: LayoutInflater) {
        super.initActivity(intent, savedState, inflater)

        BoxingUtil.singleStart(this)


        TestFragment()
    }
}