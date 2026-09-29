package com.verygoodsecurity.demoshow.ui.components

import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.verygoodsecurity.demoshow.ui.theme.DemoColors

@Composable
fun fieldColors() = TextFieldDefaults.outlinedTextFieldColors(
    textColor = DemoColors.Primary,
    backgroundColor = Color.White,
    cursorColor = DemoColors.Primary,
    focusedBorderColor = DemoColors.Primary,
    unfocusedBorderColor = DemoColors.InputBorder,
    errorBorderColor = DemoColors.ErrorColor,
    leadingIconColor = DemoColors.TextMuted,
    errorLeadingIconColor = DemoColors.TextMuted,
    placeholderColor = DemoColors.PlaceholderColor,
)
