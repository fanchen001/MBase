package com.fanchen.mbase.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter

/**
 *BaseFragmentAdapter
 * Created by fanchen on 2018/9/3.
 */
abstract class BaseFragmentAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    protected val fragments = ArrayList<Fragment>()

    /**
     *
     * @return
     */
    abstract val titles: Array<String?>?

    override fun getItem(position: Int): Fragment? {
        return if (fragments.size > position && getItemPosition(fragments[position]) == PagerAdapter.POSITION_UNCHANGED) {
            fragments[position]
        } else {
            val fragment = createFragment(position)
            fragments.add(fragment)
            fragment
        }
    }

    override fun getCount(): Int {
        return titles?.size ?: 0
    }

    override fun getPageTitle(position: Int): CharSequence {
        return titles?.get(position) ?: ""
    }

    /**
     *
     * @param position
     * @return
     */
    abstract fun createFragment(position: Int): Fragment

    /**
     * 擴展數據
     * @return
     */
    open fun getExtendInfo(): Any? {
        return null
    }
}
