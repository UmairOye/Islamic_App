package com.ub.islamicapp.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ub.islamicapp.R

@Composable
fun FeatureGrid(
    modifier: Modifier = Modifier,
    onNavigateToHijri: () -> Unit = {},
    onNavigateToCalendar: () -> Unit = {},
    onNavigateToSalah: () -> Unit = {},
    onNavigateToQibla: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "All Features",
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp),
            fontSize = 16.sp,
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FeatureItem(label = "Quran", drawable = R.drawable.quran, onClick = { }, modifier = Modifier.weight(1f))
            FeatureItem(label = "Adhkar ", drawable = R.drawable.adkhar, onClick = onNavigateToHijri, modifier = Modifier.weight(1f))
            FeatureItem(label = "Salah", drawable = R.drawable.salah, onClick = onNavigateToQibla, modifier = Modifier.weight(1f))
            FeatureItem(label = "Qibla", drawable = R.drawable.qibla, onClick = { }, modifier = Modifier.weight(1f))
            FeatureItem(label = "Events", drawable = R.drawable.events, onClick = { }, modifier = Modifier.weight(1f))

        }

    }
}
