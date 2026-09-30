package com.example.canastaverdeapp

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreRepository {

    // Instancias oficiales de Firebase Auth y Firestore
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    /**
     * Obtiene el usuario actualmente autenticado (si existe).
     */
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    /**
     * Registra un usuario en Firebase Authentication y crea su documento en Firestore.
     * Retorna Result.success con el UID si fue exitoso, o Result.failure con la excepción.
     */
    suspend fun registrarUsuario(
        nombre: String,
        email: String,
        password: String
    ): Result<String> {
        return try {
            // 1. Crear el usuario en Firebase Authentication
            val authResult = auth.createUserWithEmailAndPassword(email.trim(), password).await()
            val user = authResult.user
                ?: throw Exception("No se pudo obtener la sesión del usuario registrado.")

            val uid = user.uid

            // 2. Preparar el modelo de datos para Firestore
            val perfilUsuario = hashMapOf(
                "uid" to uid,
                "nombre" to nombre.trim(),
                "email" to email.trim(),
                "fechaCreacion" to Timestamp.now(),
                "rol" to "cliente"
            )

            // 3. Crear el documento en la colección 'usuarios' usando el UID como ID del documento
            db.collection("usuarios")
                .document(uid)
                .set(perfilUsuario)
                .await()

            Result.success(uid)
        } catch (e: FirebaseAuthException) {
            // Errores específicos de autenticación (ej: correo ya registrado, contraseña débil)
            Result.failure(e)
        } catch (e: Exception) {
            // Errores de Firestore o de red
            Result.failure(e)
        }
    }

    /**
     * Obtiene los datos del perfil del usuario logueado desde Firestore.
     */
    suspend fun obtenerPerfilUsuario(uid: String): Result<Map<String, Any>?> {
        return try {
            val snapshot = db.collection("usuarios").document(uid).get().await()
            Result.success(snapshot.data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Cierra la sesión activa.
     */
    fun cerrarSesion() {
        auth.signOut()
    }
}