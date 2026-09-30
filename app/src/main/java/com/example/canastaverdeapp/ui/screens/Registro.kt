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
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.canastaverdeapp.R
import com.example.canastaverdeapp.ui.theme.CanastaVerdeAppTheme
import com.example.canastaverdeapp.ui.theme.GrisCampo
import com.example.canastaverdeapp.ui.theme.GrisPlaceholder
import com.example.canastaverdeapp.ui.theme.MarronTexto
import com.example.canastaverdeapp.ui.theme.VerdeCanasta
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.launch

@Composable
fun RegistroScreen(
    modifier: Modifier = Modifier,
    // Se conecta con FirestoreRepository desde MainActivity
    onRegistrar: suspend (email: String, password: String) -> Result<String> =
        { _, _ -> Result.success("") },
    onRegistroExitoso: () -> Unit = {}
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    fun registrar() {
        val correo = email.trim()
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            error = "Ingresa un correo electrónico válido."
            return
        }
        if (password.length < 6) {
            error = "La contraseña debe tener al menos 6 caracteres."
            return
        }
        focusManager.clearFocus()
        error = null
        cargando = true
        scope.launch {
            val resultado = onRegistrar(correo, password)
            cargando = false
            resultado
                .onSuccess {
                    Toast.makeText(context, "¡Cuenta creada con éxito!", Toast.LENGTH_SHORT).show()
                    onRegistroExitoso()
                }
                .onFailure { error = mensajeDeError(it) }
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
            painter = painterResource(id = R.drawable.foto_registro),
            contentDescription = stringResource(R.string.foto_registro),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
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
                text = stringResource(R.string.registro_titulo).uppercase(),
                color = MarronTexto,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                lineHeight = 30.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

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

            Spacer(modifier = Modifier.height(14.dp))

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
                keyboardActions = KeyboardActions(onDone = { registrar() })
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

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = { registrar() },
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
                        modifier = Modifier.height(24.dp).width(24.dp)
                    )
                } else {
                    Text(
                        text = stringResource(R.string.boton_crear_cuenta).uppercase(),
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

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

@Composable
private fun CampoRegistro(
    valor: String,
    onValorChange: (String) -> Unit,
    placeholder: String,
    keyboardOptions: KeyboardOptions,
    modifier: Modifier = Modifier,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    enabled: Boolean = true
) {
    TextField(
        value = valor,
        onValueChange = onValorChange,
        enabled = enabled,
        singleLine = true,
        placeholder = {
            Text(
                text = placeholder.uppercase(),
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        },
        textStyle = TextStyle(fontFamily = FontFamily.Serif, fontSize = 15.sp),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        shape = RoundedCornerShape(6.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = GrisCampo,
            unfocusedContainerColor = GrisCampo,
            disabledContainerColor = GrisCampo,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedPlaceholderColor = GrisPlaceholder,
            unfocusedPlaceholderColor = GrisPlaceholder,
            cursorColor = VerdeCanasta
        ),
        modifier = modifier.fillMaxWidth()
    )
}

// Traduce los errores de Firebase a mensajes para el usuario.
// Importante: WeakPassword va antes porque hereda de InvalidCredentials.
private fun mensajeDeError(e: Throwable): String = when (e) {
    is FirebaseAuthUserCollisionException -> "Este correo ya está registrado."
    is FirebaseAuthWeakPasswordException -> "La contraseña es muy débil. Usa al menos 6 caracteres."
    is FirebaseAuthInvalidCredentialsException -> "El correo electrónico no es válido."
    is FirebaseNetworkException -> "Sin conexión. Revisa tu internet e inténtalo de nuevo."
    else -> "No se pudo crear la cuenta. Inténtalo de nuevo."
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
fun RegistroScreenPreview() {
    CanastaVerdeAppTheme {
        RegistroScreen()
    }
}