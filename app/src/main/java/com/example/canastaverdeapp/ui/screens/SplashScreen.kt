package com.example.canastaverdeapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.canastaverdeapp.R
import com.example.canastaverdeapp.ui.theme.CanastaVerdeAppTheme

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Logo central
        Image(
            painter = painterResource(id = R.drawable.logo_canasta_verde),
            contentDescription = stringResource(R.string.logo_canasta_verde),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.7f)
        )

        // "Powered by LATA" abajo
        Image(
            painter = painterResource(id = R.drawable.logo_lata),
            contentDescription = stringResource(R.string.logo_lata),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 32.dp)
                .width(90.dp)
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
fun SplashScreenPreview() {
    CanastaVerdeAppTheme {
        SplashScreen()
    }
}

