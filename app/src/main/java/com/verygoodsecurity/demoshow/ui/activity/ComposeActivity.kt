package com.verygoodsecurity.demoshow.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.verygoodsecurity.demoshow.ui.MainActivity
import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.core.listener.VGSOnResponseListener
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse
import com.verygoodsecurity.vgsshow.widget.compose.VGSTextViewWrapper

private const val PATH = "post"

class ComposeActivity : AppCompatActivity(), VGSOnResponseListener {

    private val show: VGSShow by lazy {
        VGSShow.Builder(this, MainActivity.TENANT_ID).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Content(
                show = show,
                path = PATH
            )
        }
    }

    override fun onResponse(response: VGSResponse) {
        Toast.makeText(
            this,
            when (response) {
                is VGSResponse.Success -> "Success"
                is VGSResponse.Error -> "Error"
                else -> throw IllegalStateException()
            },
            Toast.LENGTH_SHORT
        ).show()
    }
}

@Composable
private fun Content(
    show: VGSShow?,
    path: String
) {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            VGSTextViewWrapper(
                show = show,
                contentPath = "<CONTENT_PATH>",
                modifier = Modifier.fillMaxWidth(),
                onViewCreate = {
                    it.setHint("Secured data")
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            VGSTextViewWrapper(
                show = show,
                contentPath = "<CONTENT_PATH>",
                modifier = Modifier.fillMaxWidth(),
                onViewCreate = {
                    it.setHint("Secured data")
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                modifier = Modifier.align(alignment = Alignment.End),
                contentPadding = PaddingValues(16.dp),
                onClick = {
                    VGSRequest.Builder(path, VGSHttpMethod.POST).body(
                        mapOf(
                            "<CONTENT_PATH>" to "<ALIAS>"
                        )
                    ).build()
                }
            ) {
                Text(
                    text = "SUBMIT",
                    color = Color(0xff4b5d69)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun Preview() {
    Content(
        show = null,
        path = ""
    )
}
