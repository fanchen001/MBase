package com.fanchen.mbase.dialog

/**
 * OnButtonClickListener
 * @author fanchen
 */
interface OnButtonClickListener {

    companion object {
        const val LIFT = 0
        const val CENTRE = 1
        const val RIGHT = 2
    }

    fun onButtonClick(dialog: BaseAlertDialog<*>?, btn: Int)
}
