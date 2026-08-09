package com.kian.mahmoudi.vegang.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.kian.mahmoudi.vegang.R


object VazirmatnFonts {
    val Thin = FontWeight(100)
    val ExtraLight = FontWeight(200)
    val Light = FontWeight(300)
    val Regular = FontWeight(400)
    val Medium = FontWeight(500)
    val SemiBold = FontWeight(600)
    val Bold = FontWeight(700)
    val ExtraBold = FontWeight(800)
    val Black = FontWeight(900)
}

val VazirmatnFontFamily = FontFamily(
    Font(R.font.vazirmatn_thin, VazirmatnFonts.Thin),
    Font(R.font.vazirmatn_extralight, VazirmatnFonts.ExtraLight),
    Font(R.font.vazirmatn_light, VazirmatnFonts.Light),
    Font(R.font.vazirmatn_regular, VazirmatnFonts.Regular),
    Font(R.font.vazirmatn_medium, VazirmatnFonts.Medium),
    Font(R.font.vazirmatn_semibold, VazirmatnFonts.SemiBold),
    Font(R.font.vazirmatn_bold, VazirmatnFonts.Bold),
    Font(R.font.vazirmatn_extrabold, VazirmatnFonts.ExtraBold),
    Font(R.font.vazirmatn_black, VazirmatnFonts.Black),
)

object AppTypography {
    private val baseFontFamily = VazirmatnFontFamily

    val typography = Typography(
        // Display styles
        displayLarge = TextStyle(
            fontFamily = baseFontFamily,
            fontWeight = VazirmatnFonts.Bold,
            fontSize = 57.sp,
            lineHeight = 64.sp,
            letterSpacing = -0.25.sp
        ),
        displayMedium = TextStyle(
            fontFamily = baseFontFamily,
            fontWeight = VazirmatnFonts.Bold,
            fontSize = 45.sp,
            lineHeight = 52.sp
        ),
        displaySmall = TextStyle(
            fontFamily = baseFontFamily,
            fontWeight = VazirmatnFonts.SemiBold,
            fontSize = 36.sp,
            lineHeight = 44.sp
        ),

        // Headline styles
        headlineLarge = TextStyle(
            fontFamily = baseFontFamily,
            fontWeight = VazirmatnFonts.SemiBold,
            fontSize = 32.sp,
            lineHeight = 40.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = baseFontFamily,
            fontWeight = VazirmatnFonts.SemiBold,
            fontSize = 28.sp,
            lineHeight = 36.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = baseFontFamily,
            fontWeight = VazirmatnFonts.SemiBold,
            fontSize = 24.sp,
            lineHeight = 32.sp
        ),

        // Title styles
        titleLarge = TextStyle(
            fontFamily = baseFontFamily,
            fontWeight = VazirmatnFonts.Medium,
            fontSize = 22.sp,
            lineHeight = 28.sp
        ),
        titleMedium = TextStyle(
            fontFamily = baseFontFamily,
            fontWeight = VazirmatnFonts.Medium,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.15.sp
        ),
        titleSmall = TextStyle(
            fontFamily = baseFontFamily,
            fontWeight = VazirmatnFonts.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),

        // Body styles (Persian needs larger line height)
        bodyLarge = TextStyle(
            fontFamily = baseFontFamily,
            fontWeight = VazirmatnFonts.Regular,
            fontSize = 16.sp,
            lineHeight = 24.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = baseFontFamily,
            fontWeight = VazirmatnFonts.Regular,
            fontSize = 14.sp,
            lineHeight = 20.sp
        ),
        bodySmall = TextStyle(
            fontFamily = baseFontFamily,
            fontWeight = VazirmatnFonts.Regular,
            fontSize = 12.sp,
            lineHeight = 16.sp
        ),

        // Label styles
        labelLarge = TextStyle(
            fontFamily = baseFontFamily,
            fontWeight = VazirmatnFonts.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),
        labelMedium = TextStyle(
            fontFamily = baseFontFamily,
            fontWeight = VazirmatnFonts.Medium,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        ),
        labelSmall = TextStyle(
            fontFamily = baseFontFamily,
            fontWeight = VazirmatnFonts.Medium,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        ),
    )
}