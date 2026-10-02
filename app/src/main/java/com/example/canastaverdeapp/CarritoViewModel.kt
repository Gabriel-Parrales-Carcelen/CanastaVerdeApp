package com.example.canastaverdeapp.ui.screens

import androidx.lifecycle.ViewModel
import com.example.canastaverdeapp.CartItem
import com.example.canastaverdeapp.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CarritoViewModel : ViewModel() {
    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items: StateFlow<List<CartItem>> = _items.asStateFlow()

    fun agregarProducto(producto: Producto) {
        val listaActual = _items.value.toMutableList()
        val index = listaActual.indexOfFirst { it.producto.id == producto.id }

        if (index != -1) {
            val item = listaActual[index]
            listaActual[index] = item.copy(cantidad = item.cantidad + 1)
        } else {
            listaActual.add(CartItem(producto, 1))
        }
        _items.value = listaActual
    }

    fun disminuirCantidad(productoId: String) {
        val listaActual = _items.value.toMutableList()
        val index = listaActual.indexOfFirst { it.producto.id == productoId }

        if (index != -1) {
            val item = listaActual[index]
            if (item.cantidad > 1) {
                listaActual[index] = item.copy(cantidad = item.cantidad - 1)
            } else {
                listaActual.removeAt(index)
            }
            _items.value = listaActual
        }
    }

    fun vaciarCarrito() {
        _items.value = emptyList()
    }

    fun calcularTotal(): Double {
        return _items.value.sumOf { it.subtotal }
    }
}