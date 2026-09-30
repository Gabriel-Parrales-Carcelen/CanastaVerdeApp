// package com.example.canastaverdeapp

// import com.google.firebase.firestore.FirebaseFirestore

// class FirestoreRepository {

 //   private val db = FirebaseFirestore.getInstance()

    // Guardar un documento
   // fun guardarProducto(nombre: String, precio: Double, onResult: (Boolean) -> Unit) {
     //   val producto = hashMapOf(
       //     "nombre" to nombre,
         //   "precio" to precio
        // )
       // db.collection("productos")
         //   .add(producto)
         //   .addOnSuccessListener { onResult(true) }
          //  .addOnFailureListener { onResult(false) }
   // }

    // Leer todos los documentos
 //   fun obtenerProductos(onResult: (List<Map<String, Any>>) -> Unit) {
   //     db.collection("productos")
     //       .get()
       //     .addOnSuccessListener { resultado ->
         //       onResult(resultado.documents.mapNotNull { it.data })
           // }
           // .addOnFailureListener { onResult(emptyList()) }
    // }
 // }