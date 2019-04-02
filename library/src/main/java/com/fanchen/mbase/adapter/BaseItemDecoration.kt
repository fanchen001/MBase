//package com.fanchen.mbase.adapter
//
//import android.graphics.Rect
//import android.support.v7.widget.RecyclerView
//import android.view.View
//import com.chad.library.adapter.base.BaseQuickAdapter
//import com.fanchen.mbase.util.DisplayUtil
//
///**
// * BaseItemDecoration
// * Created by fanchen on 2018/11/22.
// */
//class BaseItemDecoration(private val offsets: Float) : RecyclerView.ItemDecoration() {
//
//    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
//        val baseQuickAdapter = parent.adapter as? BaseQuickAdapter<*, *>
//        val position = parent.getChildLayoutPosition(view)
//        val dp2px = DisplayUtil.dp2px(parent.context, offsets)
//        if (baseQuickAdapter != null) {
//            if (baseQuickAdapter.headerLayoutCount > 0 && baseQuickAdapter.headerLayoutCount > position) {
//                return
//            } else if (baseQuickAdapter.headerLayoutCount + baseQuickAdapter.data.size <= position) {
//                return
//            }
//        }
//        if (parent.getChildLayoutPosition(view).rem(2) == 0) {
//            outRect.set(dp2px, dp2px, dp2px, 0)
//        } else {
//            outRect.set(0, dp2px, dp2px, 0)
//        }
//    }
//
//}
