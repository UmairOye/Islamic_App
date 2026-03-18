package com.ub.islamicapp.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ub.islamicapp.R
import com.ub.islamicapp.theme.EmeraldGreen
import com.ub.islamicapp.theme.PrimaryGreen

@Preview
@Composable
fun LastReadCard(

) {
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(PrimaryGreen, EmeraldGreen)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth().padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(gradientBrush)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {

                Text(
                    text = "Ayat of the Moment",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "Surah-Al-Baqarah [3-4]",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "وَ الَّذِیْنَ یُؤْمِنُوْنَ بِمَاۤ اُنْزِلَ اِلَیْكَ وَ مَاۤ اُنْزِلَ مِنْ قَبْلِكَۚ-وَ بِالْاٰخِرَةِ هُمْ یُوْقِنُوْنَﭤ ",
                    fontSize = 26.sp,

                    color = Color.White
                )
            }

        }
    }
}
