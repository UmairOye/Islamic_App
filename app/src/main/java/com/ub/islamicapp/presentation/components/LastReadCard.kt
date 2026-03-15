package com.ub.islamicapp.presentation.components

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
//    surah: String,
//    verse: String,
//    onContinueClick: () -> Unit,
//    modifier: Modifier = Modifier
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
//                    fontFamily = FontFamily(Font(R.font.noorehuda, FontWeight.Bold)),
                    color = Color.White
                )
            }

//            Column {
//                Text(
//                    text = "Last Read",
//                    style = MaterialTheme.typography.labelMedium,
//                    color = Color.White.copy(alpha = 0.8f)
//                )
//                Spacer(modifier = Modifier.height(8.dp))
//                // Simulated Arabic text or actual surah name
//                Text(
//                    text = surah, // "الفاتحة"
//                    style = MaterialTheme.typography.headlineMedium.copy(
//                        fontWeight = FontWeight.Bold,
//                        fontSize = 28.sp
//                    ),
//                    color = Color.White
//                )
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(
//                    text = verse,
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = Color.White.copy(alpha = 0.9f)
//                )
//                Spacer(modifier = Modifier.height(16.dp))
//
//                Button(
//                    onClick = onContinueClick,
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = Color.White,
//                        contentColor = PrimaryGreen
//                    ),
//                    shape = RoundedCornerShape(20.dp),
//                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
//                    modifier = Modifier.height(36.dp)
//                ) {
//                    Text(
//                        text = "Continue",
//                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
//                    )
//                    Spacer(modifier = Modifier.width(4.dp))
//                    Icon(
//                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
//                        contentDescription = "Continue",
//                        modifier = Modifier.size(16.dp)
//                    )
//                }
//            }
//
//            // Placeholder for the Quran illustration on the right
//            Icon(
//                imageVector = Icons.AutoMirrored.Rounded.MenuBook,
//                contentDescription = "Quran",
//                tint = Color.White.copy(alpha = 0.5f),
//                modifier = Modifier.size(80.dp)
//            )
        }
    }
}
