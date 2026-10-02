package com.example.canastaverdeapp.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import com.example.canastaverdeapp.notification.NotificationHelper
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.canastaverdeapp.Producto
import com.example.canastaverdeapp.R
import com.example.canastaverdeapp.ui.theme.CanastaVerdeAppTheme
import com.example.canastaverdeapp.ui.theme.GrisCampo
import com.example.canastaverdeapp.ui.theme.GrisPlaceholder
import com.example.canastaverdeapp.ui.theme.GrisTarjeta
import com.example.canastaverdeapp.ui.theme.RojoCerrarSesion
import com.example.canastaverdeapp.ui.theme.VerdeCanasta
import com.example.canastaverdeapp.ui.theme.VerdeMedio
import kotlinx.coroutines.launch
import java.util.Locale

// ---------------------------------------------------------------
// Pantalla con lógica: obtiene los datos del ViewModel (Firestore)
// ---------------------------------------------------------------
@Composable
fun HomeScreen(
    correoUsuario: String,
    onCerrarSesion: () -> Unit,
    onCarritoClick: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val productosFiltrados = filtrarProductos(
        productos = viewModel.productos,
        busqueda = viewModel.busqueda,
        categoria = viewModel.categoriaSeleccionada
    )

    HomeContent(
        correoUsuario = correoUsuario,
        productos = productosFiltrados,
        hayProductosEnTotal = viewModel.productos.isNotEmpty(),
        cargando = viewModel.cargando,
        error = viewModel.error,
        busqueda = viewModel.busqueda,
        categoriaSeleccionada = viewModel.categoriaSeleccionada,
        cantidades = viewModel.cantidades,
        onBusquedaChange = { viewModel.busqueda = it },
        onCategoriaClick = viewModel::seleccionarCategoria,
        onAgregar = viewModel::agregar,
        onQuitar = viewModel::quitar,
        onReintentar = viewModel::cargarProductos,
        onCarritoClick = onCarritoClick,
        onCerrarSesion = {
            viewModel.reiniciar()
            onCerrarSesion()
        }
    )
}

// ---------------------------------------------------------------
// Pantalla visual (sin lógica): sirve también para el Preview
// ---------------------------------------------------------------
@Composable
fun HomeContent(
    correoUsuario: String,
    productos: List<Producto>,
    hayProductosEnTotal: Boolean,
    cargando: Boolean,
    error: String?,
    busqueda: String,
    categoriaSeleccionada: String?,
    cantidades: Map<String, Int>,
    onBusquedaChange: (String) -> Unit,
    onCategoriaClick: (String) -> Unit,
    onAgregar: (Producto) -> Unit,
    onQuitar: (Producto) -> Unit,
    onReintentar: () -> Unit,
    onCarritoClick: () -> Unit,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Con el menú abierto, "atrás" lo cierra
    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        modifier = modifier,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(250.dp),
                drawerShape = RectangleShape,
                drawerContainerColor = VerdeCanasta,
                drawerContentColor = Color.White
            ) {
                ContenidoMenu(
                    correo = correoUsuario,
                    onCerrarMenu = { scope.launch { drawerState.close() } },
                    onCerrarSesion = onCerrarSesion
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Barra superior: menú + buscador
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
                        contentDescription = "Abrir menú",
                        tint = VerdeCanasta,
                        modifier = Modifier.size(34.dp)
                    )
                }
                BarraBusqueda(
                    valor = busqueda,
                    onValorChange = onBusquedaChange,
                    modifier = Modifier.weight(1f)
                )
            }

            // Banner + categorías + productos (todo hace scroll junto)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    BannerCarrusel()
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    FilaCategorias(
                        seleccionada = categoriaSeleccionada,
                        onClick = onCategoriaClick
                    )
                }

                when {
                    cargando -> item(span = { GridItemSpan(maxLineSpan) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = VerdeCanasta)
                        }
                    }

                    error != null -> item(span = { GridItemSpan(maxLineSpan) }) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = error,
                                color = Color.Black,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = onReintentar,
                                colors = ButtonDefaults.buttonColors(containerColor = VerdeCanasta)
                            ) {
                                Text("Reintentar", color = Color.White)
                            }
                        }
                    }

                    productos.isEmpty() -> item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            text = if (hayProductosEnTotal) "No se encontraron productos."
                            else "Aún no hay productos disponibles.",
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                        )
                    }

                    else -> items(productos, key = { it.id }) { producto ->
                        ProductoCard(
                            producto = producto,
                            cantidad = cantidades[producto.id] ?: 0,
                            onAgregar = { onAgregar(producto) },
                            onQuitar = { onQuitar(producto) }
                        )
                    }
                }
            }

            BarraInferior(onCarritoClick = onCarritoClick)
        }
    }
}

// ---------------------------------------------------------------
// Menú lateral
// ---------------------------------------------------------------
@Composable
private fun ContenidoMenu(
    correo: String,
    onCerrarMenu: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        IconButton(onClick = onCerrarMenu) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Cerrar menú",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = VerdeCanasta,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = correo,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        val context = LocalContext.current

        Button(
            onClick = {
                NotificationHelper.showNotification(
                    context = context,
                    title = "¡Oferta en Canasta Verde! 🧺",
                    body = "¡Aprovecha un 20% de descuento en frutas y verduras seleccionadas hoy!"
                )
            },
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = VerdeCanasta
            ),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
            modifier = Modifier.height(30.dp)
        ) {
            Text(
                text = "PROBAR NOTIFICACIÓN",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = onCerrarSesion,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = RojoCerrarSesion,
                contentColor = Color.White
            ),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
            modifier = Modifier.height(30.dp)
        ) {
            Text(
                text = stringResource(R.string.boton_cerrar_sesion).uppercase(),
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = painterResource(id = R.drawable.logo_canasta_verde_blanco),
            contentDescription = stringResource(R.string.logo_canasta_verde),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
        )
    }
}

// ---------------------------------------------------------------
// Buscador
// ---------------------------------------------------------------
@Composable
private fun BarraBusqueda(
    valor: String,
    onValorChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    BasicTextField(
        value = valor,
        onValueChange = onValorChange,
        singleLine = true,
        textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),
        cursorBrush = SolidColor(VerdeCanasta),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
        modifier = modifier,
        decorationBox = { campo ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .clip(CircleShape)
                    .background(GrisCampo)
                    .padding(start = 16.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (valor.isEmpty()) {
                        Text(
                            text = stringResource(R.string.hint_busqueda).uppercase(),
                            color = GrisPlaceholder,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                    campo()
                }
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(VerdeCanasta)
                        .clickable { focusManager.clearFocus() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    )
}

// ---------------------------------------------------------------
// Banner con flechas
// ---------------------------------------------------------------
@Composable
private fun BannerCarrusel(modifier: Modifier = Modifier) {
    // Para más banners, agrega las imágenes a drawable y ponlas aquí:
    // listOf(R.drawable.banner_1, R.drawable.banner_2, ...)
    val banners = listOf(R.drawable.banner_1)
    val pagerState = rememberPagerState(pageCount = { banners.size })
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pagina ->
            Image(
                painter = painterResource(id = banners[pagina]),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        FlechaBanner(
            icono = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            descripcion = "Anterior",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 6.dp)
        ) {
            scope.launch {
                pagerState.animateScrollToPage(
                    (pagerState.currentPage - 1 + banners.size) % banners.size
                )
            }
        }

        FlechaBanner(
            icono = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            descripcion = "Siguiente",
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 6.dp)
        ) {
            scope.launch {
                pagerState.animateScrollToPage(
                    (pagerState.currentPage + 1) % banners.size
                )
            }
        }
    }
}

@Composable
private fun FlechaBanner(
    icono: ImageVector,
    descripcion: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .size(26.dp)
            .clip(CircleShape)
            .background(VerdeCanasta)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icono,
            contentDescription = descripcion,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
}

// ---------------------------------------------------------------
// Categorías (los 3 recuadros)
// ---------------------------------------------------------------
@Composable
private fun FilaCategorias(
    seleccionada: String?,
    onClick: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CATEGORIAS.forEach { categoria ->
            val activa = seleccionada == categoria
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(CircleShape)
                    .background(if (activa) VerdeCanasta else Color.White)
                    .border(1.dp, VerdeCanasta, CircleShape)
                    .clickable { onClick(categoria) }
                    .padding(horizontal = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = categoria,
                    color = if (activa) Color.White else VerdeCanasta,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    lineHeight = 13.sp,
                    maxLines = 2,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ---------------------------------------------------------------
// Tarjeta de producto
// ---------------------------------------------------------------
@Composable
private fun ProductoCard(
    producto: Producto,
    cantidad: Int,
    onAgregar: () -> Unit,
    onQuitar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(GrisTarjeta)
    ) {
        AsyncImage(
            model = producto.imagenUrl,
            contentDescription = producto.nombre,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)

        )

        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
            Text(
                text = "$${String.format(Locale.US, "%.2f", producto.precio)}",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Botón "+" o selector "- 1 +"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (cantidad == 0) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(VerdeMedio)
                            .clickable(onClick = onAgregar),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("+", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(VerdeMedio),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clickable(onClick = onQuitar),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("-", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                        Text(
                            text = cantidad.toString(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clickable(onClick = onAgregar),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = producto.nombre,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                lineHeight = 19.sp,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ---------------------------------------------------------------
// Barra verde inferior con el carrito (sin acción por ahora)
// ---------------------------------------------------------------
@Composable
private fun BarraInferior(onCarritoClick: () -> Unit) {
    val insetInferior = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(78.dp + insetInferior)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(44.dp + insetInferior)
                .background(VerdeCanasta)
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(72.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(4.dp, VerdeCanasta, CircleShape)
                .clickable(onClick = onCarritoClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Carrito",
                tint = VerdeCanasta,
                modifier = Modifier.size(38.dp)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
fun HomeContentPreview() {
    CanastaVerdeAppTheme {
        HomeContent(
            correoUsuario = "correo@gmail.com",
            productos = listOf(
                Producto("1", "Leche de vaca entera 1L", 9.99, "Refrigerados", ""),
                Producto("2", "Manzanas Rojas 1K", 2.99, "Frutas y Verduras", ""),
                Producto("3", "Pan de masa madre 1U", 4.99, "Básicos", ""),
                Producto("4", "Crema de maní 500gr", 12.99, "Básicos", "")
            ),
            hayProductosEnTotal = true,
            cargando = false,
            error = null,
            busqueda = "",
            categoriaSeleccionada = null,
            cantidades = mapOf("2" to 1),
            onBusquedaChange = {},
            onCategoriaClick = {},
            onAgregar = {},
            onQuitar = {},
            onReintentar = {},
            onCarritoClick = {},
            onCerrarSesion = {}
        )
    }
}