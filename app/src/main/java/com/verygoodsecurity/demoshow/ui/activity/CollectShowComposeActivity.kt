package com.verygoodsecurity.demoshow.ui.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import com.verygoodsecurity.demoshow.ui.theme.DemoTextStyles
import com.verygoodsecurity.demoshow.ui.theme.VgsDemoTheme
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.widget.compose.material.VgsCardNumberOutlinedTextField
import com.verygoodsecurity.vgscollect.widget.compose.material.VgsExpiryOutlinedTextField
import com.verygoodsecurity.vgscollect.widget.compose.state.core.BaseFieldState
import com.verygoodsecurity.vgscollect.widget.compose.state.rememberVgsCardNumberTextFieldState
import com.verygoodsecurity.vgscollect.widget.compose.state.rememberVgsExpiryTextFieldState
import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.core.listener.VGSOnResponseListener
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.core.network.model.VGSResponse
import com.verygoodsecurity.vgsshow.widget.compose.VgsText
import com.verygoodsecurity.vgsshow.widget.compose.state.VgsTextState
import com.verygoodsecurity.vgsshow.widget.compose.state.rememberVgsTextState
import org.json.JSONObject
import com.verygoodsecurity.vgscollect.core.model.network.VGSRequest as CollectVGSRequest
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse as CollectVGSResponse

private const val NUMBER_FIELD_NAME = "cardNumber"
private const val EXP_DATE_FIELD_NAME = "expDate"
private const val MASKED_NUMBER = "•••• •••• •••• ••••"
private const val MASKED_EXP_DATE = "••/••"

/**
 * One created-alias pair. Content paths and reveal body keys are fixed strings matching this
 * vault's route (not namespaced per-submission like a generic "/post" echo would allow), so only
 * one card's data can be live in these [VgsTextState]s at a time — submitting a new card replaces
 * the previous one instead of adding to a list.
 */
private data class SubmittedCard(
    val numberAlias: String,
    val expDateAlias: String,
) {

    val numberContentPath: String get() = "json.payment_card_number"
    val expDateContentPath: String get() = "json.payment_card_expiration_date"
    val numberBodyKey: String get() = "payment_card_number"
    val expDateBodyKey: String get() = "payment_card_expiration_date"
}

class ComposeActivity : AppCompatActivity() {

    private val show: VGSShow by lazy {
        VGSShow(this, MainActivity.VAULT_ID, MainActivity.ENVIRONMENT)
    }

    private val collect: VGSCollect by lazy {
        VGSCollect(this, MainActivity.VAULT_ID, MainActivity.ENVIRONMENT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Content(show = show, collect = collect, onBack = { finish() })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        collect.onDestroy()
        show.onDestroy()
    }
}

@Composable
private fun Content(show: VGSShow, collect: VGSCollect, onBack: () -> Unit) {
    VgsDemoTheme {
        var isSubmitting by remember { mutableStateOf(false) }
        var collectError by remember { mutableStateOf<String?>(null) }
        var card by remember { mutableStateOf<SubmittedCard?>(null) }
        var isRevealing by remember { mutableStateOf(false) }
        var isRevealed by remember { mutableStateOf(false) }
        var showError by remember { mutableStateOf<String?>(null) }

        DisposableEffect(collect) {
            val listener = object : VgsCollectResponseListener {
                override fun onResponse(response: CollectResponse) {
                    isSubmitting = false
                    when (response) {
                        is CollectSuccessResponse -> {
                            val json = response?.body?.let(::JSONObject)?.optJSONObject("json")
                            val numberAlias = json?.optString(NUMBER_FIELD_NAME)
                            val expDateAlias = json?.optString(EXP_DATE_FIELD_NAME)
                            if (numberAlias.isNullOrEmpty() || expDateAlias.isNullOrEmpty()) {
                                collectError = "Create alias failed: unexpected server response"
                            } else {
                                collectError = null
                                card = SubmittedCard(numberAlias, expDateAlias)
                                isRevealed = false
                            }
                        }

                        is CollectVGSResponse.ErrorResponse -> {
                            collectError = "Create alias failed: ${response.localizeMessage} " +
                                    "(code ${response.errorCode})"
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
                    isRevealing = false
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
                title = "VGS",
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
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                CollectSection(
                    collect = collect,
                    isSubmitting = isSubmitting,
                    error = collectError,
                    onSubmit = { states ->
                        collectError = null
                        isSubmitting = true
                        collect.asyncSubmit(
                            CollectVGSRequest.VGSRequestBuilder().setPath("/post").build(),
                            states,
                        )
                    },
                )

                ShowSection(
                    show = show,
                    card = card,
                    isRevealing = isRevealing,
                    isRevealed = isRevealed,
                    error = showError,
                    onToggle = { alreadyFetched ->
                        val currentCard = card
                        when {
                            currentCard == null -> Unit
                            isRevealed -> isRevealed = false
                            alreadyFetched -> isRevealed = true
                            else -> {
                                showError = null
                                isRevealing = true
                                show.requestAsync(
                                    VGSRequest.Builder("post", VGSHttpMethod.POST).body(
                                        mapOf(
                                            currentCard.numberBodyKey to currentCard.numberAlias,
                                            currentCard.expDateBodyKey to currentCard.expDateAlias,
                                        )
                                    ).build()
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
    collect: VGSCollect,
    isSubmitting: Boolean,
    error: String?,
    onSubmit: (List<BaseFieldState>) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionTitle("Collect")

        DemoCard {
            var cardNumberState by rememberVgsCardNumberTextFieldState(
                collect = collect,
                fieldName = NUMBER_FIELD_NAME,
            )
            var expDateState by rememberVgsExpiryTextFieldState(
                collect = collect,
                fieldName = EXP_DATE_FIELD_NAME,
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FieldLabel("Card number")
                VgsCardNumberOutlinedTextField(
                    state = cardNumberState,
                    onStateChange = { cardNumberState = it },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = isSubmitting,
                    isError = !cardNumberState.isEmpty && !cardNumberState.isValid,
                    textStyle = DemoTextStyles.fieldText,
                    placeholder = { Text("1234 5678 9012 3456") },
                    leadingIcon = { FieldIcon(R.drawable.ic_credit_card_24) },
                    shape = DemoShapes.field,
                    colors = fieldColors(),
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FieldLabel("Expiration date")
                VgsExpiryOutlinedTextField(
                    state = expDateState,
                    onStateChange = { expDateState = it },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = isSubmitting,
                    isError = !expDateState.isEmpty && !expDateState.isValid,
                    textStyle = DemoTextStyles.fieldText,
                    placeholder = { Text("MM/YY") },
                    leadingIcon = { FieldIcon(R.drawable.ic_calendar_24) },
                    shape = DemoShapes.field,
                    colors = fieldColors(),
                )
            }

            if (error != null) {
                ErrorText(error)
            }

            ActionButton(
                text = "Create alias",
                loadingText = "Creating alias…",
                iconRes = R.drawable.ic_shield_check_24,
                enabled = cardNumberState.isValid && expDateState.isValid && !isSubmitting,
                loading = isSubmitting,
                backgroundColor = DemoColors.Primary,
                contentColor = Color.White,
                onClick = { onSubmit(listOf(cardNumberState, expDateState)) },
            )
        }
    }
}

@Composable
private fun ShowSection(
    show: VGSShow,
    card: SubmittedCard?,
    isRevealing: Boolean,
    isRevealed: Boolean,
    error: String?,
    onToggle: (alreadyFetched: Boolean) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionTitle("Show")

        if (card == null) {
            EmptyState()
        } else {
            ShowCard(
                show = show,
                card = card,
                isRevealing = isRevealing,
                isRevealed = isRevealed,
                onToggle = onToggle,
            )
        }

        if (error != null) {
            ErrorText(error)
        }
    }
}

@Composable
private fun ShowCard(
    show: VGSShow,
    card: SubmittedCard,
    isRevealing: Boolean,
    isRevealed: Boolean,
    onToggle: (alreadyFetched: Boolean) -> Unit,
) {
    val numberState =
        rememberVgsTextState(show = show, contentPath = card.numberContentPath, ignore = false)
    val expDateState =
        rememberVgsTextState(show = show, contentPath = card.expDateContentPath, ignore = false)
    val alreadyFetched = !numberState.value.isEmpty && !expDateState.value.isEmpty

    DemoCard {
        ShowField(
            label = "Card number",
            state = numberState,
            isRevealed = isRevealed,
            masked = MASKED_NUMBER,
            alias = card.numberAlias,
        )
        ShowField(
            label = "Expiration date",
            state = expDateState,
            isRevealed = isRevealed,
            masked = MASKED_EXP_DATE,
            alias = card.expDateAlias,
        )
        ActionButton(
            text = if (isRevealed) "Hide" else "Reveal",
            loadingText = "Revealing…",
            iconRes = if (isRevealed) R.drawable.ic_eye_off_24 else R.drawable.ic_eye_24,
            enabled = !isRevealing,
            loading = isRevealing,
            backgroundColor = DemoColors.Accent,
            contentColor = DemoColors.Primary,
            onClick = { onToggle(alreadyFetched) },
        )
    }
}

@Composable
private fun ShowField(
    label: String,
    state: MutableState<VgsTextState>,
    isRevealed: Boolean,
    masked: String,
    alias: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        FieldLabel(label)
        if (isRevealed) {
            VgsText(state = state.value, style = DemoTextStyles.valueText)
        } else {
            Text(text = masked, style = DemoTextStyles.valueText)
        }
        Box(
            modifier = Modifier
                .background(DemoColors.AliasChipBackground, DemoShapes.chip)
                .padding(horizontal = 8.dp, vertical = 5.dp)
        ) {
            Text(
                text = alias,
                color = DemoColors.AliasChipText,
                style = DemoTextStyles.aliasChipText,
            )
        }
    }
}

@Composable
private fun EmptyState() {
    val dash = with(LocalDensity.current) { 6.dp.toPx() }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, DemoShapes.card)
            .drawBehind {
                val inset = 0.5.dp.toPx()
                drawRoundRect(
                    color = DemoColors.InputBorder,
                    topLeft = Offset(inset, inset),
                    size = Size(size.width - inset * 2, size.height - inset * 2),
                    cornerRadius = CornerRadius(16.dp.toPx()),
                    style = Stroke(
                        width = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(dash, dash)),
                    ),
                )
            }
            .padding(horizontal = 20.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = DemoColors.AccentTint,
                    shape = DemoShapes.icon,
                )
                .padding(8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_credit_card_24),
                contentDescription = null,
                tint = DemoColors.Primary,
                modifier = Modifier.size(24.dp),
            )
        }
        Text(
            text = "No aliases yet",
            color = DemoColors.Primary,
            style = DemoTextStyles.emptyTitle,
        )
        Text(
            text = "Enter a card above and tap Create alias.",
            color = DemoColors.TextMuted,
            style = DemoTextStyles.emptyBody,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF3F6FC)
@Composable
private fun Preview() {
    Content(
        show = VGSShow(LocalContext.current, "", ""),
        collect = VGSCollect(LocalContext.current, "", ""),
        onBack = {},
    )
}
