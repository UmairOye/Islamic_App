package com.ub.islamicapp.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ub.islamicapp.R

//fonts

val InterDisplay = listOf<FontFamily>(
        FontFamily(Font(R.font.inter_bold, FontWeight.Bold)),
        FontFamily(Font(R.font.inter_semibold, FontWeight.SemiBold)),
        FontFamily(Font(R.font.inter_medium, FontWeight.Medium)),
        FontFamily(Font(R.font.inter_regular, FontWeight.Normal)),
        FontFamily(Font(R.font.inter_extrabold, FontWeight.ExtraBold))
        )




// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)