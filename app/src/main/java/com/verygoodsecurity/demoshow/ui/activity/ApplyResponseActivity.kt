package com.verygoodsecurity.demoshow.ui.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.verygoodsecurity.demoshow.R
import com.verygoodsecurity.demoshow.ui.CollectResponse
import com.verygoodsecurity.demoshow.ui.CollectSuccessResponse
import com.verygoodsecurity.demoshow.ui.MainActivity
import com.verygoodsecurity.demoshow.ui.components.ActionButton
import com.verygoodsecurity.demoshow.ui.components.DemoCard
import com.verygoodsecurity.demoshow.ui.components.DemoHeader
import com.verygoodsecurity.demoshow.ui.components.ErrorText
import com.verygoodsecurity.demoshow.ui.components.FieldIcon
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
import com.verygoodsecurity.vgscollect.widget.compose.material.VgsOutlinedTextField
import com.verygoodsecurity.vgscollect.widget.compose.state.rememberVgsTextFieldState
import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.core.listener.VGSOnResponseListener
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse
import com.verygoodsecurity.vgsshow.widget.compose.material.VgsText
import com.verygoodsecurity.vgsshow.widget.compose.state.rememberVgsTextState
import org.json.JSONObject

private const val FIELD_NAME = "sensitive_data"
private const val PATH = "/post"
private const val REVEAL_BODY_KEY = "raw_sensitive_data"
private const val REVEAL_CONTENT_PATH = "json.raw_sensitive_data"

/**
 * Demonstrates [VGSShow.applyResponse]: screen one creates an alias and reveals it as usual, then
 * screen two — with its own, otherwise untouched [VGSShow] instance — renders that same revealed
 * value by having the stored [VGSResponse.Success] applied to it, instead of making its own reveal
 * request.
 */
class ApplyResponseActivity : AppCompatActivity() {

    private val collect: VGSCollect by lazy {
        VGSCollect(this, MainActivity.VAULT_ID, MainActivity.ENVIRONMENT)
    }

    private val revealShow: VGSShow by lazy {
        VGSShow(this, MainActivity.VAULT_ID, MainActivity.ENVIRONMENT)
    }

    private val appliedShow: VGSShow by lazy {
        VGSShow(this, MainActivity.VAULT_ID, MainActivity.ENVIRONMENT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Content(
                collect = collect,
                revealShow = revealShow,
                appliedShow = appliedShow,
                onBack = { finish() },
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        collect.onDestroy()
        revealShow.onDestroy()
        appliedShow.onDestroy()
    }
}

@Composable
private fun Content(
    collect: VGSCollect,
    revealShow: VGSShow,
    appliedShow: VGSShow,
    onBack: () -> Unit,
) {
    VgsDemoTheme {
        var appliedResponse by remember { mutableStateOf<VGSResponse.Success?>(null) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DemoColors.Background)
        ) {
            DemoHeader(
                title = "Apply response",
                subtitle = "Apply a Collect response to Show views",
                environment = MainActivity.ENVIRONMENT,
                onBack = { if (appliedResponse != null) appliedResponse = null else onBack() },
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .navigationBarsPadding()
                    .padding(DemoSpacing.screenPadding),
                verticalArrangement = Arrangement.spacedBy(DemoSpacing.sectionGap),
            ) {
                val response = appliedResponse
                if (response == null) {
                    CreateAndRevealStep(
                        collect = collect,
                        show = revealShow,
                        onRevealed = { appliedResponse = it },
                    )
                } else {
                    AppliedStep(
                        show = appliedShow,
                        response = response,
                    )
                }
            }
        }
    }
}

@Composable
private fun CreateAndRevealStep(
    collect: VGSCollect,
    show: VGSShow,
    onRevealed: (VGSResponse.Success) -> Unit,
) {
    var fieldState by rememberVgsTextFieldState(collect = collect, fieldName = FIELD_NAME)
    var isSubmitting by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    DisposableEffect(collect, show) {
        val listener = object : VgsCollectResponseListener {
            override fun onResponse(response: CollectResponse) {
                when (response) {
                    is CollectSuccessResponse -> {
                        val alias = response?.body
                            ?.let(::JSONObject)
                            ?.optJSONObject("json")
                            ?.optString(FIELD_NAME)
                        if (alias.isNullOrEmpty()) {
                            isSubmitting = false
                            error = "Create alias failed: unexpected server response"
                        } else {
                            error = null
                            show.requestAsync(PATH, VGSHttpMethod.POST, mapOf(REVEAL_BODY_KEY to alias))
                        }
                    }

                    else -> {
                        isSubmitting = false
                        error = "Create alias failed: unknown error"
                    }
                }
            }
        }
        collect.addOnResponseListeners(listener)
        onDispose { collect.removeOnResponseListener(listener) }
    }

    DisposableEffect(show) {
        val listener = object : VGSOnResponseListener {
            override fun onResponse(response: VGSResponse) {
                isSubmitting = false
                when (response) {
                    is VGSResponse.Success -> onRevealed(response)
                    is VGSResponse.Error -> error =
                        "Reveal failed: ${response.message} (code ${response.code})"
                }
            }
        }
        show.addOnResponseListener(listener)
        onDispose { show.removeOnResponseListener(listener) }
    }

    Column(verticalArrangement = Arrangement.spacedBy(DemoSpacing.md)) {
        SectionTitle("Create alias & reveal")

        DemoCard {
            Column(verticalArrangement = Arrangement.spacedBy(DemoSpacing.sm)) {
                FieldLabel("Sensitive data")
                VgsOutlinedTextField(
                    state = fieldState,
                    onStateChange = { fieldState = it },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = isSubmitting,
                    isError = !fieldState.isEmpty && !fieldState.isValid,
                    textStyle = DemoTextStyles.fieldText,
                    placeholder = { Text("Enter a value to tokenize") },
                    leadingIcon = { FieldIcon(R.drawable.ic_layers_24) },
                    shape = DemoShapes.field,
                    colors = fieldColors(),
                    singleLine = true,
                )
            }

            if (error != null) {
                ErrorText(error ?: "")
            }

            ActionButton(
                text = "Create alias & reveal",
                loadingText = "Revealing…",
                iconRes = R.drawable.ic_swap_24,
                enabled = fieldState.isValid && !isSubmitting,
                loading = isSubmitting,
                backgroundColor = DemoColors.Primary,
                contentColor = Color.White,
                onClick = {
                    error = null
                    isSubmitting = true
                    collect.asyncSubmit(PATH, HTTPMethod.POST, listOf(fieldState))
                },
            )
        }
    }
}

@Composable
private fun AppliedStep(show: VGSShow, response: VGSResponse.Success) {
    val textState = rememberVgsTextState(show = show, contentPath = REVEAL_CONTENT_PATH, ignore = false)

    // This VGSShow instance never makes a reveal request of its own — applyResponse() pushes the
    // response obtained on the previous step straight into this screen's subscribed state.
    LaunchedEffect(show, response) {
        show.applyResponse(response)
    }

    Column(verticalArrangement = Arrangement.spacedBy(DemoSpacing.md)) {
        SectionTitle("Applied on another screen")
        Text(
            text = "This screen owns a separate VGSShow instance. The value below was never " +
                    "requested over the network here — VGSShow.applyResponse() applied the " +
                    "response from the previous step instead.",
            color = DemoColors.TextMuted,
            style = DemoTextStyles.rowSubtitle,
        )

        DemoCard {
            FieldLabel("Sensitive data")
            VgsText(state = textState.value, style = DemoTextStyles.valueText)
        }
    }
}
