package com.verygoodsecurity.demoshow.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.verygoodsecurity.demoshow.R
import com.verygoodsecurity.demoshow.ui.CollectResponse
import com.verygoodsecurity.demoshow.ui.CollectSuccessResponse
import com.verygoodsecurity.demoshow.ui.MainActivity
import com.verygoodsecurity.demoshow.ui.components.ActionButton
import com.verygoodsecurity.demoshow.ui.components.DemoCard
import com.verygoodsecurity.demoshow.ui.components.DemoHeader
import com.verygoodsecurity.demoshow.ui.components.FieldLabel
import com.verygoodsecurity.demoshow.ui.components.SectionTitle
import com.verygoodsecurity.demoshow.ui.components.fieldColors
import com.verygoodsecurity.demoshow.ui.theme.DemoColors
import com.verygoodsecurity.demoshow.ui.theme.DemoShapes
import com.verygoodsecurity.demoshow.ui.theme.DemoSpacing
import com.verygoodsecurity.demoshow.ui.theme.DemoTextStyles
import com.verygoodsecurity.demoshow.ui.theme.VgsDemoTheme
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.core.listener.VGSOnResponseListener
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse
import com.verygoodsecurity.vgsshow.widget.compose.VgsImage
import com.verygoodsecurity.vgsshow.widget.compose.state.rememberVgsImageState
import org.json.JSONObject

private const val IMAGE_FIELD_NAME = "image"
private const val REVEAL_CONTENT_PATH = "json.revealed_image"
private const val REVEAL_BODY_KEY = "revealed_image"

class ComposeImageActivity : AppCompatActivity() {

    private val show: VGSShow by lazy {
        VGSShow.Builder(this, MainActivity.VAULT_ID).build()
    }

    private val collect: VGSCollect by lazy {
        VGSCollect(this, MainActivity.VAULT_ID, MainActivity.ENVIRONMENT)
    }

    private val attachedFileName = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Content(
                show = show,
                collect = collect,
                attachedFileName = attachedFileName.value,
                onAttachFile = { collect.getFileProvider().attachFile(this, IMAGE_FIELD_NAME) },
                onBack = { finish() },
            )
        }
    }

    @Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        collect.onActivityResult(requestCode, resultCode, data)
        attachedFileName.value = collect.getFileProvider().getAttachedFiles().firstOrNull()?.name
    }

    override fun onDestroy() {
        super.onDestroy()
        collect.onDestroy()
        show.onDestroy()
    }
}

@Composable
private fun Content(
    show: VGSShow,
    collect: VGSCollect,
    attachedFileName: String?,
    onAttachFile: () -> Unit,
    onBack: () -> Unit,
) {
    VgsDemoTheme {
        var name by remember { mutableStateOf("") }
        var isSubmitting by remember { mutableStateOf(false) }
        var collectError by remember { mutableStateOf<String?>(null) }
        var alias by remember { mutableStateOf<String?>(null) }
        var isRevealing by remember { mutableStateOf(false) }
        var isRevealed by remember { mutableStateOf(false) }
        var showError by remember { mutableStateOf<String?>(null) }

        DisposableEffect(collect) {
            val listener = object : VgsCollectResponseListener {
                override fun onResponse(response: CollectResponse) {
                    isSubmitting = false
                    when (response) {
                        is CollectSuccessResponse -> {
                            val imageAlias = response?.rawResponse
                                ?.let(::JSONObject)
                                ?.optJSONObject("json")
                                ?.optString(IMAGE_FIELD_NAME)
                            if (imageAlias.isNullOrEmpty()) {
                                collectError = "Submit failed: unexpected server response"
                            } else {
                                collectError = null
                                alias = imageAlias
                                isRevealed = false
                            }
                        }

                        else -> collectError = "Submit failed: unknown error"
                    }
                }
            }
            collect.addOnResponseListeners(listener)
            onDispose { collect.removeOnResponseListener(listener) }
        }

        DisposableEffect(show) {
            val listener = object : VGSOnResponseListener {
                override fun onResponse(response: VGSResponse) {
                    when (response) {
                        is VGSResponse.Success -> isRevealed = true
                        is VGSResponse.Error -> showError =
                            "Reveal failed: ${response.message} (code ${response.code})"
                    }
                }

            }
            show.addOnResponseListener(listener)
            onDispose { show.removeOnResponseListener(listener) }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DemoColors.Background)
        ) {
            DemoHeader(
                title = "Image reveal",
                subtitle = "Collect + Show demo",
                environment = MainActivity.ENVIRONMENT,
                onBack = onBack,
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .navigationBarsPadding()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(DemoSpacing.sectionGap),
            ) {
                CollectSection(
                    name = name,
                    onNameChange = { name = it },
                    attachedFileName = attachedFileName,
                    onAttachFile = onAttachFile,
                    alias = alias,
                    isSubmitting = isSubmitting,
                    error = collectError,
                    onSubmit = {
                        collectError = null
                        isSubmitting = true
                        collect.asyncSubmit("/post", HTTPMethod.POST)
                    },
                )

                ShowSection(
                    show = show,
                    hasAlias = alias != null,
                    isRevealing = isRevealing,
                    isRevealed = isRevealed,
                    error = showError,
                    onToggle = {
                        val currentAlias = alias
                        when {
                            isRevealed -> isRevealed = false
                            currentAlias != null -> {
                                showError = null
                                isRevealing = true
                                show.requestAsync(
                                    VGSRequest.Builder("post", VGSHttpMethod.POST)
                                        .body(mapOf(REVEAL_BODY_KEY to currentAlias))
                                        .build()
                                )
                            }
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun CollectSection(
    name: String,
    onNameChange: (String) -> Unit,
    attachedFileName: String?,
    onAttachFile: () -> Unit,
    alias: String?,
    isSubmitting: Boolean,
    error: String?,
    onSubmit: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionTitle("Collect")

        DemoCard {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                FieldLabel("Name")
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = isSubmitting,
                    placeholder = { Text("Enter a name") },
                    textStyle = DemoTextStyles.fieldText,
                    shape = DemoShapes.field,
                    colors = fieldColors(),
                    singleLine = true,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                FieldLabel("File")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, DemoColors.InputBorder, DemoShapes.field)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(DemoColors.AccentTint, DemoShapes.icon),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_image_24),
                            contentDescription = null,
                            tint = DemoColors.Primary,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        if (attachedFileName != null) {
                            Text(
                                text = attachedFileName,
                                color = DemoColors.Primary,
                                style = DemoTextStyles.rowTitle
                            )
                            Text(
                                text = "Ready to submit",
                                color = DemoColors.TextMuted,
                                style = DemoTextStyles.rowSubtitle
                            )
                        } else {
                            Text(
                                text = "No file attached",
                                color = DemoColors.Primary,
                                style = DemoTextStyles.rowTitle
                            )
                            Text(
                                text = "JPG or PNG",
                                color = DemoColors.TextMuted,
                                style = DemoTextStyles.rowSubtitle
                            )
                        }
                    }
                    OutlinedButton(
                        onClick = onAttachFile,
                        enabled = !isSubmitting,
                        shape = DemoShapes.chip,
                    ) {
                        Text(if (attachedFileName != null) "Change file" else "Attach file")
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                FieldLabel("Alias")
                if (alias != null) {
                    Box(
                        modifier = Modifier
                            .background(DemoColors.AliasChipBackground, DemoShapes.chip)
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = alias,
                            color = DemoColors.AliasChipText,
                            style = DemoTextStyles.aliasChipText
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .border(1.dp, DemoColors.InputBorder, DemoShapes.chip)
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "Returned after submit",
                            color = DemoColors.TextMuted,
                            style = DemoTextStyles.aliasChipText
                        )
                    }
                }
            }

            if (error != null) {
                Text(
                    text = error,
                    color = DemoColors.ErrorColor,
                    style = DemoTextStyles.rowSubtitle
                )
            }

            ActionButton(
                text = "Submit",
                loadingText = "Submitting…",
                iconRes = R.drawable.ic_shield_check_24,
                enabled = attachedFileName != null && !isSubmitting,
                loading = isSubmitting,
                backgroundColor = DemoColors.Primary,
                contentColor = Color.White,
                onClick = onSubmit,
            )
        }
    }
}

@Composable
private fun ShowSection(
    show: VGSShow,
    hasAlias: Boolean,
    isRevealing: Boolean,
    isRevealed: Boolean,
    error: String?,
    onToggle: () -> Unit,
) {
    val imageState = rememberVgsImageState(
        show = show,
        contentPath = REVEAL_CONTENT_PATH,
        ignore = false,
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionTitle("Show")

        DemoCard {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(DemoColors.CardBorder.copy(alpha = 0.3f), DemoShapes.icon)
                    .border(1.dp, DemoColors.CardBorder, DemoShapes.icon),
                contentAlignment = Alignment.Center,
            ) {
                if (isRevealed) {
                    VgsImage(
                        state = imageState.value,
                        contentDescription = "Revealed image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(horizontal = 24.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(DemoColors.AccentTint, DemoShapes.icon),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_image_24),
                                contentDescription = null,
                                tint = DemoColors.Primary,
                                modifier = Modifier.size(22.dp),
                            )
                        }
                        Text(
                            text = if (hasAlias) {
                                "Image is protected. Tap Reveal to load it."
                            } else {
                                "Revealed image appears here"
                            },
                            color = DemoColors.TextMuted,
                            style = DemoTextStyles.rowSubtitle,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }

            ActionButton(
                text = if (isRevealed) "Hide" else "Reveal",
                loadingText = "Revealing…",
                iconRes = if (isRevealed) R.drawable.ic_eye_off_24 else R.drawable.ic_eye_24,
                enabled = !isRevealing && (isRevealed || hasAlias),
                loading = isRevealing,
                backgroundColor = DemoColors.Accent,
                contentColor = DemoColors.Primary,
                onClick = onToggle,
            )

            if (error != null) {
                Text(
                    text = error,
                    color = DemoColors.ErrorColor,
                    style = DemoTextStyles.rowSubtitle
                )
            }
        }
    }
}
