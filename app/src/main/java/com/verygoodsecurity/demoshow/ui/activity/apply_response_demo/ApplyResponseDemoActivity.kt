package com.verygoodsecurity.demoshow.ui.activity.apply_response_demo

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.viewpager2.widget.ViewPager2
import com.verygoodsecurity.demoshow.R
import com.verygoodsecurity.demoshow.ui.activity.apply_response_demo.adapter.ViewPagerAdapter
import com.verygoodsecurity.demoshow.ui.activity.apply_response_demo.core.StateHandler
import com.verygoodsecurity.demoshow.ui.activity.apply_response_demo.fragments.FirsFragment
import com.verygoodsecurity.demoshow.ui.activity.apply_response_demo.fragments.SecondFragment
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse

/**
 * Fragments host activity.
 */
class ApplyResponseDemoActivity : AppCompatActivity(R.layout.activity_apply_response_demo), StateHandler {

    private lateinit var progressView: Group

    private lateinit var viewPager: ViewPager2

    override var response: VGSResponse? = null
        set(value) {
            when (value) {
                is VGSResponse.Success -> viewPager.currentItem = viewPager.currentItem.inc()
                is VGSResponse.Error -> setError(response.toString())
                else -> {}
            }
            field = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        progressView = findViewById(R.id.progressGroup)

        viewPager = findViewById(R.id.viewPager)
        viewPager.isUserInputEnabled = false
        viewPager.adapter = ViewPagerAdapter(this, listOf(FirsFragment(), SecondFragment()))
    }

    override fun setLoading(isLoading: Boolean) {
        progressView.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun setError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}