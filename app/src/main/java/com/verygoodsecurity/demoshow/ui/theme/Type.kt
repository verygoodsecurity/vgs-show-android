package com.verygoodsecurity.demoshow.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object DemoTextStyles {

    val headerTitle = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.2).sp,
    )

    val headerSubtitle = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.2.sp,
    )

    val badgeText = TextStyle(
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.7.sp,
    )

    val sectionTitle = TextStyle(
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.1).sp,
    )

    val fieldLabel = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
    )

    val fieldText = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.5.sp,
    )

    val valueText = TextStyle(
        color = DemoColors.Primary,
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.5.sp,
    )

    val aliasChipText = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = 12.sp,
        lineHeight = 17.sp,
    )

    val buttonText = TextStyle(
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.2.sp,
    )

    val rowTitle = TextStyle(
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold,
    )

    val rowSubtitle = TextStyle(
        fontSize = 12.sp,
    )

    val emptyTitle = TextStyle(
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
    )

    val emptyBody = TextStyle(
        fontSize = 13.sp,
        lineHeight = 19.5.sp,
    )
}
