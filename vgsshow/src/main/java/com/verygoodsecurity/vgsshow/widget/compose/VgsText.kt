package com.verygoodsecurity.vgsshow.widget.compose

import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import com.verygoodsecurity.vgsshow.widget.compose.state.VgsTextState
import com.verygoodsecurity.vgsshow.widget.compose.state.rememberVgsTextState

/**
 * Securely displays revealed text, obtained via [rememberVgsTextState].
 *
 * Renders [VgsTextState.displayText], which already has that state's visual transformation
 * applied — pass a transformation to [rememberVgsTextState] to change how the value is masked
 * or formatted, since plain Compose `Text` has no such concept to plug into here.
 *
 * Accepts the same styling parameters as Compose Material's `Text`, with two deliberate
 * omissions: `inlineContent` and `onTextLayout`. Both are left out because either would hand the
 * currently displayed string back to your code — a real leak of the revealed value whenever no
 * transformation is applied — defeating the point of this widget.
 *
 * @param state The current text state.
 * @param modifier The modifier to apply to this layout.
 * @param color The color to apply to the text.
 * @param fontSize The size of glyphs to use when painting the text.
 * @param fontStyle The typeface variant to use when drawing the letters (e.g. italics).
 * @param fontWeight The typeface thickness to use when painting the text (e.g. bold).
 * @param fontFamily The font family to be used when rendering the text.
 * @param letterSpacing The amount of space to add between each letter.
 * @param textDecoration The decorations to paint on the text (e.g. an underline).
 * @param textAlign The alignment of the text within the lines of the paragraph.
 * @param lineHeight Line height for the paragraph in [TextUnit] unit.
 * @param overflow How visual overflow should be handled.
 * @param softWrap Whether the text should break at soft line breaks.
 * @param maxLines The maximum number of lines to display.
 * @param minLines The minimum number of lines to display.
 * @param style The style to apply to the text.
 */
@Composable
fun VgsText(
    state: VgsTextState,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    style: TextStyle = LocalTextStyle.current,
) {
    Text(
        text = state.displayText,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        style = style,
    )
}
