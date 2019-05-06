package com.fanchen

import android.Manifest
import com.fanchen.mbase.R
import com.fanchen.mbase.ui.BasePermissionActivity
import com.zwh.easy.permissions.PermissionItem

class TestPermissionActivity : BasePermissionActivity() {

    override fun getLayout(): Int {
        return -1
    }

    override fun getPermissions(): List<PermissionItem> {
        val list = ArrayList<PermissionItem>()
        list.add(PermissionItem(Manifest.permission.READ_PHONE_STATE,"IMEI", R.drawable.permission_ic_phone))
        list.add(PermissionItem(Manifest.permission.CAMERA,"相机", R.drawable.permission_ic_camera))
        return list
    }

}