package com.verygoodsecurity.demoshow.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.verygoodsecurity.demoshow.ui.theme.DemoColors
import com.verygoodsecurity.demoshow.ui.theme.DemoTextStyles

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        color = DemoColors.Primary,
        style = DemoTextStyles.sectionTitle,
    )
}

@Composable
fun FieldLabel(text: String) {
    Text(
        text = text,
        color = DemoColors.TextMuted,
        style = DemoTextStyles.fieldLabel,
    )
}

@Composable
fun FieldIcon(@DrawableRes iconRes: Int) {
    Icon(
        painter = painterResource(iconRes),
        contentDescription = null,
        modifier = Modifier.size(18.dp)
    )
}

@Composable
fun ErrorText(text: String) {
    Text(
        text = text,
        color = DemoColors.ErrorColor,
        fontSize = 12.sp
    )
}
