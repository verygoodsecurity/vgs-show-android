package com.verygoodsecurity.demoshow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.verygoodsecurity.vgsshow.core.VGSShow
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val show = VGSShow()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        show.bindView(vgsSecureView)

        btn?.setOnClickListener {
            revealData()
        }
    }

    private fun revealData() {
        show.request("fieldName", "123")
        show.request("fieldName_2", "123")
        show.request("someName", "123")
    }

}