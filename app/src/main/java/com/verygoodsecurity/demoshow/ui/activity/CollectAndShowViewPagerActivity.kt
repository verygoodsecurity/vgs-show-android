package com.verygoodsecurity.demoshow.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.verygoodsecurity.demoshow.R
import com.verygoodsecurity.demoshow.ui.fragment.CollectFragment
import com.verygoodsecurity.demoshow.ui.fragment.ShowFragment
import kotlinx.android.synthetic.main.activity_viewpager_collect_and_show.*

private const val PAGES_COUNT = 2

class VGSShowViewPagerActivity : AppCompatActivity(R.layout.activity_viewpager_collect_and_show),
    OnCardAliasChangeListener {

    private var aliasChangeListener: OnCardAliasChangeListener? = null

    private var cardNumberAlias: String = ""
    private var expirationDateAlias: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewPager()
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        if (fragment is OnCardAliasChangeListener) {
            aliasChangeListener = fragment
            aliasChangeListener?.onAliasChange(cardNumberAlias, expirationDateAlias)
        }
    }

    override fun onAliasChange(cardNumberAlias: String, expirationDateAlias: String) {
        this.cardNumberAlias = cardNumberAlias
        this.expirationDateAlias = expirationDateAlias
        aliasChangeListener?.onAliasChange(cardNumberAlias, expirationDateAlias)
    }

    private fun initViewPager() {
        vpVGSViewPagerActivity?.adapter = VGSShowFragmentsAdapter(this)
    }

    private inner class VGSShowFragmentsAdapter constructor(fa: FragmentActivity) :
        FragmentStateAdapter(fa) {

        override fun getItemCount(): Int = PAGES_COUNT

        override fun createFragment(position: Int): Fragment = when (position) {
            0 -> CollectFragment()
            1 -> ShowFragment()
            else -> throw IllegalArgumentException("Impalement fragment first!")
        }
    }
}

interface OnCardAliasChangeListener {

    fun onAliasChange(cardNumberAlias: String, expirationDateAlias: String)
}