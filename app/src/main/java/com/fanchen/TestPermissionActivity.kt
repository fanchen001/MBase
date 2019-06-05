package com.fanchen

import android.Manifest
import com.fanchen.mbase.ui.BasePermissionActivity
import com.zwh.easy.permissions.PermissionItem

class TestPermissionActivity : BasePermissionActivity() {

    override fun getLayout(): Int {
        return -1
    }

    override fun getPermissions(): List<PermissionItem> {
        val list = ArrayList<PermissionItem>()
        list.add(PermissionItem(Manifest.permission.READ_PHONE_STATE))
        list.add(PermissionItem(Manifest.permission.CAMERA))
        return list
    }

}