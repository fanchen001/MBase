package com.fanchen.mbase.dialog

import android.view.View
import android.widget.AdapterView

/**
 * OnItemClickListener
 * @author fanchen
 */
interface OnItemClickListener {
    /**
     *
     * @param dialog
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    fun onItemClick(dialog: BaseDialog<*>?, parent: AdapterView<*>?, view: View?, position: Int, id: Long)
}
