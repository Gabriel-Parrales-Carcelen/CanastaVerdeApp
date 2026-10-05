package com.example.canastaverdeapp.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.canastaverdeapp.CartItem
import com.example.canastaverdeapp.FirestoreRepository
import com.example.canastaverdeapp.Producto
import com.example.canastaverdeapp.R
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(
    correoUsuario: String,
    carritoViewModel: CarritoViewModel,
    repository: FirestoreRepository,
    onVolverAlMenu: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val itemsCarrito by carritoViewModel.items.collectAsState()
    val total = itemsCarrito.sumOf { it.subtotal }
    var pedidoRealizado by remember { mutableStateOf(false) }
    var busqueda by remember { mutableStateOf("") }

    var todosLosProductos by remember { mutableStateOf<List<Producto>>(emptyList()) }
    var cargandoProductosTienda by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        cargandoProductosTienda = true
        repository.obtenerProductos().onSuccess {
            todosLosProductos = it
        }
        cargandoProductosTienda = false
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    val resultadosBusqueda = remember(busqueda, todosLosProductos) {
        val texto = busqueda.trim()
        if (texto.isEmpty()) {
            emptyList()
        } else {
            todosLosProductos.filter { p ->
                p.nombre.contains(texto, ignoreCase = true) ||
                        p.categoria.contains(texto, ignoreCase = true)
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(250.dp),
                drawerShape = RectangleShape,
                drawerContainerColor = MaterialTheme.colorScheme.primary,
                drawerContentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                ContenidoMenu(
                    correo = correoUsuario,
                    onCerrarMenu = { scope.launch { drawerState.close() } },
                    onCerrarSesion = onCerrarSesion
                )
            }
        }
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                if (!pedidoRealizado) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = stringResource(R.string.home_descripcion_abrir_menu),
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                        BarraBusqueda(
                            valor = busqueda,
                            onValorChange = { busqueda = it },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            bottomBar = {
                if (!pedidoRealizado) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                                .border(4.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                .clickable { onVolverAlMenu() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = stringResource(R.string.carrito_descripcion_home),
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(38.dp)
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                if (pedidoRealizado) {
                    // PANTALLA DE ÉXITO ("¡Tu pedido fue realizado!")
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(paddingValues),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingCart,
                            contentDescription = stringResource(R.string.home_descripcion_carrito),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(120.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = stringResource(R.string.carrito_pedido_realizado),
                            color = MaterialTheme.colorScheme.tertiary,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            lineHeight = 40.sp
                        )
                        Spacer(modifier = Modifier.height(48.dp))
                        Button(
                            onClick = {
                                carritoViewModel.vaciarCarrito()
                                onVolverAlMenu()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(30.dp),
                            modifier = Modifier
                                .height(60.dp)
                                .padding(horizontal = 32.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = stringResource(R.string.carrito_descripcion_home),
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = stringResource(R.string.carrito_boton_volver_al_menu),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                } else {
                    // PANTALLA DEL CARRITO (LISTA COMPLETA)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(paddingValues)
                            .padding(horizontal = 24.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.carrito_titulo).uppercase(),
                            color = MaterialTheme.colorScheme.tertiary,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            thickness = 2.dp,
                            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                        )

                        if (itemsCarrito.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.carrito_vacio),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        } else {
                            LazyColumn(modifier = Modifier.weight(1f)) {
                                items(itemsCarrito, key = { it.producto.id }) { item ->
                                    ItemCarritoVista(
                                        item = item,
                                        onSumar = { carritoViewModel.agregarProducto(item.producto) },
                                        onRestar = { carritoViewModel.disminuirCantidad(item.producto.id) }
                                    )
                                }
                            }
                        }

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant,
                            thickness = 2.dp,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.carrito_subtotal).uppercase(),
                                color = MaterialTheme.colorScheme.tertiary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "$${String.format(Locale.US, "%.3f", total)}",
                                color = MaterialTheme.colorScheme.tertiary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { pedidoRealizado = true },
                            enabled = itemsCarrito.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                disabledContentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(30.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.carrito_boton_realizar_pedido).uppercase(),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // MINI MENÚ DESPLEGABLE CON RESULTADOS DE LA TIENDA
                if (!pedidoRealizado && busqueda.trim().isNotEmpty()) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .align(Alignment.TopCenter)
                            .padding(top = paddingValues.calculateTopPadding())
                            .heightIn(max = 320.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(
                                        R.string.carrito_productos_tienda,
                                        resultadosBusqueda.size
                                    ).uppercase(),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                                IconButton(
                                    onClick = { busqueda = "" },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = stringResource(R.string.carrito_descripcion_cerrar_busqueda),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant,
                                thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            if (cargandoProductosTienda) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            } else if (resultadosBusqueda.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 20.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stringResource(R.string.carrito_no_se_encontraron_tienda),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 14.sp
                                    )
                                }
                            } else {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(resultadosBusqueda, key = { it.id }) { productoStore ->
                                        ItemMiniMenuTienda(
                                            producto = productoStore,
                                            cantidadEnCarrito = itemsCarrito.find { it.producto.id == productoStore.id }?.cantidad ?: 0,
                                            onAgregar = {
                                                carritoViewModel.agregarProducto(productoStore)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemMiniMenuTienda(
    producto: Producto,
    cantidadEnCarrito: Int,
    onAgregar: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.primary)
        ) {
            if (producto.imagenUrl.isNotEmpty()) {
                AsyncImage(
                    model = producto.imagenUrl,
                    contentDescription = producto.nombre,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = producto.nombre,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "$${String.format(Locale.US, "%.3f", producto.precio)}",
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            onClick = onAgregar,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(20.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
            modifier = Modifier.height(32.dp)
        ) {
            Text(
                text = if (cantidadEnCarrito > 0)
                    stringResource(R.string.carrito_boton_mas_cantidad, cantidadEnCarrito)
                else
                    stringResource(R.string.carrito_boton_agregar),
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ItemCarritoVista(item: CartItem, onSumar: () -> Unit, onRestar: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primary)
        ) {
            if (item.producto.imagenUrl.isNotEmpty()) {
                AsyncImage(
                    model = item.producto.imagenUrl,
                    contentDescription = item.producto.nombre,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.producto.nombre,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Selector tipo píldora
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.secondary)
                    .width(90.dp)
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = stringResource(R.string.home_simbolo_menos),
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .clickable { onRestar() }
                        .padding(horizontal = 8.dp)
                )
                Text(
                    text = item.cantidad.toString(),
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = stringResource(R.string.home_simbolo_mas),
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .clickable { onSumar() }
                        .padding(horizontal = 8.dp)
                )
            }
        }

        Text(
            text = "$${String.format(Locale.US, "%.3f", item.subtotal)}",
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp
        )
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
}
