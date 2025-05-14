package com.example.lespetitespuces.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// import androidx.compose.runtime.livedata.observeAsState // Potentially not needed here if categories are passed directly
// import androidx.lifecycle.viewmodel.compose.viewModel // Explicitly removed as per request

import com.example.lespetitespuces.Model.CategoryModel
import com.example.lespetitespuces.R
// import com.example.lespetitespuces.ui.theme.MainViewModel // ViewModel type might not be needed in this file anymore

val Italianno = FontFamily(
    Font(R.font.italianno_regular)
)

@Composable
fun MainScreen(
    categories: List<CategoryModel> // Categories are now passed as a direct parameter
) {
    // La logique pour obtenir 'categories' (par exemple, depuis un ViewModel)
    // doit maintenant être gérée par l'appelant de MainScreen.

    Scaffold(
        topBar = { AppHeader() },
        bottomBar = { AppFooter() },
        content = { paddingValues ->
            HomeContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                categories = categories  // Passage des catégories reçues en paramètre
            )
        }
    )
}

@Composable
private fun AppHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Les petites puces",
                fontSize = 40.sp,
                fontFamily = Italianno,
            )
            Text(
                text = "L'art de gamer avec style",
                fontSize = 30.sp,
                fontFamily = Italianno,
            )
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color.Red, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "AN",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    categories: List<CategoryModel> = emptyList()
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        SearchBar()

        Spacer(modifier = Modifier.height(16.dp))

        CategoryTabs(categories = categories)

        Spacer(modifier = Modifier.height(8.dp))

        Divider(
            color = Color.Black,
            thickness = 1.dp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Liste des produits à implémenter",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

@Composable
fun SearchBar() {
    var searchText by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(20.dp))
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(20.dp))
            .background(Color.White)
            .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Rechercher",
                tint = Color.Gray
            )

            Spacer(modifier = Modifier.width(8.dp))

            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("chercher") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun CategoryTabs(categories: List<CategoryModel>) {
    val defaultCategory = CategoryModel(title = "Tous", id = -1)
    val allCategories = remember(categories) {
        listOf(defaultCategory) + categories
    }

    var selectedCategoryId by remember { mutableStateOf(-1) }
    val scrollState = rememberScrollState() // <<--- ÉTAT POUR LE DÉFILEMENT

    // Enveloppez le Row avec horizontalScroll
    Row(
        modifier = Modifier
            .fillMaxWidth() // Le Row externe peut remplir la largeur
            .horizontalScroll(scrollState), // <<--- AJOUTER LE DÉFILEMENT HORIZONTAL
        horizontalArrangement = Arrangement.spacedBy(8.dp)
        // verticalAlignment = Alignment.CenterVertically // Peut être utile si les hauteurs varient
    ) {
        if (allCategories.isEmpty() || (allCategories.size == 1 && allCategories.first().id == -1 && categories.isEmpty())) {
            // Le Row interne aura une taille fixe, donc le message peut être affiché à l'extérieur si besoin
            // Ou gardez-le ici, il défilera aussi s'il est trop long
            Text(
                text = "Chargement des catégories ou aucune catégorie disponible...",
                modifier = Modifier.padding(start = 16.dp, end = 16.dp) // Assurez-vous qu'il soit visible
            )
        } else {
            allCategories.forEach { category ->
                val isSelected = category.id == selectedCategoryId

                Button(
                    onClick = { selectedCategoryId = category.id },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Color.Red else Color.LightGray,
                        contentColor = if (isSelected) Color.White else Color.Black
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    modifier = Modifier.height(36.dp) // Les boutons ont une hauteur fixe
                ) {
                    Text(text = category.title)
                }
            }
        }
    }
}

@Composable
private fun AppFooter() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.Red)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FooterImageItem(
                drawableResId = R.drawable.home,
                contentDescription = "Accueil",
                onClick = { /* TODO */ }
            )
            FooterImageItem(
                drawableResId = R.drawable.favorite,
                contentDescription = "Favoris",
                onClick = { /* TODO */ }
            )
            FooterImageItem(
                drawableResId = R.drawable.support_agent,
                contentDescription = "support",
                onClick = { /* TODO */ }
            )
            FooterImageItem(
                drawableResId = R.drawable.account_circle,
                contentDescription = "account",
                onClick = { /* TODO */ }
            )
        }
    }
}

@Composable
private fun FooterImageItem(
    drawableResId: Int,
    contentDescription: String,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(40.dp)
    ) {
        Icon(
            painter = painterResource(id = drawableResId),
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

// Pour utiliser ce MainScreen, vous devrez l'appeler depuis un autre Composable
// en lui passant la liste des catégories. Par exemple:
//
// Dans votre activité ou un Composable parent :
// @Composable
// fun MyApp() {
//     // Exemple: catégories statiques pour démonstration
//     val sampleCategories = listOf(
//         CategoryModel(title = "PC Gamer", id = 1),
//         CategoryModel(title = "Consoles", id = 2),
//         CategoryModel(title = "Accessoires", id = 3)
//     )
//
//     // Si vous utilisez toujours un ViewModel à un niveau supérieur pour obtenir les catégories:
//     // val mainViewModel: MainViewModel = ... (obtenu d'une autre manière, ex: Hilt, ou ViewModelProvider)
//     // val categoriesFromVm by mainViewModel.loadCategory().observeAsState(emptyList())
//
//     MainScreen(categories = sampleCategories /* ou categoriesFromVm ?: emptyList() */)
// }