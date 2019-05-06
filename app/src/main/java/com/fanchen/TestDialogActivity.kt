package com.fanchen

import android.view.View
import android.widget.AdapterView
import com.fanchen.mbase.R
import com.fanchen.mbase.dialog.*
import com.fanchen.mbase.ui.BaseActivity
import com.fanchen.mbase.util.HUDUtil
import kotlinx.android.synthetic.main.activity_dialog.*

class TestDialogActivity : BaseActivity() {

    override fun getLayout(): Int {
        return R.layout.activity_dialog
    }

    fun onClick(v: View) {
        when (v) {
            button1 -> NormalDialog.showAlert(this, "Alert")
            button2 -> NormalDialog.showConfirm(this, "Confirm", "content"){ view, dialog -> dialog?.dismiss() }
            button3 -> NormalDialog.showInput(this, "Input", "content"){ view, dialog -> dialog?.dismiss() }
            button4 -> NormalDialog.show(this, "show", arrayOf("btn1", "btn2", "btn3")){ dialog, btn -> dialog?.dismiss() }
            button5 -> MaterialDialog.show(this, "show")
            button6 -> MaterialListDialog.show(this, listOf("aaaa", "bbbb", "cccc", "dddd"), AdapterView.OnItemClickListener { parent, view, position, id -> })
            button7 -> {
                val listOf = listOf(MaterialListDialog.IconText(R.drawable.permission_ic_phone, "手机"), MaterialListDialog.IconText(R.drawable.permission_ic_camera, "相机"))
                MaterialListDialog.show(this, listOf, AdapterView.OnItemClickListener { parent, view, position, id -> })
            }
            button8 -> SheetActionDialog.show(this, "aa", arrayOf("bb", "cc")){ dialog, adapterView, view, i, l -> dialog?.dismiss() }
            button9 ->  HUDUtil.showHUD(this, "loading...")
        }
    }
}