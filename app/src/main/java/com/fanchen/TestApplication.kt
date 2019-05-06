package com.fanchen

import com.fanchen.api.TestApi
import com.fanchen.mbase.LeakApplication
import com.fanchen.mbase.http.OkHttpUtil

class TestApplication: LeakApplication() {

    override fun onCreate() {
        super.onCreate()
        OkHttpUtil.with(this).initHttpUtil(TestApi::class.java,"http://www.kuaidi100.com/",true)
    }

}