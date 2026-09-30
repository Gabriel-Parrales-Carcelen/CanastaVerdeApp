package com.example.canastaverdeapp.ui.screens

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.canastaverdeapp.R
import com.example.canastaverdeapp.ui.theme.CanastaVerdeAppTheme
import com.example.canastaverdeapp.ui.theme.MarronTexto
import com.example.canastaverdeapp.ui.theme.VerdeCanasta
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    // Se conecta con FirestoreRepository desde MainActivity
    onIniciarSesion: suspend (email: String, password: String) -> Result<String> =
        { _, _ -> Result.success("") },
    onRegistrarseClick: () -> Unit = {},
    onLoginExitoso: () -> Unit = {}
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    fun iniciarSesion() {
        val correo = email.trim()
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            error = "Ingresa un correo electrónico válido."
            return
        }
        if (password.isEmpty()) {
            error = "Ingresa tu contraseña."
            return
        }
        focusManager.clearFocus()
        error = null
        cargando = true
        scope.launch {
            val resultado = onIniciarSesion(correo, password)
            cargando = false
            resultado
                .onSuccess {
                    Toast.makeText(context, "¡Bienvenid@ de vuelta!", Toast.LENGTH_SHORT).show()
                    onLoginExitoso()
                }
                .onFailure { error = mensajeDeErrorLogin(it) }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .imePadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Foto superior
        Image(
            painter = painterResource(id = R.drawable.foto_login),
            contentDescription = stringResource(R.string.foto_login),
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.95f)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.login_titulo).uppercase(),
                color = MarronTexto,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                lineHeight = 28.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            CampoRegistro(
                valor = email,
                onValorChange = { email = it },
                placeholder = stringResource(R.string.hint_correo),
                enabled = !cargando,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            CampoRegistro(
                valor = password,
                onValorChange = { password = it },
                placeholder = stringResource(R.string.hint_contrasena),
                enabled = !cargando,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { iniciarSesion() })
            )

            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { iniciarSesion() },
                enabled = !cargando,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VerdeCanasta,
                    contentColor = Color.White,
                    disabledContainerColor = VerdeCanasta.copy(alpha = 0.6f),
                    disabledContentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (cargando) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = stringResource(R.string.boton_iniciar_sesion).uppercase(),
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.primera_vez),
                color = MarronTexto,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            TextButton(onClick = onRegistrarseClick, enabled = !cargando) {
                Text(
                    text = stringResource(R.string.boton_registrarse),
                    color = VerdeCanasta,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textDecoration = TextDecoration.Underline
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

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

// Traduce los errores de Firebase a mensajes para el usuario.
private fun mensajeDeErrorLogin(e: Throwable): String = when (e) {
    is FirebaseAuthInvalidUserException,
    is FirebaseAuthInvalidCredentialsException -> "Correo o contraseña incorrectos."
    is FirebaseTooManyRequestsException -> "Demasiados intentos. Inténtalo más tarde."
    is FirebaseNetworkException -> "Sin conexión. Revisa tu internet e inténtalo de nuevo."
    else -> "No se pudo iniciar sesión. Inténtalo de nuevo."
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
fun LoginScreenPreview() {
    CanastaVerdeAppTheme {
        LoginScreen()
    }
}