package com.verygoodsecurity.demoshow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import com.verygoodsecurity.vgsshow.core.VGSShow
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val show = VGSShow()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupTextView()

        btn?.setOnClickListener {
            revealData()
        }
        Testbtn?.setOnClickListener {
            testfunc()
        }
    }

    private fun setupTextView() {
        show.bindView(vgsSecureView)

        vgsSecureView.setOnClickListener {
            Log.e("test", "CLICK $it")
        }
    }

    private fun revealData() {
        parentV.requestLayout()
        vgsSecureView.requestLayout()
        vgsSecureView.invalidate()

        show.request("fieldName", "123")
        show.request("fieldName_2", "123")
        show.request("someName", "123")
    }

    fun testfunc() {
        vgsSecureView?.requestFocus()
        for(ch in 0..vgsSecureView.childCount) {
            val child = vgsSecureView.getChildAt(ch)

            if(child is TextView) {
                Log.e("test", "getText() = ${child.text}")
                Log.e("test", "getEditableText() = ${child.editableText}")
                child.addTextChangedListener {
                    Log.e("test", "TEXT_WATHCER $it")
                }
            }
        }
    }
}