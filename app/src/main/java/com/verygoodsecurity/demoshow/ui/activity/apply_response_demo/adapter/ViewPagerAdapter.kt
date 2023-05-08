package com.verygoodsecurity.demoshow.ui.activity.apply_response_demo.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter constructor(
    activity: FragmentActivity,
    private val fragments: List<Fragment>
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = fragments.count()

    override fun createFragment(position: Int): Fragment = fragments[position]
}