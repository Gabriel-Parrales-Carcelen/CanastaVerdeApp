package com.example.canastaverdeapp.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.canastaverdeapp.FirestoreRepository
import com.example.canastaverdeapp.Producto
import kotlinx.coroutines.launch
import java.text.Normalizer

val CATEGORIAS = listOf("Básicos", "Frutas y Verduras", "Refrigerados")

class HomeViewModel : ViewModel() {

    private val repository = FirestoreRepository()

    var productos by mutableStateOf<List<Producto>>(emptyList())
        private set
    var cargando by mutableStateOf(true)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    var busqueda by mutableStateOf("")
    var categoriaSeleccionada by mutableStateOf<String?>(null)
        private set

    // id del producto -> cantidad elegida
    val cantidades = mutableStateMapOf<String, Int>()

    init {
        cargarProductos()
    }

    fun cargarProductos() {
        viewModelScope.launch {
            cargando = true
            error = null
            repository.obtenerProductos()
                .onSuccess { productos = it }
                .onFailure {
                    error = "No se pudieron cargar los productos. Revisa tu conexión e inténtalo de nuevo."
                }
            cargando = false
        }
    }

    // Tocar la categoría activa la desactiva y vuelve a mostrar todo
    fun seleccionarCategoria(categoria: String) {
        categoriaSeleccionada = if (categoriaSeleccionada == categoria) null else categoria
    }

    fun agregar(producto: Producto) {
        cantidades[producto.id] = (cantidades[producto.id] ?: 0) + 1
    }

    fun quitar(producto: Producto) {
        val nueva = (cantidades[producto.id] ?: 0) - 1
        if (nueva <= 0) cantidades.remove(producto.id) else cantidades[producto.id] = nueva
    }

    // Se llama al cerrar sesión para que el siguiente usuario empiece limpio
    fun reiniciar() {
        busqueda = ""
        categoriaSeleccionada = null
        cantidades.clear()
    }
}

fun filtrarProductos(
    productos: List<Producto>,
    busqueda: String,
    categoria: String?
): List<Producto> {
    val texto = normalizar(busqueda)
    val cat = categoria?.let { normalizar(it) }
    return productos.filter { p ->
        val coincideCategoria = cat == null || normalizar(p.categoria) == cat
        val coincideTexto = texto.isEmpty() ||
                normalizar(p.nombre).contains(texto) ||
                normalizar(p.categoria).contains(texto)
        coincideCategoria && coincideTexto
    }
}

// Ignora mayúsculas y tildes: "Básicos" == "basicos"
private fun normalizar(texto: String): String =
    Normalizer.normalize(texto.trim(), Normalizer.Form.NFD)
        .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
        .lowercase()