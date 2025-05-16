// fichier: com/example/lespetitespuces/ui/screens/CartScreen.kt
package com.example.lespetitespuces.ui.screens

import android.widget.Toast
// import androidx.compose.foundation.Image // Pas utilisé directement ici, mais par AsyncImage
import androidx.compose.foundation.background
// import androidx.compose.foundation.clickable // Pas utilisé directement ici
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape // Importé
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete // Pour l'icône de suppression
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
// import androidx.compose.ui.res.painterResource // Pas utilisé directement ici pour les logos
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.lespetitespuces.R // Pour R.drawable.placeholder_image
import com.example.lespetitespuces.model.CartItemModel
import com.example.lespetitespuces.navigation.AppRoutes
import com.example.lespetitespuces.ui.theme.CustomRed
// import com.example.lespetitespuces.ui.theme.White // Remplacé par Color.White si c'est le cas

import com.example.lespetitespuces.viewmodel.AuthViewModel
import com.example.lespetitespuces.viewmodel.CartActionResult
import com.example.lespetitespuces.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel()
) {
    val cartItems by cartViewModel.userCart.observeAsState(emptyList())
    val cartActionResult by cartViewModel.cartActionResult.observeAsState()
    val context = LocalContext.current

    val totalQuantity by remember(cartItems) { derivedStateOf { cartItems.sumOf { it.quantity } } }
    val totalPrice by remember(cartItems) { derivedStateOf { cartItems.sumOf { it.price * it.quantity } } }

    LaunchedEffect(cartActionResult) {
        when (val result = cartActionResult) {
            is CartActionResult.Success -> {
                // Ce message est maintenant plus générique, car la confirmation d'achat
                // se fait dans CheckoutScreen.
                Toast.makeText(context, "Opération panier réussie !", Toast.LENGTH_SHORT).show()
                cartViewModel.resetCartActionResult()
            }
            is CartActionResult.Error -> {
                Toast.makeText(context, "Erreur panier: ${result.message}", Toast.LENGTH_LONG).show()
                cartViewModel.resetCartActionResult()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Votre Panier", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Column(modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant).padding(16.dp)) {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Quantité totale", fontWeight = FontWeight.Medium)
                        Text("$totalQuantity", fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total HT", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            text = "${String.format("%.0f", totalPrice)}DH",
                            color = CustomRed,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            // MODIFICATION ICI : Naviguer vers CheckoutScreen
                            if (authViewModel.isUserLoggedIn()) {
                                navController.navigate(AppRoutes.CHECKOUT_SCREEN)
                            } else {
                                Toast.makeText(context, "Veuillez vous connecter pour continuer.", Toast.LENGTH_SHORT).show()
                                navController.navigate(AppRoutes.LOGIN_SCREEN)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CustomRed,
                            contentColor = Color.White // Assurez-vous que White est Color.White ou votre couleur définie
                        )
                    ) {
                        // Le texte du bouton est maintenant "Procéder au paiement" ou similaire
                        Text(text = "Procéder au Paiement", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Livraison en 48 h partout au Maroc",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    ) { paddingValues ->
        if (!authViewModel.isUserLoggedIn() && cartItems.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Veuillez vous connecter pour voir ou ajouter des articles à votre panier.",
                    style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navController.navigate(AppRoutes.LOGIN_SCREEN) }) {
                    Text("Se connecter / S'inscrire")
                }
            }
            return@Scaffold
        }

        if (cartItems.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Votre panier est vide.", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navController.popBackStack(AppRoutes.MAIN_SCREEN, inclusive = false) }) {
                    Text("Continuer les achats")
                }
            }
        } else {
            Column(modifier = Modifier.padding(paddingValues)) {
                Text(
                    text = "Votre Panier",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                )
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(cartItems, key = { it.itemId }) { item ->
                        CartItemCardAdapter(
                            cartItem = item,
                            onIncrease = {
                                if (item.quantity < item.stock) {
                                    cartViewModel.updateCartItemQuantity(item.itemId, item.quantity + 1, item.stock)
                                } else {
                                    Toast.makeText(context, "Stock maximum atteint.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onDecrease = {
                                cartViewModel.updateCartItemQuantity(item.itemId, item.quantity - 1, item.stock)
                            }
                            // onRemove = { cartViewModel.removeItemFromCart(item.itemId) } // Optionnel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemCardAdapter(
    cartItem: CartItemModel,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(cartItem.picUrl.ifEmpty { null })
                    .crossfade(true)
//                    .placeholder(R.drawable.placeholder_image)
//                    .error(R.drawable.placeholder_image)
                    .build(),
                contentDescription = cartItem.title,
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)).background(Color.LightGray),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(cartItem.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${String.format("%.0f", cartItem.price)}DH",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = CustomRed
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onDecrease,
                            modifier = Modifier.size(30.dp).background(CustomRed, RoundedCornerShape(8.dp)),
                            colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                        ) { Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold) }
                        Text(
                            "${cartItem.quantity}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        IconButton(
                            onClick = onIncrease,
                            modifier = Modifier.size(30.dp).background(CustomRed, RoundedCornerShape(8.dp)),
                            colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                        ) { Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold) }
                    }
                }
            }
            // Optionnel: Bouton de suppression direct pour l'item
            IconButton(onClick = { /* cartViewModel.removeItemFromCart(cartItem.itemId) */ },
                modifier = Modifier.align(Alignment.Top) // Aligner en haut à droite de la Card Row
            ) {
                Icon(Icons.Filled.Delete, contentDescription = "Supprimer item", tint = Color.Gray)
            }
        }
    }
}