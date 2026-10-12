package com.verygoodsecurity.demoshow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.verygoodsecurity.demoshow.R
import com.verygoodsecurity.demoshow.ui.theme.DemoColors
import com.verygoodsecurity.demoshow.ui.theme.DemoShapes
import com.verygoodsecurity.demoshow.ui.theme.DemoTextStyles

@Composable
fun DemoHeader(
    title: String,
    subtitle: String,
    environment: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(DemoColors.Primary)
            .statusBarsPadding()
            .padding(
                start = if (onBack != null) 10.dp else 20.dp,
                top = if (onBack != null) 16.dp else 20.dp,
                end = 20.dp,
                bottom = if (onBack != null) 16.dp else 20.dp,
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(if (onBack != null) 4.dp else 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack, modifier = Modifier.size(44.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_back_24),
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp),
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.linearGradient(listOf(DemoColors.Accent, DemoColors.Secondary)),
                            shape = DemoShapes.icon,
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
            }
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(text = title, color = Color.White, style = DemoTextStyles.headerTitle)
                Text(text = subtitle, color = DemoColors.Accent, style = DemoTextStyles.headerSubtitle)
            }
        }
        Box(
            modifier = Modifier
                .border(1.dp, Color.White.copy(alpha = 0.3f), DemoShapes.pill)
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Text(text = environment.uppercase(), color = Color.White, style = DemoTextStyles.badgeText)
        }
    }
}
