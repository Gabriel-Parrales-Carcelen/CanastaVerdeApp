package com.example.canastaverdeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.canastaverdeapp.ui.screens.BienvenidaScreen
import com.example.canastaverdeapp.ui.screens.RegistroScreen
import com.example.canastaverdeapp.ui.screens.SplashScreen
import com.example.canastaverdeapp.ui.theme.CanastaVerdeAppTheme
import kotlinx.coroutines.delay

enum class Pantalla { Splash, Bienvenida, Registro }

class MainActivity : ComponentActivity() {

    private val repository by lazy { FirestoreRepository() }

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
                var pantalla by rememberSaveable { mutableStateOf(Pantalla.Splash) }

                LaunchedEffect(pantalla) {
                    if (pantalla == Pantalla.Splash) {
                        delay(2000)
                        pantalla = Pantalla.Bienvenida
                    }
                }

                // El botón "atrás" en Registro vuelve a Bienvenida
                BackHandler(enabled = pantalla == Pantalla.Registro) {
                    pantalla = Pantalla.Bienvenida
                }

                when (pantalla) {
                    Pantalla.Splash -> SplashScreen()

                    Pantalla.Bienvenida -> BienvenidaScreen(
                        onIngresarClick = { /* pendiente: ir a Login */ },
                        onRegistrarseClick = { pantalla = Pantalla.Registro }
                    )

                    Pantalla.Registro -> RegistroScreen(
                        onRegistrar = { email, password ->
                            repository.registrarUsuario(
                                nombre = email.substringBefore("@"),
                                email = email,
                                password = password
                            )
                        },
                        onRegistroExitoso = { /* pendiente: ir a la pantalla principal */ }
                    )
                }
            }
        }
    }
}