import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier

@Composable
fun AutoResizeText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle,
    maxLines: Int = 1,
    minFontSizeSp: Float = 10f,
    maxFontSizeSp: Float = 14f,
    stepSp: Float = 0.1f
) {
    val measurer = rememberTextMeasurer()

    BoxWithConstraints(modifier) {
        val density = LocalDensity.current
        val maxWidthPx = with(density) { maxWidth.roundToPx() }

        var chosenSp by remember(text, maxWidthPx, maxLines) { mutableStateOf(maxFontSizeSp) }

        LaunchedEffect(text, maxWidthPx, maxLines, minFontSizeSp, maxFontSizeSp, stepSp) {
            var sizeSp = maxFontSizeSp
            var found = false

            while (sizeSp >= minFontSizeSp) {
                val result = measurer.measure(
                    text = AnnotatedString(text),
                    style = style.copy(fontSize = sizeSp.sp),
                    constraints = Constraints(
                        minWidth = 0,
                        maxWidth = maxWidthPx,
                        minHeight = 0,
                        maxHeight = Constraints.Infinity
                    )
                )
                val fits = !result.didOverflowWidth &&
                        !result.didOverflowHeight &&
                        result.lineCount <= maxLines

                if (fits) {
                    chosenSp = sizeSp
                    found = true
                    break
                }
                sizeSp -= stepSp
            }

            if (!found) chosenSp = minFontSizeSp
        }

        Text(
            text = text,
            style = style.copy(fontSize = chosenSp.sp),
            maxLines = maxLines,
            softWrap = false
        )
    }
}
