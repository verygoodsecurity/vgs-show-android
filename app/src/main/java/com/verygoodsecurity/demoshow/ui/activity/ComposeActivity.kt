package com.verygoodsecurity.demoshow.ui.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.verygoodsecurity.demoshow.ui.CollectResponse
import com.verygoodsecurity.demoshow.ui.CollectSuccessResponse
import com.verygoodsecurity.demoshow.ui.MainActivity
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.widget.compose.ExpirationDateEditTextWrapper
import com.verygoodsecurity.vgscollect.widget.compose.VGSCardNumberEditTextWrapper
import com.verygoodsecurity.vgsshow.VGSShow
import com.verygoodsecurity.vgsshow.core.network.client.VGSHttpMethod
import com.verygoodsecurity.vgsshow.core.network.model.VGSRequest
import com.verygoodsecurity.vgsshow.widget.compose.VgsShowText
import com.verygoodsecurity.vgsshow.widget.compose.rememberVgsShowTextState
import org.json.JSONObject

private const val NUMBER_FIELD_NAME = "cardNumber"
private const val EXP_DATE_FIELD_NAME = "expDate"
private const val NUMBER_CONTENT_PATH = "json.payment_card_number"
private const val EXP_DATE_CONTENT_PATH = "json.payment_card_expiration_date"

class ComposeActivity : AppCompatActivity() {

    private val show: VGSShow by lazy {
        VGSShow.Builder(this, MainActivity.TENANT_ID).build()
    }

    private val collect: VGSCollect by lazy {
        VGSCollect(this, MainActivity.TENANT_ID, MainActivity.ENVIRONMENT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
private fun Content(show: VGSShow?, collect: VGSCollect?) {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding()
                .padding(16.dp)
        ) {
            var cardNumberAlias by remember { mutableStateOf<String?>(null) }
            var expDateAlias by remember { mutableStateOf<String?>(null) }

            DisposableEffect(collect) {
                val listener = object : VgsCollectResponseListener {
                    override fun onResponse(response: CollectResponse) {
                        val json = (response as? CollectSuccessResponse)?.rawResponse
                            ?.let(::JSONObject)
                            ?.optJSONObject("json")
                        cardNumberAlias = json?.optString(NUMBER_FIELD_NAME)
                        expDateAlias = json?.optString(EXP_DATE_FIELD_NAME)
                    }
                }
                collect?.addOnResponseListeners(listener)
                onDispose { collect?.removeOnResponseListener(listener) }
            }

            Text(text = "Collect", color = Color(0xff4b5d69))

            Spacer(modifier = Modifier.height(16.dp))

            VGSCardNumberEditTextWrapper(
                collect = collect,
                fieldName = NUMBER_FIELD_NAME,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExpirationDateEditTextWrapper(
                collect = collect,
                fieldName = EXP_DATE_FIELD_NAME,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                modifier = Modifier.align(alignment = Alignment.End),
                contentPadding = PaddingValues(16.dp),
                onClick = { collect?.asyncSubmit("/post", HTTPMethod.POST) },
            ) {
                Text(text = "CREATE ALIASES", color = Color(0xff4b5d69))
            }

            Spacer(modifier = Modifier.height(24.dp))
            Divider()
            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Show", color = Color(0xff4b5d69))

            Spacer(modifier = Modifier.height(16.dp))

            val cardNumberState = rememberVgsShowTextState(
                show = show,
                contentPath = NUMBER_CONTENT_PATH,
            )
            VgsShowText(state = cardNumberState, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(16.dp))

            val expDateState = rememberVgsShowTextState(
                show = show,
                contentPath = EXP_DATE_CONTENT_PATH,
            )
            VgsShowText(state = expDateState, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                modifier = Modifier.align(alignment = Alignment.End),
                contentPadding = PaddingValues(16.dp),
                enabled = cardNumberAlias != null && expDateAlias != null,
                onClick = {
                    val numberAlias = cardNumberAlias
                    val expAlias = expDateAlias
                    if (numberAlias != null && expAlias != null) {
                        show?.requestAsync(
                            VGSRequest.Builder("post", VGSHttpMethod.POST).body(
                                mapOf(
                                    "payment_card_number" to numberAlias,
                                    "payment_card_expiration_date" to expAlias,
                                )
                            ).build()
                        )
                    }
                },
            ) {
                Text(text = "REVEAL", color = Color(0xff4b5d69))
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun Preview() {
    Content(show = null, collect = null)
}
