package com.ub.islamicapp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ub.islamicapp.R

@Composable
@Preview(showBackground = true)
fun CompassView(){
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {


        Image(
            painter = painterResource(R.drawable.qibla_outer_ring),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
        )


        Image(
            painter = painterResource(R.drawable.qibla_reading),
            contentDescription = null,
            modifier = Modifier.fillMaxSize().offset(y = 20.dp)
        )




    }
}