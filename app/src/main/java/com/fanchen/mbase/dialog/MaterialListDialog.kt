package com.fanchen.mbase.dialog

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView

/**
 * MaterialListDialog
 * Created by fanchen on 2017/7/24.
 */
class MaterialListDialog : MaterialDialog, AdapterView.OnItemClickListener {

    private var mListView: ListView? = null
    private var mAdapter: ListAdapter<*>? = null
    private var itemClickListener: AdapterView.OnItemClickListener? = null

    constructor(context: Context, titles: Array<Any>, style: Int = BaseDialog.DEFAULT_STYLE) : super(context, ListView(context), style) {
        mAdapter = ListAdapter(context, android.R.layout.simple_list_item_1, titles)
        init()
    }

    constructor(context: Context, titles: List<Any>, style: Int = BaseDialog.DEFAULT_STYLE) : super(context, ListView(context), style) {
        mAdapter = ListAdapter(context, android.R.layout.simple_list_item_1, titles)
        init()
    }

    private fun init() {
        mListView = view as ListView
        mListView?.adapter = mAdapter
        mListView?.divider = null
        mListView?.onItemClickListener = this
        setButtonVisble(View.GONE)
        setTitleVisble(View.GONE)
    }

    override fun title(title: String): MaterialDialog {
        if (!TextUtils.isEmpty(title)) setTitleVisble(View.VISIBLE)
        return super.title(title)
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        if (itemClickListener != null)
            itemClickListener?.onItemClick(parent, view, position, id)
        dismiss()
    }

    fun setItemClickListener(itemClickListener: AdapterView.OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    /**
     *
     */
    class IconText {
        var drawable: Int = 0
        var title: String?= ""

        constructor(drawable: Int, title: String) {
            this.drawable = drawable
            this.title = title
        }

        constructor()

        override fun toString(): String {
            return title ?: ""
        }

    }

    private class ListAdapter<T> : ArrayAdapter<T> {

        constructor(context: Context, resource: Int, objects: Array<T>) : super(context, resource, objects) {}

        constructor(context: Context, resource: Int, objects: List<T>) : super(context, resource, objects) {}

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getView(position, convertView, parent)
            val item = getItem(position)
            if (item is IconText && view is TextView) {
                val drawable = parent.context.resources.getDrawable((item as IconText).drawable)
                drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                view.setCompoundDrawables(drawable, null, null, null)
            }
            return view
        }
    }

    companion object {

        /**
         * @param context
         * @param contents
         * @param l
         * @return
         */
        fun show(context: Context, contents: Array<Any>?, l: AdapterView.OnItemClickListener): MaterialListDialog? {
            if (contents == null) return null
            val dialog = MaterialListDialog(context, contents)
            dialog.setItemClickListener(l)
            dialog.show()
            return dialog
        }

        /**
         * @param context
         * @param title
         * @param contents
         * @param l
         * @return
         */
        fun show(context: Context, title: String, contents: List<Any>?, l: AdapterView.OnItemClickListener): MaterialListDialog? {
            if (contents == null) return null
            val dialog = MaterialListDialog(context, contents)
            dialog.setItemClickListener(l)
            dialog.title(title)
            dialog.show()
            return dialog
        }

        fun show(context: Context, title: String, contents: Array<Any>?, l: AdapterView.OnItemClickListener): MaterialListDialog? {
            if (contents == null) return null
            val dialog = MaterialListDialog(context, contents)
            dialog.setItemClickListener(l)
            dialog.title(title)
            dialog.show()
            return dialog
        }

        /**
         * @param context
         * @param contents
         * @param l
         * @return
         */
        fun show(context: Context, contents: List<Any>?, l: AdapterView.OnItemClickListener): MaterialListDialog? {
            if (contents == null) return null
            val dialog = MaterialListDialog(context, contents)
            dialog.setItemClickListener(l)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(false)
            dialog.show()
            return dialog
        }
    }

}
