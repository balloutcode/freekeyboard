package com.freekeyboardballoutcode.ime.autofill

import android.content.Context
import android.os.Build
import android.util.Size
import android.util.TypedValue
import android.widget.inline.InlinePresentationSpec
import androidx.annotation.RequiresApi
import androidx.autofill.inline.UiVersions
import androidx.autofill.inline.v1.InlineSuggestionUi
import androidx.compose.ui.graphics.toArgb
import com.freekeyboardballoutcode.R
import com.freekeyboardballoutcode.data.ThemePreference
import com.freekeyboardballoutcode.ui.theme.keyboardColorsFor
import android.view.inputmethod.InlineSuggestionsRequest as ImeInlineSuggestionsRequest

/**
 * Builds themed [ImeInlineSuggestionsRequest] for password-manager chips in the IME strip.
 * Does not read or log credential content — presentation only.
 */
/** Pixel size for chip layout (JVM-safe; avoids android.util.Size in unit tests). */
data class ChipSizePx(val width: Int, val height: Int) {
    fun toAndroidSize(): Size = Size(width, height)
}

object InlineAutofillSupport {
    const val MAX_SUGGESTION_COUNT = 3
    const val CHIP_MIN_WIDTH_DP = 80f
    const val CHIP_HEIGHT_DP = 48f
    /** Max chip width as a fraction of screen width. */
    const val CHIP_MAX_WIDTH_SCREEN_FRACTION = 2f / 3f

    fun chipMinSizePx(density: Float): ChipSizePx {
        val w = (CHIP_MIN_WIDTH_DP * density).toInt().coerceAtLeast(1)
        val h = (CHIP_HEIGHT_DP * density).toInt().coerceAtLeast(1)
        return ChipSizePx(w, h)
    }

    fun chipMaxSizePx(screenWidthPx: Int, density: Float): ChipSizePx {
        val w = (screenWidthPx * CHIP_MAX_WIDTH_SCREEN_FRACTION).toInt().coerceAtLeast(1)
        val h = (CHIP_HEIGHT_DP * density).toInt().coerceAtLeast(1)
        return ChipSizePx(w, h)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun createInlineSuggestionsRequest(
        context: Context,
        theme: ThemePreference,
    ): ImeInlineSuggestionsRequest {
        val density = context.resources.displayMetrics.density
        val screenW = context.resources.displayMetrics.widthPixels
        val min = chipMinSizePx(density).toAndroidSize()
        val max = chipMaxSizePx(screenW, density).toAndroidSize()
        val colors = keyboardColorsFor(theme)
        val chipBg = colors.key.toArgb()
        val title = colors.onKey.toArgb()
        val subtitle = colors.onKey.copy(alpha = 0.7f).toArgb()
        val styleBundle = buildStyleBundle(context, chipBg, title, subtitle)
        val spec = InlinePresentationSpec.Builder(min, max)
            .setStyle(styleBundle)
            .build()
        val specs = List(MAX_SUGGESTION_COUNT) { spec }
        return ImeInlineSuggestionsRequest.Builder(specs)
            .setMaxSuggestionCount(MAX_SUGGESTION_COUNT)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun buildStyleBundle(
        context: Context,
        chipBgArgb: Int,
        titleArgb: Int,
        subtitleArgb: Int,
    ): android.os.Bundle {
        fun dp(v: Float): Int =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v, context.resources.displayMetrics)
                .toInt()

        val chipIcon = android.graphics.drawable.Icon.createWithResource(
            context,
            R.drawable.inline_suggestion_chip,
        ).setTint(chipBgArgb)

        val suggestionStyle = InlineSuggestionUi.newStyleBuilder()
            .setSingleIconChipStyle(
                androidx.autofill.inline.common.ViewStyle.Builder()
                    .setBackground(chipIcon)
                    .setPadding(0, 0, 0, 0)
                    .build(),
            )
            .setChipStyle(
                androidx.autofill.inline.common.ViewStyle.Builder()
                    .setBackground(chipIcon)
                    .setPadding(dp(6f), 0, dp(6f), 0)
                    .build(),
            )
            .setStartIconStyle(
                androidx.autofill.inline.common.ImageViewStyle.Builder()
                    .setLayoutMargin(0, 0, 0, 0)
                    .build(),
            )
            .setTitleStyle(
                androidx.autofill.inline.common.TextViewStyle.Builder()
                    .setLayoutMargin(dp(4f), 0, dp(4f), 0)
                    .setTextColor(titleArgb)
                    .setTextSize(14f)
                    .build(),
            )
            .setSubtitleStyle(
                androidx.autofill.inline.common.TextViewStyle.Builder()
                    .setLayoutMargin(dp(4f), 0, dp(4f), 0)
                    .setTextColor(subtitleArgb)
                    .setTextSize(12f)
                    .build(),
            )
            .setEndIconStyle(
                androidx.autofill.inline.common.ImageViewStyle.Builder()
                    .setLayoutMargin(0, 0, 0, 0)
                    .build(),
            )
            .build()

        return UiVersions.newStylesBuilder()
            .addStyle(suggestionStyle)
            .build()
    }
}
