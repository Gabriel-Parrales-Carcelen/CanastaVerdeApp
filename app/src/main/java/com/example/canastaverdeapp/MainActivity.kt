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
import com.example.canastaverdeapp.notification.NotificationHelper
import com.example.canastaverdeapp.notification.RequestNotificationPermission
import com.example.canastaverdeapp.ui.screens.BienvenidaScreen
import com.example.canastaverdeapp.ui.screens.CarritoScreen
import com.example.canastaverdeapp.ui.screens.CarritoViewModel
import com.example.canastaverdeapp.ui.screens.HomeScreen
import com.example.canastaverdeapp.ui.screens.LoginScreen
import com.example.canastaverdeapp.ui.screens.RegistroScreen
import com.example.canastaverdeapp.ui.screens.SplashScreen
import com.example.canastaverdeapp.ui.theme.CanastaVerdeAppTheme
import kotlinx.coroutines.delay

enum class Pantalla { Splash, Bienvenida, Login, Registro, Home, Carrito }

class MainActivity : ComponentActivity() {

    private val repository by lazy { FirestoreRepository() }
    private val carritoViewModel by lazy { CarritoViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationHelper.createNotificationChannel(this)
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
                RequestNotificationPermission()
                var pantalla by rememberSaveable { mutableStateOf(Pantalla.Splash) }

                LaunchedEffect(pantalla) {
                    if (pantalla == Pantalla.Splash) {
                        delay(2000)
                        // Si ya hay una sesión abierta, entra directo al Home
                        pantalla = if (repository.currentUser != null) Pantalla.Home
                        else Pantalla.Bienvenida
                    }
                }

                // "Atrás" en Login o Registro vuelve a Bienvenida
                BackHandler(enabled = pantalla == Pantalla.Login || pantalla == Pantalla.Registro) {
                    pantalla = Pantalla.Bienvenida
                }

                when (pantalla) {
                    Pantalla.Splash -> SplashScreen()

                    Pantalla.Bienvenida -> BienvenidaScreen(
                        onIngresarClick = { pantalla = Pantalla.Login },
                        onRegistrarseClick = { pantalla = Pantalla.Registro }
                    )

                    Pantalla.Login -> LoginScreen(
                        onIniciarSesion = { email, password ->
                            repository.iniciarSesion(email, password)
                        },
                        onRegistrarseClick = { pantalla = Pantalla.Registro },
                        onLoginExitoso = { pantalla = Pantalla.Home }
                    )

                    Pantalla.Registro -> RegistroScreen(
                        onRegistrar = { email, password ->
                            repository.registrarUsuario(
                                nombre = email.substringBefore("@"),
                                email = email,
                                password = password
                            )
                        },
                        onRegistroExitoso = { pantalla = Pantalla.Home }
                    )

                    Pantalla.Home -> HomeScreen(
                        correoUsuario = repository.currentUser?.email ?: "",
                        carritoViewModel = carritoViewModel,
                        onCerrarSesion = {
                            repository.cerrarSesion()
                            carritoViewModel.vaciarCarrito()
                            pantalla = Pantalla.Login
                        },
                        onCarritoClick = { pantalla = Pantalla.Carrito }
                    )

                    Pantalla.Carrito -> CarritoScreen(
                        correoUsuario = repository.currentUser?.email ?: "",
                        carritoViewModel = carritoViewModel,
                        repository = repository,
                        onVolverAlMenu = { pantalla = Pantalla.Home },
                        onCerrarSesion = {
                            repository.cerrarSesion()
                            carritoViewModel.vaciarCarrito()
                            pantalla = Pantalla.Login
                        }
                    )
                }
            }
        }
    }
}
