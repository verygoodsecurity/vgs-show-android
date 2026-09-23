package com.verygoodsecurity.demoshow.ui.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.verygoodsecurity.demoshow.R
import com.verygoodsecurity.demoshow.ui.CollectResponse
import com.verygoodsecurity.demoshow.ui.CollectSuccessResponse
import com.verygoodsecurity.demoshow.ui.MainActivity
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
import com.verygoodsecurity.vgsshow.widget.compose.VgsShowText
import com.verygoodsecurity.vgsshow.widget.compose.rememberVgsShowTextState
import com.verygoodsecurity.vgsshow.widget.compose.state.VgsShowTextState
import org.json.JSONObject
import com.verygoodsecurity.vgscollect.core.model.network.VGSRequest as CollectVGSRequest
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse as CollectVGSResponse

private const val NUMBER_FIELD_NAME = "cardNumber"
private const val EXP_DATE_FIELD_NAME = "expDate"
private const val MASKED_NUMBER = "•••• •••• •••• ••••"
private const val MASKED_EXP_DATE = "••/••"

private val Primary = Color(0xFF002270)
private val Accent = Color(0xFF4CBDF2)
private val Secondary = Color(0xFF8779D5)
private val Background = Color(0xFFF3F6FC)
private val TextMuted = Color(0xFF5B6784)
private val CardBorder = Color(0xFFDCE3F0)
private val InputBorder = Color(0xFFC9D3E6)
private val PlaceholderColor = Color(0xFF9AA5BD)
private val AliasChipBackground = Secondary.copy(alpha = 0.16f)
private val AliasChipText = Color(0xFF444EA3)
private val ErrorColor = Color(0xFFB00020)

/**
 * One created-alias pair, rendered as its own card. Content paths and body keys are namespaced
 * by [id] so revealing one card never updates another card's [VgsShowTextState] — the demo's
 * "/post" endpoint echoes back whatever keys were sent under "json".
 */
private data class SubmittedCard(
    val id: Int,
    val numberAlias: String,
    val expDateAlias: String,
) {
    val numberContentPath: String get() = "json.card_${id}_number"
    val expDateContentPath: String get() = "json.card_${id}_expDate"
    val numberBodyKey: String get() = "card_${id}_number"
    val expDateBodyKey: String get() = "card_${id}_expDate"
}

class ComposeActivity : AppCompatActivity() {

    private val show: VGSShow by lazy {
        VGSShow.Builder(this, MainActivity.TENANT_ID).build()
    }

    private val collect: VGSCollect by lazy {
        VGSCollect(this, MainActivity.TENANT_ID, MainActivity.ENVIRONMENT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Content(show = show, collect = collect)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        collect.onDestroy()
        show.onDestroy()
    }
}

@Composable
private fun Content(show: VGSShow?, collect: VGSCollect) {
    MaterialTheme {
        var isSubmitting by remember { mutableStateOf(false) }
        var collectError by remember { mutableStateOf<String?>(null) }
        var cards by remember { mutableStateOf<List<SubmittedCard>>(emptyList()) }
        // Only one reveal request is in flight at a time: VGSOnResponseListener can't tell which
        // request a response belongs to, so concurrent reveals couldn't be attributed to a card.
        var revealingCardId by remember { mutableStateOf<Int?>(null) }
        var revealedCardIds by remember { mutableStateOf<Set<Int>>(emptySet()) }
        var showError by remember { mutableStateOf<String?>(null) }

        DisposableEffect(collect) {
            val listener = object : VgsCollectResponseListener {
                override fun onResponse(response: CollectResponse) {
                    isSubmitting = false
                    when (response) {
                        is CollectSuccessResponse -> {
                            val json = response?.rawResponse
                                ?.let(::JSONObject)
                                ?.optJSONObject("json")
                            val numberAlias = json?.optString(NUMBER_FIELD_NAME)
                            val expDateAlias = json?.optString(EXP_DATE_FIELD_NAME)
                            if (numberAlias.isNullOrEmpty() || expDateAlias.isNullOrEmpty()) {
                                collectError = "Create alias failed: unexpected server response"
                            } else {
                                collectError = null
                                cards = listOf(
                                    SubmittedCard(
                                        cards.size,
                                        numberAlias,
                                        expDateAlias
                                    )
                                ) + cards
                            }
                        }

                        is CollectVGSResponse.ErrorResponse -> {
                            collectError = "Create alias failed: ${response.localizeMessage} " +
                                    "(code ${response.errorCode})"
                        }

                        else -> collectError = "Create alias failed: unknown error"
                    }
                }
            }
            collect.addOnResponseListeners(listener)
            onDispose { collect.removeOnResponseListener(listener) }
        }

        DisposableEffect(show) {
            val listener = object : VGSOnResponseListener {
                override fun onResponse(response: VGSResponse) {
                    val cardId = revealingCardId
                    revealingCardId = null
                    when (response) {
                        is VGSResponse.Success -> if (cardId != null) revealedCardIds =
                            revealedCardIds + cardId

                        is VGSResponse.Error -> showError =
                            "Reveal failed: ${response.message} (code ${response.code})"
                    }
                }
            }
            show?.addOnResponseListener(listener)
            onDispose { show?.removeOnResponseListener(listener) }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
        ) {
            Header()

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
                    cards = cards,
                    revealingCardId = revealingCardId,
                    revealedCardIds = revealedCardIds,
                    error = showError,
                    onToggle = { card, alreadyFetched ->
                        when {
                            card.id in revealedCardIds -> revealedCardIds =
                                revealedCardIds - card.id

                            alreadyFetched -> revealedCardIds = revealedCardIds + card.id
                            show != null -> {
                                showError = null
                                revealingCardId = card.id
                                show.requestAsync(
                                    VGSRequest.Builder("post", VGSHttpMethod.POST).body(
                                        mapOf(
                                            card.numberBodyKey to card.numberAlias,
                                            card.expDateBodyKey to card.expDateAlias,
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
private fun Header() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Primary)
            .statusBarsPadding()
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.linearGradient(listOf(Accent, Secondary)),
                        shape = RoundedCornerShape(12.dp),
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_lock_24),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp),
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = "VGS",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.2).sp,
                )
                Text(
                    text = "Collect + Show demo",
                    color = Accent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.2.sp,
                )
            }
        }
        Box(
            modifier = Modifier
                .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(999.dp))
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Text(
                text = MainActivity.ENVIRONMENT.uppercase(),
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.7.sp,
            )
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
                    textStyle = FieldTextStyle,
                    placeholder = { Text("1234 5678 9012 3456") },
                    leadingIcon = { FieldIcon(R.drawable.ic_credit_card_24) },
                    shape = RoundedCornerShape(10.dp),
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
                    textStyle = FieldTextStyle,
                    placeholder = { Text("MM/YY") },
                    leadingIcon = { FieldIcon(R.drawable.ic_calendar_24) },
                    shape = RoundedCornerShape(10.dp),
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
                loading = true,
                backgroundColor = Primary,
                contentColor = Color.White,
                onClick = { onSubmit(listOf(cardNumberState, expDateState)) },
            )
        }
    }
}

@Composable
private fun ShowSection(
    show: VGSShow?,
    cards: List<SubmittedCard>,
    revealingCardId: Int?,
    revealedCardIds: Set<Int>,
    error: String?,
    onToggle: (card: SubmittedCard, alreadyFetched: Boolean) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SectionTitle("Show")
            Text(
                text = when (cards.size) {
                    0 -> ""
                    1 -> "1 card"
                    else -> "${cards.size} cards"
                },
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
            )
        }

        if (cards.isEmpty()) {
            EmptyState()
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            cards.forEach { card ->
                key(card.id) {
                    ShowCard(
                        show = show,
                        card = card,
                        isRevealing = revealingCardId == card.id,
                        isRevealed = card.id in revealedCardIds,
                        revealRequestAllowed = revealingCardId == null,
                        onToggle = { alreadyFetched -> onToggle(card, alreadyFetched) },
                    )
                }
            }
        }

        if (error != null) {
            ErrorText(error)
        }
    }
}

@Composable
private fun ShowCard(
    show: VGSShow?,
    card: SubmittedCard,
    isRevealing: Boolean,
    isRevealed: Boolean,
    revealRequestAllowed: Boolean,
    onToggle: (alreadyFetched: Boolean) -> Unit,
) {
    val numberState = rememberVgsShowTextState(show = show, contentPath = card.numberContentPath)
    val expDateState = rememberVgsShowTextState(show = show, contentPath = card.expDateContentPath)
    val alreadyFetched = !numberState.isEmpty && !expDateState.isEmpty
    // Hide / re-show of already fetched data is local; only a network reveal is gated
    // while another card's request is in flight.
    val isLocalToggle = isRevealed || alreadyFetched

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
            enabled = !isRevealing && (isLocalToggle || revealRequestAllowed),
            loading = isRevealing,
            backgroundColor = Accent,
            contentColor = Primary,
            onClick = { onToggle(alreadyFetched) },
        )
    }
}

@Composable
private fun ShowField(
    label: String,
    state: VgsShowTextState,
    isRevealed: Boolean,
    masked: String,
    alias: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        FieldLabel(label)
        if (isRevealed) {
            VgsShowText(state = state, style = ValueTextStyle)
        } else {
            Text(text = masked, style = ValueTextStyle)
        }
        Box(
            modifier = Modifier
                .background(AliasChipBackground, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 5.dp)
        ) {
            Text(
                text = alias,
                color = AliasChipText,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                lineHeight = 17.sp,
            )
        }
    }
}

@Composable
private fun DemoCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = Color.White,
        border = BorderStroke(1.dp, CardBorder),
        elevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content,
        )
    }
}

@Composable
private fun ActionButton(
    text: String,
    loadingText: String,
    @DrawableRes iconRes: Int,
    enabled: Boolean,
    loading: Boolean,
    backgroundColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        shape = RoundedCornerShape(10.dp),
        elevation = null,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = backgroundColor,
            contentColor = contentColor,
            disabledBackgroundColor = backgroundColor.copy(alpha = 0.45f),
            disabledContentColor = contentColor.copy(alpha = 0.45f),
        ),
        contentPadding = PaddingValues(16.dp)
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(18.dp),
                color = LocalContentColor.current,
                strokeWidth = 2.5.dp,
            )
        } else {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier
                    .size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (loading) loadingText else text,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.2.sp,
        )
    }
}

@Composable
private fun EmptyState() {
    val dash = with(LocalDensity.current) { 6.dp.toPx() }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .drawBehind {
                val inset = 0.5.dp.toPx()
                drawRoundRect(
                    color = InputBorder,
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
                    color = Accent.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_credit_card_24),
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(24.dp),
            )
        }
        Text(
            text = "No aliases yet",
            color = Primary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "Enter a card above and tap Create alias.",
            color = TextMuted,
            fontSize = 13.sp,
            lineHeight = 19.5.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = Primary,
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.1).sp,
    )
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        color = TextMuted,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun FieldIcon(@DrawableRes iconRes: Int) {
    Icon(
        painter = painterResource(iconRes),
        contentDescription = null,
        modifier = Modifier.size(18.dp)
    )
}

@Composable
private fun ErrorText(text: String) {
    Text(
        text = text,
        color = ErrorColor,
        fontSize = 12.sp
    )
}

@Composable
private fun fieldColors() = TextFieldDefaults.outlinedTextFieldColors(
    textColor = Primary,
    backgroundColor = Color.White,
    cursorColor = Primary,
    focusedBorderColor = Primary,
    unfocusedBorderColor = InputBorder,
    errorBorderColor = ErrorColor,
    leadingIconColor = TextMuted,
    errorLeadingIconColor = TextMuted,
    placeholderColor = PlaceholderColor,
)

private val FieldTextStyle = TextStyle(
    fontSize = 16.sp,
    fontWeight = FontWeight.Medium,
    letterSpacing = 0.5.sp,
)

private val ValueTextStyle = TextStyle(
    color = Primary,
    fontSize = 18.sp,
    fontWeight = FontWeight.SemiBold,
    letterSpacing = 1.5.sp,
)

@Preview(showBackground = true, backgroundColor = 0xFFF3F6FC)
@Composable
private fun Preview() {
    Content(
        show = null,
        collect = VGSCollect(LocalContext.current, "", "")
    )
}
