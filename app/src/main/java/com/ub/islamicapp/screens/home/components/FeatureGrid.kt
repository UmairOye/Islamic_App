package com.ub.islamicapp.screens.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
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
            text = stringResource(R.string.home_all_features),
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp),
            fontSize = 16.sp,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FeatureItem(label = stringResource(R.string.feature_quran), drawable = R.drawable.quran, onClick = { }, modifier = Modifier.weight(1f))
            FeatureItem(label = stringResource(R.string.feature_adhkar), drawable = R.drawable.adkhar, onClick = {}, modifier = Modifier.weight(1f))
            FeatureItem(label = stringResource(R.string.feature_salah), drawable = R.drawable.salah, onClick = onNavigateToSalah, modifier = Modifier.weight(1f))
            FeatureItem(label = stringResource(R.string.feature_qibla), drawable = R.drawable.qibla, onClick = onNavigateToQibla, modifier = Modifier.weight(1f))
            FeatureItem(label = stringResource(R.string.feature_hijri), drawable = R.drawable.events, onClick = onNavigateToHijri, modifier = Modifier.weight(1f))
        }

    }
}
