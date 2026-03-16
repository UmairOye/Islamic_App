package com.ub.islamicapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ub.islamicapp.presentation.components.CalendarView
import com.ub.islamicapp.theme.LightBackground
import com.ub.islamicapp.theme.PrimaryGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HijriCalendarScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Hijri Calendar",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "UMM AL-QURA",
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.sp,
                            color = PrimaryGreen,
                            letterSpacing = 1.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(PrimaryGreen.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = PrimaryGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = { /* Handle Today click, ideally pass a trigger down to CalendarView or manage state here */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryGreen.copy(alpha = 0.1f),
                            contentColor = PrimaryGreen
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .height(32.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Today",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = LightBackground)
            )
        },
        containerColor = LightBackground
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            CalendarView(isHijri = true, modifier = Modifier.fillMaxSize())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GregorianCalendarScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Gregorian Calendar",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "STANDARD",
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.sp,
                            color = PrimaryGreen,
                            letterSpacing = 1.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(PrimaryGreen.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = PrimaryGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = { /* Handle Today click */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryGreen.copy(alpha = 0.1f),
                            contentColor = PrimaryGreen
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .height(32.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Today",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = LightBackground)
            )
        },
        containerColor = LightBackground
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            CalendarView(isHijri = false, modifier = Modifier.fillMaxSize())
        }
    }
}
