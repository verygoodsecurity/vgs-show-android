package com.verygoodsecurity.demoshow.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.verygoodsecurity.demoshow.R
import com.verygoodsecurity.demoshow.ui.MainActivity.Companion.ENVIRONMENT
import com.verygoodsecurity.demoshow.ui.activity.ApplyResponseActivity
import com.verygoodsecurity.demoshow.ui.activity.CollectShowNativeActivity
import com.verygoodsecurity.demoshow.ui.activity.ComposeActivity
import com.verygoodsecurity.demoshow.ui.activity.ComposeImageActivity
import com.verygoodsecurity.demoshow.ui.activity.ComposePdfActivity
import com.verygoodsecurity.demoshow.ui.components.DemoHeader
import com.verygoodsecurity.demoshow.ui.components.DemoListItem
import com.verygoodsecurity.demoshow.ui.components.SectionTitle
import com.verygoodsecurity.demoshow.ui.theme.DemoColors
import com.verygoodsecurity.demoshow.ui.theme.VgsDemoTheme

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        enableEdgeToEdge()
        setContent {
            VgsDemoTheme {
                HomeScreen(
                    onPdfReveal = { startActivity(Intent(this, ComposePdfActivity::class.java)) },
                    onImageReveal = { startActivity(Intent(this, ComposeImageActivity::class.java)) },
                    onStartCompose = { startActivity(Intent(this, ComposeActivity::class.java)) },
                    onApplyResponseDemo = { startActivity(Intent(this, ApplyResponseActivity::class.java)) },
                    onStartNativeViews = { startActivity(Intent(this, CollectShowNativeActivity::class.java)) },
                )
            }
        }
    }

    companion object {

        const val VAULT_ID = "<VAULT>"
        const val ENVIRONMENT = "sandbox"
    }
}

@Composable
private fun HomeScreen(
    onStartCompose: () -> Unit,
    onPdfReveal: () -> Unit,
    onImageReveal: () -> Unit,
    onApplyResponseDemo: () -> Unit,
    onStartNativeViews: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DemoColors.Background)
    ) {
        DemoHeader(
            title = "VGS",
            subtitle = "Android SDK demos",
            environment = ENVIRONMENT,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .padding(bottom = 4.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SectionTitle("Demos")

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                DemoListItem(
                    icon = painterResource(R.drawable.ic_layers_24),
                    title = "Secure data",
                    subtitle = "Collect and show compose",
                    onClick = onStartCompose,
                )
                DemoListItem(
                    icon = painterResource(R.drawable.ic_document_24),
                    title = "PDF Reveal",
                    subtitle = "Reveal a tokenized PDF",
                    onClick = onPdfReveal,
                )
                DemoListItem(
                    icon = painterResource(R.drawable.ic_image_24),
                    title = "Image Reveal",
                    subtitle = "Reveal a tokenized image",
                    onClick = onImageReveal,
                )
                DemoListItem(
                    icon = painterResource(R.drawable.ic_swap_24),
                    title = "Apply Response Demo",
                    subtitle = "Apply a Collect response to Show views",
                    onClick = onApplyResponseDemo,
                )
                DemoListItem(
                    icon = painterResource(R.drawable.ic_credit_card_24),
                    title = "Secure data",
                    subtitle = "Collect and show native views (deprecated).",
                    onClick = onStartNativeViews,
                )
            }
        }
    }
}

typealias ShowResponse = com.verygoodsecurity.vgsshow.core.network.model.VGSResponse
typealias CollectResponse = com.verygoodsecurity.vgscollect.core.model.network.VGSResponse?
typealias CollectSuccessResponse = com.verygoodsecurity.vgscollect.core.model.network.VGSResponse.SuccessResponse?
