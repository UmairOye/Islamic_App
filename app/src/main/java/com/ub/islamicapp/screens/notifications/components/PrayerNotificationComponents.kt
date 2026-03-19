package com.ub.islamicapp.screens.notifications.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ub.islamicapp.screens.notifications.model.PrayerUiData
import com.ub.islamicapp.theme.ThemeButtonBg
import com.ub.islamicapp.theme.ThemeDarkGreenIcon
import com.ub.islamicapp.theme.ThemeGrayText

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            color = ThemeGrayText
        ),
        modifier = Modifier.padding(horizontal = 24.dp)
    )
}

@Composable
fun IconBox(iconResId: Int, bgColor: Color, iconColor: Color) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(painter = painterResource(id = iconResId), contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
    }
}

@Composable
fun VectorIconBox(icon: ImageVector, bgColor: Color, iconColor: Color) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
    }
}

@Composable
fun PrayerNotificationItem(prayer: PrayerUiData) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(prayer.initialOption) }
    val options = listOf("None", "Notification", "Adhan")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            IconBox(iconResId = prayer.iconResId, bgColor = prayer.bgColor, iconColor = prayer.iconColor)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = prayer.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = prayer.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ThemeGrayText
                )
            }
        }

        Box {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(ThemeButtonBg)
                    .clickable { expanded = true }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedOption,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = Color(0xFF334155)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    Icons.Rounded.KeyboardArrowDown,
                    contentDescription = "Dropdown",
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(18.dp)
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                color = if (selectedOption == option) ThemeDarkGreenIcon else Color(0xFF334155)
                            )
                        },
                        onClick = {
                            selectedOption = option
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsActionItem(title: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(ThemeButtonBg)
            .clickable { /* Handle click */ }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color(0xFF0F172A), modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                color = Color(0xFF0F172A)
            )
        }
        Icon(Icons.Rounded.ChevronRight, contentDescription = "Forward", tint = Color(0xFF94A3B8))
    }
}
