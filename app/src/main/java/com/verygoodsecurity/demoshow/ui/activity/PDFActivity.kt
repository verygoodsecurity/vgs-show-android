package com.verygoodsecurity.demoshow.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.verygoodsecurity.demoshow.R
import com.verygoodsecurity.demoshow.ui.CollectResponse
import com.verygoodsecurity.demoshow.ui.CollectSuccessResponse
import com.verygoodsecurity.demoshow.ui.MainActivity
import com.verygoodsecurity.demoshow.ui.MainActivity.Companion.TENANT_ID
import com.verygoodsecurity.demoshow.ui.ShowResponse
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.core.listener.VGSOnResponseListener
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.widget.view.pdf.VGSPDFView
import kotlinx.android.synthetic.main.activity_pdf.*
import org.json.JSONObject

class PDFActivity : AppCompatActivity(), VgsCollectResponseListener, VGSOnResponseListener {

    private val show: VGSShow by lazy {
        VGSShow.Builder(this, TENANT_ID).build()
    }

    private val collect: VGSCollect by lazy {
        VGSCollect(this, TENANT_ID, MainActivity.ENVIRONMENT)
    }

    private var documentAlias: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf)
        setupCollect()
        setupShow()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        collect.onActivityResult(requestCode, resultCode, data)
        updateAttachedFileName()
    }

    override fun onResponse(response: CollectResponse) {
        Log.d(PDFActivity::class.java.simpleName, response.toString())
        flProgress?.visibility = View.GONE
        (response as? CollectSuccessResponse)?.rawResponse?.let {
            try {
                JSONObject(it).getJSONObject("json").getString("pdf").run {
                    tvFileAlias.text = this
                    documentAlias = this
                }
            } catch (e: Exception) {
                Log.d(PDFActivity::class.simpleName, e.message ?: "Invalid json or data not exist!")
            }
        }
    }

    override fun onResponse(response: ShowResponse) {
        Log.d(PDFActivity::class.java.simpleName, response.toString())
        flProgress?.visibility = View.GONE
    }

    private fun setupCollect() {
        collect.addOnResponseListeners(this)

        mbAttachFile?.setOnClickListener {
            collect.getFileProvider().attachFile("pdf")
        }
        mbSubmit?.setOnClickListener {
            flProgress?.visibility = View.VISIBLE
            collect.asyncSubmit("/post", HTTPMethod.POST)
        }
    }

    private fun updateAttachedFileName() {
        collect.getFileProvider().getAttachedFiles().firstOrNull()?.let {
            tvFileName?.text = it.name
        }
    }

    private fun setupShow() {
        show.subscribe(vgsPDFView)
        show.addOnResponseListener(this)

        vgsPDFView?.onRenderStateChangeListener = object : VGSPDFView.OnRenderStateChangeListener {

            override fun onStart(pages: Int) {
                Log.d(PDFActivity::class.java.simpleName, "VGSPDFView::onStart, p = $pages")
            }

            override fun onComplete(pages: Int) {
                Log.d(PDFActivity::class.java.simpleName, "VGSPDFView::onComplete, p = $pages")
            }

            override fun onError(t: Throwable) {
                Log.d(PDFActivity::class.java.simpleName, "VGSPDFView::onError, t = $t")
            }
        }

        mbReveal?.setOnClickListener {
            flProgress?.visibility = View.VISIBLE
            show.requestAsync(
                VGSRequest.Builder("post", VGSHttpMethod.POST)
                    .body(mapOf("payment_card_pdf" to documentAlias))
                    .build()
            )
        }
        mbShare?.setOnClickListener {
            vgsPDFView.sharePDF()
        }
    }
}
