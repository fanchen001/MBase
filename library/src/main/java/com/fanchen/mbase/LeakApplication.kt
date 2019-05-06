package com.fanchen.mbase

import com.squareup.leakcanary.LeakCanary

/***
 * LeakApplication
 */
open class LeakApplication : BaseApplication(){

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this))return
        LeakCanary.install(this)
    }

}