package com.example.canastaverdeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.canastaverdeapp.ui.screens.BienvenidaScreen
import com.example.canastaverdeapp.ui.screens.SplashScreen
import com.example.canastaverdeapp.ui.theme.CanastaVerdeAppTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )
        setContent {
            CanastaVerdeAppTheme {
                var mostrarSplash by remember { mutableStateOf(true) }

                LaunchedEffect(Unit) {
                    delay(2000)
                    mostrarSplash = false
                }

                if (mostrarSplash) {
                    SplashScreen()
                } else {
                    BienvenidaScreen(
                        onIngresarClick = { /* pendiente: ir a Login */ },
                        onRegistrarseClick = { /* pendiente: ir a Registro */ }
                    )
                }
            }
        }
    }
}