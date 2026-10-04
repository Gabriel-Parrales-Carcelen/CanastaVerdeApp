package com.example.canastaverdeapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Paleta para el Modo Oscuro (Dark Theme)
private val DarkColorScheme = darkColorScheme(
    primary = VerdeClaro,
    onPrimary = VerdeOscuro,
    primaryContainer = VerdeCanasta,
    onPrimaryContainer = Blanco,
    secondary = VerdeMedio,
    onSecondary = VerdeOscuro,
    secondaryContainer = VerdeOscuro,
    onSecondaryContainer = VerdeClaro,
    tertiary = VerdeClaro,
    onTertiary = VerdeOscuro,
    background = Color(0xFF121413),
    onBackground = Blanco,
    surface = Color(0xFF1B201C),
    onSurface = Blanco,
    surfaceVariant = Color(0xFF28322A),
    onSurfaceVariant = GrisPlaceholder,
    error = RojoCerrarSesion,
    onError = Blanco
)

// Paleta para el Modo Claro (Light Theme)
private val LightColorScheme = lightColorScheme(
    primary = VerdeCanasta,
    onPrimary = Blanco,
    primaryContainer = VerdeClaro,
    onPrimaryContainer = VerdeOscuro,
    secondary = VerdeMedio,
    onSecondary = Blanco,
    secondaryContainer = GrisCampo,
    onSecondaryContainer = MarronTexto,
    tertiary = MarronTexto,
    onTertiary = Blanco,
    background = Blanco,
    onBackground = Color(0xFF1C1B1F),
    surface = Blanco,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = GrisTarjeta,
    onSurfaceVariant = GrisPlaceholder,
    error = RojoCerrarSesion,
    onError = Blanco
)

@Composable
fun CanastaVerdeAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
