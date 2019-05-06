package com.fanchen.mbase.ui

import android.content.Intent
import android.os.Bundle
import com.fanchen.mbase.util.SystemUtil
import com.fanchen.mbase.warp.showToast
import com.zwh.easy.permissions.EasyPermission
import com.zwh.easy.permissions.PermissionCallback
import com.zwh.easy.permissions.PermissionItem

/**
 * 权限管理Activity
 * BasePermissionActivity
 */
abstract class BasePermissionActivity : BaseActivity(), PermissionCallback {

    abstract fun getPermissions(): List<PermissionItem>

    open fun getPermissionStyle(): Int {
        return -1
    }

    override fun beforeCreate(intent: Intent?, savedState: Bundle?): Boolean {
        val permissions = getPermissions()
        val create = EasyPermission.create(this)
        val style = getPermissionStyle()
        if (style != -1) create.style(style)
        create.permissions(permissions).checkMutiPermission(this)
        return false
    }

    override fun onFinish() {
        mRootView?.post(this)
    }

    override fun onDeny(permission: String?, code: Int) {
    }

    override fun onGuarantee(permission: String?, code: Int) {
    }

    override fun onClose() {
        showToast("应用缺少必要权限")
        SystemUtil.startAppSetting(this)
    }

}