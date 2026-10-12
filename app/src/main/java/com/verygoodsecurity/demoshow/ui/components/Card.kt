package com.verygoodsecurity.demoshow.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.verygoodsecurity.demoshow.ui.theme.DemoColors
import com.verygoodsecurity.demoshow.ui.theme.DemoShapes
import com.verygoodsecurity.demoshow.ui.theme.DemoSpacing

@Composable
fun DemoCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = DemoShapes.card,
        backgroundColor = Color.White,
        border = BorderStroke(1.dp, DemoColors.CardBorder),
        elevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(DemoSpacing.cardPadding),
            verticalArrangement = Arrangement.spacedBy(DemoSpacing.xl),
            content = content,
        )
    }
}
