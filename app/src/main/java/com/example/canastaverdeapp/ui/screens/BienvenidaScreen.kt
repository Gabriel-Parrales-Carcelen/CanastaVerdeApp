package com.example.canastaverdeapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.canastaverdeapp.R
import com.example.canastaverdeapp.ui.theme.CanastaVerdeAppTheme

@Composable
fun BienvenidaScreen(
    modifier: Modifier = Modifier,
    onIngresarClick: () -> Unit = {},      // luego: navegar a Login
    onRegistrarseClick: () -> Unit = {}    // luego: navegar a Registro
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Foto superior
        Image(
            painter = painterResource(id = R.drawable.foto_bienvenida),
            contentDescription = stringResource(R.string.foto_bienvenida),
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.47f)
        )

        // Zona inferior con texto y botones
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.53f)
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(R.string.bienvenida_titulo).uppercase(),
                color = MaterialTheme.colorScheme.onBackground,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                lineHeight = 32.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = onIngresarClick,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = stringResource(R.string.boton_ingresar).uppercase(),
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.primera_vez),
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            TextButton(onClick = onRegistrarseClick) {
                Text(
                    text = stringResource(R.string.boton_registrarse),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textDecoration = TextDecoration.Underline
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // "Powered by LATA"
            Image(
                painter = painterResource(id = R.drawable.logo_lata),
                contentDescription = stringResource(R.string.logo_lata),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(bottom = 24.dp)
                    .width(90.dp)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
fun BienvenidaScreenPreview() {
    CanastaVerdeAppTheme {
        BienvenidaScreen()
    }
}