package com.verygoodsecurity.demoshow.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.verygoodsecurity.demoshow.R
import com.verygoodsecurity.demoshow.ui.CollectResponse
import com.verygoodsecurity.demoshow.ui.CollectSuccessResponse
import com.verygoodsecurity.demoshow.ui.MainActivity
import com.verygoodsecurity.demoshow.ui.ShowResponse
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.core.listener.VGSOnResponseListener
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.widget.VGSImageView
import org.json.JSONObject

class ImageActivity : AppCompatActivity(R.layout.activity_image),
    VgsCollectResponseListener, VGSOnResponseListener {

    private val show: VGSShow by lazy {
        VGSShow.Builder(this, MainActivity.TENANT_ID).build()
    }

    private val collect: VGSCollect by lazy {
        VGSCollect(this, MainActivity.TENANT_ID, MainActivity.ENVIRONMENT)
    }

    private val vgsImageView: VGSImageView by lazy { findViewById(R.id.vgsImageView) }
    private val flProgress: FrameLayout by lazy { findViewById(R.id.flProgress) }
    private val tvFileAlias: TextView by lazy { findViewById(R.id.tvFileAlias) }
    private val tvFileName: TextView by lazy { findViewById(R.id.tvFileName) }
    private val mbAttachFile: MaterialButton by lazy { findViewById(R.id.mbAttachFile) }
    private val mbSubmit: MaterialButton by lazy { findViewById(R.id.mbSubmit) }
    private val mbReveal: MaterialButton by lazy { findViewById(R.id.mbReveal) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupCollect()
        setupShow()
    }

    @Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        collect.onActivityResult(requestCode, resultCode, data)
        updateAttachedFileName()
    }

    override fun onResponse(response: CollectResponse) {
        Log.d(PDFActivity::class.java.simpleName, response.toString())
        flProgress.visibility = View.GONE
        (response as? CollectSuccessResponse)?.rawResponse?.let {
            try {
                JSONObject(it).getJSONObject("json").getString("image").run {
                    tvFileAlias.text = this
                }
            } catch (e: Exception) {
                Log.d(ImageActivity::class.simpleName, e.message ?: "Invalid json or data not exist!")
            }
        }
    }

    override fun onResponse(response: ShowResponse) {
        Log.d(PDFActivity::class.java.simpleName, response.toString())
        flProgress.visibility = View.GONE
    }

    private fun setupCollect() {
        collect.addOnResponseListeners(this)

        mbAttachFile.setOnClickListener {
            collect.getFileProvider().attachFile(this, "image")
        }
        mbSubmit.setOnClickListener {
            flProgress.visibility = View.VISIBLE
            collect.asyncSubmit("/post", HTTPMethod.POST)
        }
    }

    private fun setupShow() {
        show.subscribe(vgsImageView)
        show.addOnResponseListener(this)

        mbReveal.setOnClickListener {
            flProgress.visibility = View.VISIBLE
            show.requestAsync(
                VGSRequest.Builder("post", VGSHttpMethod.POST)
                    .body(mapOf("revealed_image" to (tvFileAlias.text ?: "")))
                    .build()
            )
        }
    }

    private fun updateAttachedFileName() {
        collect.getFileProvider().getAttachedFiles().firstOrNull()?.let {
            tvFileName.text = it.name
        }
    }
}