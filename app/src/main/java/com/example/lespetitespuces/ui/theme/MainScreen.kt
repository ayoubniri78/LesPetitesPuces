package com.example.lespetitespuces.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items // important pour LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage // Pour charger les images depuis une URL
import coil.request.ImageRequest
import com.example.lespetitespuces.Model.CategoryModel
import com.example.lespetitespuces.R
import com.example.lespetitespuces.model.ItemsModel // Importez ItemsModel

import androidx.navigation.NavController // Importer NavController




val Italianno = FontFamily(
    Font(R.font.italianno_regular)
)

@Composable
fun MainScreen(
    categories: List<CategoryModel>,
    allItems: List<ItemsModel> // Accepter tous les items
) {
    var selectedCategoryId by remember { mutableStateOf(-1) } // -1 pour "Tous"

    Scaffold(
        topBar = { AppHeader() },
        bottomBar = { AppFooter() },
        content = { paddingValues ->
            HomeContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                categories = categories,
                allItems = allItems,
                selectedCategoryId = selectedCategoryId,
                onCategorySelected = { categoryId ->
                    selectedCategoryId = categoryId
                }
            )
        }
    )
}

@Composable
private fun AppHeader() { // Pas de changement ici
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            Text(text = "Les petites puces", fontSize = 40.sp, fontFamily = Italianno)
            Text(text = "L'art de gamer avec style", fontSize = 30.sp, fontFamily = Italianno)
        }
        Box(
            modifier = Modifier.size(40.dp).background(Color.Red, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "AN", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    categories: List<CategoryModel>,
    allItems: List<ItemsModel>,
    selectedCategoryId: Int,
    onCategorySelected: (Int) -> Unit
) {
    val displayedItems = remember(allItems, selectedCategoryId) {
        if (selectedCategoryId == -1) { // -1 est l'ID pour "Tous"
            allItems
        } else {
            allItems.filter { item ->
                // Assurez-vous que la comparaison des ID est correcte
                // ItemsModel.categoryId est un String, CategoryModel.id est un Int
                item.categoryId.toIntOrNull() == selectedCategoryId
            }
        }
    }

    Column(
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        SearchBar()
        Spacer(modifier = Modifier.height(16.dp))
        CategoryTabs(
            categories = categories,
            selectedCategoryId = selectedCategoryId,
            onCategorySelected = onCategorySelected
        )
        Spacer(modifier = Modifier.height(8.dp))
        Divider(color = Color.Black, thickness = 1.dp, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))

        // Afficher la liste des produits
        if (displayedItems.isEmpty() && allItems.isNotEmpty()) { // Si le filtre ne donne rien mais qu'il y a des items
            Text(
                text = "Aucun produit trouvé pour cette catégorie.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp)
            )
        } else if (allItems.isEmpty() && categories.isNotEmpty()){ // Si aucun item n'est chargé au départ
            Text(
                text = "Chargement des produits...",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp)
            )
        }
        else {
            ProductList(items = displayedItems)
        }
    }
}

@Composable
fun SearchBar() { // Pas de changement ici
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
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Rechercher", tint = Color.Gray)
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
fun CategoryTabs(
    categories: List<CategoryModel>,
    selectedCategoryId: Int, // ID de la catégorie actuellement sélectionnée
    onCategorySelected: (Int) -> Unit // Callback pour notifier la sélection
) {
    val defaultCategory = CategoryModel(title = "Tous", id = -1) // ID -1 pour "Tous"
    val allDisplayCategories = remember(categories) {
        listOf(defaultCategory) + categories
    }
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (allDisplayCategories.isEmpty() || (allDisplayCategories.size == 1 && allDisplayCategories.first().id == -1 && categories.isEmpty())) {
            Text("Chargement...", modifier = Modifier.padding(start = 16.dp, end = 16.dp))
        } else {
            allDisplayCategories.forEach { category ->
                val isSelected = category.id == selectedCategoryId // Utiliser l'ID passé
                Button(
                    onClick = { onCategorySelected(category.id) }, // Appeler le callback
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) Color.Red else Color.LightGray,
                        contentColor = if (isSelected) Color.White else Color.Black
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(text = category.title)
                }
            }
        }
    }
}

@Composable
fun ProductList(items: List<ItemsModel>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2), // 2 colonnes comme dans le design
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp), // Espace entre les colonnes
        verticalArrangement = Arrangement.spacedBy(8.dp)     // Espace entre les lignes
    ) {
        items(items, key = { item -> item.title + item.categoryId }) { item -> // Clé unique pour la performance
            ProductItemCard(item = item)
        }
    }
}

@Composable
fun ProductItemCard(item: ItemsModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth() // La carte prendra la largeur de sa cellule de grille
            // .height(280.dp) // Hauteur fixe si vous voulez, ou laissez dynamique
            .clickable { /* TODO: Action au clic sur l'item */ },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.picUrl.firstOrNull()) // Prendre la première image de la liste
                    .crossfade(true)
//                    .placeholder(R.drawable.placeholder_image) // Ajoutez une image placeholder dans vos drawables
//                    .error(R.drawable.placeholder_image) // Image si erreur de chargement
                    .build(),
                contentDescription = item.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp) // Hauteur pour l'image
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop // Ou ContentScale.Fit selon le rendu souhaité
            )

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = item.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.description, // Peut-être tronquer ou choisir une partie spécifique
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 2, // Limiter la description
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFC107), // Couleur Jaune/Or pour l'étoile
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = item.rating.toString(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Icon(
                        imageVector = Icons.Filled.FavoriteBorder, // Ou Icons.Filled.Favorite si favori
                        contentDescription = "Favorite",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                            .clickable { /* TODO: Logique pour ajouter/retirer des favoris */ }
                    )
                }
                // Vous pouvez ajouter le prix si nécessaire ici
                Text(text = "${item.price} DH", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}


@Composable
private fun AppFooter() { // Pas de changement ici
    Box(modifier = Modifier.fillMaxWidth().height(56.dp).background(Color.Red)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp).align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FooterImageItem(R.drawable.home, "Accueil") { /* TODO */ }
            FooterImageItem(R.drawable.favorite, "Favoris") { /* TODO */ }
            FooterImageItem(R.drawable.support_agent, "support") { /* TODO */ }
            FooterImageItem(R.drawable.account_circle, "account") { /* TODO */ }
        }
    }
}

@Composable
private fun FooterImageItem(drawableResId: Int, contentDescription: String, onClick: () -> Unit) { // Pas de changement ici
    IconButton(onClick = onClick, modifier = Modifier.size(40.dp)) {
        Icon(
            painter = painterResource(id = drawableResId),
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}