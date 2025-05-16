// Créez ce fichier dans ui/screens
// fichier: com/example/lespetitespuces/ui/screens/CheckoutScreen.kt
package com.example.lespetitespuces.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource // Pour les logos Mastercard/Visa
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lespetitespuces.R // Assurez-vous d'avoir les logos dans drawable
import com.example.lespetitespuces.model.CartItemModel
import com.example.lespetitespuces.navigation.AppRoutes
import com.example.lespetitespuces.ui.theme.CustomRed // Vos couleurs
import com.example.lespetitespuces.viewmodel.AuthViewModel
import com.example.lespetitespuces.viewmodel.CartActionResult
import com.example.lespetitespuces.viewmodel.CartViewModel

// Data class pour représenter une méthode de paiement (pour la sélection)
data class PaymentMethod(
    val id: String,
    val name: String, // "Carte crédit", "Carte débit"
    val lastFourDigits: String, // "234******3556"
    val logoResId: Int // R.drawable.mastercard_logo, R.drawable.visa_logo
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel()
) {
    val cartItems by cartViewModel.userCart.observeAsState(emptyList())
    val cartActionResult by cartViewModel.cartActionResult.observeAsState()
    val context = LocalContext.current

    // Exemple de méthodes de paiement (vous pourriez les charger dynamiquement)
    val paymentMethods = listOf(
        PaymentMethod("mastercard_1", "Carte crédit", "234******3556", R.drawable.mastercard_logo),
        PaymentMethod("visa_1", "Carte débit", "234******3556", R.drawable.visa_logo)
    )
    var selectedPaymentMethodId by remember { mutableStateOf(paymentMethods.firstOrNull()?.id) }
    var savePaymentInfo by remember { mutableStateOf(true) }

    val subtotal = cartItems.sumOf { it.price * it.quantity }
    val taxes = 20.0 // Exemple de taxe fixe
    val shipping = 20.0 // Exemple de frais de livraison fixes
    val totalOrderPrice = subtotal + taxes + shipping

    // Gérer le résultat de l'action du panier (après la simulation d'achat)
    LaunchedEffect(cartActionResult) {
        when (val result = cartActionResult) {
            is CartActionResult.Success -> {
                // L'achat (simulation) a réussi et le stock a été diminué
                Toast.makeText(context, "Commande confirmée avec succès !", Toast.LENGTH_LONG).show()
                // Naviguer vers l'écran de succès et nettoyer la pile de retour
                navController.navigate(AppRoutes.ORDER_SUCCESS_SCREEN) {
                    popUpTo(AppRoutes.MAIN_SCREEN) { inclusive = false } // Retour à l'accueil, pas au panier/checkout
                    launchSingleTop = true
                }
                cartViewModel.resetCartActionResult()
            }
            is CartActionResult.Error -> {
                Toast.makeText(context, "Erreur lors de la commande: ${result.message}", Toast.LENGTH_LONG).show()
                cartViewModel.resetCartActionResult()
            }
            else -> {}
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Résumé des commandes", fontWeight = FontWeight.Bold) },
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Pour défiler si le contenu est long
                .padding(16.dp)
        ) {
            // Section Résumé de la commande
            OrderSummaryItem("Commande", "${String.format("%.0f", subtotal)}DH")
            OrderSummaryItem("Taxes", "${String.format("%.0f", taxes)}DH")
            OrderSummaryItem("Livraison", "${String.format("%.0f", shipping)}DH")

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold))
                Text(
                    "${String.format("%.0f", totalOrderPrice)}DH",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, color = CustomRed)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section Méthode de paiement
            Text("Méthode de paiement", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(12.dp))

            paymentMethods.forEach { method ->
                PaymentMethodItem(
                    method = method,
                    isSelected = selectedPaymentMethodId == method.id,
                    onSelected = { selectedPaymentMethodId = method.id }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { savePaymentInfo = !savePaymentInfo }
            ) {
                Checkbox(
                    checked = savePaymentInfo,
                    onCheckedChange = { savePaymentInfo = it },
                    colors = CheckboxDefaults.colors(checkedColor = CustomRed)
                )
                Text("Enregistrer les informations de paiement", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.weight(1f)) // Pousse le bouton en bas

            // Bouton Payer maintenant
            Button(
                onClick = {
                    if (selectedPaymentMethodId == null) {
                        Toast.makeText(context, "Veuillez sélectionner une méthode de paiement.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (authViewModel.isUserLoggedIn()) {
                        // SIMULATION DE PAIEMENT
                        Toast.makeText(context, "Paiement en cours de traitement...", Toast.LENGTH_SHORT).show()
                        // Déclencher la logique de confirmation d'achat et de diminution du stock
                        cartViewModel.confirmPurchaseAndDecreaseStock(cartItems)
                    } else {
                        Toast.makeText(context, "Veuillez vous connecter pour payer.", Toast.LENGTH_SHORT).show()
                        navController.navigate(AppRoutes.LOGIN_SCREEN)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CustomRed), // Votre CustomRed
                enabled = cartItems.isNotEmpty() // Désactiver si le panier est vide (ne devrait pas arriver ici)
            ) {
                Text("Pays maintenant", fontSize = 18.sp, color = Color.White)
            }
        }
    }
}

@Composable
fun OrderSummaryItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun PaymentMethodItem(
    method: PaymentMethod,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelected)
            .border(
                BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) CustomRed else Color.LightGray
                ),
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) CustomRed.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = method.logoResId),
                contentDescription = method.name,
                modifier = Modifier.height(30.dp).padding(end = 12.dp) // Ajustez la taille du logo
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(method.name, fontWeight = FontWeight.SemiBold)
                Text(method.lastFourDigits, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            RadioButton(
                selected = isSelected,
                onClick = onSelected,
                colors = RadioButtonDefaults.colors(selectedColor = CustomRed)
            )
        }
    }
}

// Assurez-vous d'avoir les images des logos (par exemple, mastercard_logo.png, visa_logo.png)
// dans votre dossier res/drawable.