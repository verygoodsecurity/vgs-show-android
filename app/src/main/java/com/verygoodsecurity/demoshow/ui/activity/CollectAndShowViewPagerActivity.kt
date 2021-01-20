package com.verygoodsecurity.demoshow.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.verygoodsecurity.demoshow.R
import com.verygoodsecurity.demoshow.ui.fragment.CollectAndShowFragment
import kotlinx.android.synthetic.main.activity_viewpager_collect_and_show.*

private const val PAGES_COUNT = 2

class VGSShowViewPagerActivity : AppCompatActivity(R.layout.activity_viewpager_collect_and_show) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewPager()
    }

    private fun initViewPager() {
        vpVGSViewPagerActivity?.adapter = VGSShowFragmentsAdapter(this)
    }

    private inner class VGSShowFragmentsAdapter constructor(
        fa: FragmentActivity
    ) : FragmentStateAdapter(fa) {

        override fun getItemCount(): Int = PAGES_COUNT

        override fun createFragment(position: Int): Fragment = CollectAndShowFragment()
    }
}