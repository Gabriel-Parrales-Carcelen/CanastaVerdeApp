package com.example.canastaverdeapp.notification

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class CanastaFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Nuevo token FCM recibido: $token")
        // Aquí se puede enviar el token a tu backend o servidor de Firestore si fuera necesario
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Mensaje FCM recibido de: ${remoteMessage.from}")

        // Obtener título y cuerpo de la notificación
        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: "Canasta Verde"

        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: remoteMessage.data["message"]
            ?: "Tienes un nuevo mensaje"

        NotificationHelper.showNotification(
            context = applicationContext,
            title = title,
            body = body
        )
    }

    companion object {
        private const val TAG = "CanastaFCMService"
    }
}
