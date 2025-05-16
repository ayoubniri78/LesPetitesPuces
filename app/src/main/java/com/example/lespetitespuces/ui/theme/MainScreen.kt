package com.example.lespetitespuces.ui.screens

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
import androidx.compose.material.icons.filled.ShoppingCart // Icône panier
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.lespetitespuces.Model.CategoryModel
import com.example.lespetitespuces.R
import com.example.lespetitespuces.model.ItemsModel
import com.example.lespetitespuces.navigation.AppRoutes // Assurez-vous que cet import est correct
import com.example.lespetitespuces.ui.theme.CustomRed
import com.example.lespetitespuces.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseUser


val Italianno = FontFamily(
    Font(R.font.italianno_regular) // Assurez-vous que cette ressource existe
)

// Définition de AppHeader() si elle a été supprimée
@Composable
private fun AppHeader(
    navController: NavController, // Pour la navigation
    currentUser: FirebaseUser?    // Pour afficher les initiales
) {
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

        val initials = currentUser?.let { user ->
            val displayName = user.displayName
            val email = user.email
            if (!displayName.isNullOrBlank()) {
                val names = displayName.split(" ")
                val firstInitial = names.firstOrNull()?.firstOrNull()?.uppercaseChar() ?: ' '
                val lastInitial = if (names.size > 1) names.lastOrNull()?.firstOrNull()?.uppercaseChar() ?: ' ' else ' '
                if (lastInitial != ' ') "$firstInitial$lastInitial" else "$firstInitial"
            } else if (!email.isNullOrBlank()) {
                email.substring(0, minOf(2, email.length)).uppercase()
            } else {
                "U" // Utilisateur
            }
        } ?: "AN" // "AN" si non connecté (ou autre placeholder)

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape) // Pour que le clic fonctionne bien sur le cercle
                .background(if (currentUser != null) CustomRed else Color.Gray, CircleShape) // Couleur différente si connecté
                .clickable(enabled = currentUser != null) { // Cliquable seulement si connecté
                    navController.navigate(AppRoutes.PROFILE_SCREEN)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = if (initials.length == 1) 18.sp else 14.sp // Ajuster la taille de la police
            )
        }
    }
}


@Composable
fun MainScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    categories: List<CategoryModel>,
    allItems: List<ItemsModel>,
) {
    val currentUser by authViewModel.currentUser.observeAsState()
    var selectedCategoryId by rememberSaveable { mutableStateOf(-1) }
    var searchText by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = { AppHeader(
            navController = navController, // Passer NavController
            currentUser = currentUser       // Passer l'utilisateur actuel
        ) }, // AppHeader est maintenant défini ci-dessus
        bottomBar = {
            AppFooter(
                navController = navController,
                isUserLoggedIn = currentUser != null,
                onLogoutClick = { authViewModel.logoutUser() }
            )
        },
        content = { paddingValues ->
            HomeContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                navController = navController,
                categories = categories,
                allItems = allItems,
                selectedCategoryId = selectedCategoryId,
                onCategorySelected = { categoryId -> selectedCategoryId = categoryId },
                searchText = searchText,
                onSearchTextChanged = { newText -> searchText = newText }
            )
        }
    )
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    categories: List<CategoryModel>,
    allItems: List<ItemsModel>,
    selectedCategoryId: Int,
    onCategorySelected: (Int) -> Unit,
    searchText: String,
    onSearchTextChanged: (String) -> Unit
) {
    val displayedItems = remember(allItems, selectedCategoryId, searchText) {
        val categoryFilteredItems = if (selectedCategoryId == -1) {
            allItems
        } else {
            allItems.filter { item -> item.categoryId.toIntOrNull() == selectedCategoryId }
        }
        if (searchText.isBlank()) {
            categoryFilteredItems
        } else {
            categoryFilteredItems.filter { item ->
                item.title.contains(searchText, ignoreCase = true) ||
                        item.description.contains(searchText, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        SearchBar(
            searchText = searchText,
            onSearchTextChanged = onSearchTextChanged
        )
        Spacer(modifier = Modifier.height(16.dp))
        CategoryTabs(
            categories = categories,
            selectedCategoryId = selectedCategoryId,
            onCategorySelected = onCategorySelected
        )
        Spacer(modifier = Modifier.height(8.dp))
        Divider(color = Color.Black, thickness = 1.dp, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))

        if (displayedItems.isEmpty()) {
            val message = if (allItems.isEmpty() && categories.isNotEmpty()) {
                "Chargement des produits..."
            } else if (searchText.isNotBlank()){
                "Aucun produit ne correspond à votre recherche '${searchText}'."
            } else {
                "Aucun produit trouvé pour cette catégorie."
            }
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp)
            )
        } else {
            ProductList(
                navController = navController,
                items = displayedItems
            )
        }
    }
}


@Composable
fun SearchBar(
    searchText: String,
    onSearchTextChanged: (String) -> Unit
) {
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
            TextField( // Utiliser TextField de Material 3
                value = searchText,
                onValueChange = onSearchTextChanged,
                placeholder = { Text("chercher") },
                singleLine = true,
                colors = TextFieldDefaults.colors( // Pour Material 3
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary // Optionnel: couleur du curseur
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun CategoryTabs(
    categories: List<CategoryModel>,
    selectedCategoryId: Int,
    onCategorySelected: (Int) -> Unit
) {
    val defaultCategory = CategoryModel(title = "Tous", id = -1)
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
                val isSelected = category.id == selectedCategoryId
                Button( // Utiliser Button de Material 3
                    onClick = { onCategorySelected(category.id) },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors( // Pour Material 3
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
fun ProductList(
    navController: NavController,
    items: List<ItemsModel>
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(), // Prends tout l'espace disponible dans son parent (HomeContent Column)
        contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp), // Ajouté padding en haut et en bas
        horizontalArrangement = Arrangement.spacedBy(12.dp), // Espace ajusté
        verticalArrangement = Arrangement.spacedBy(12.dp)     // Espace ajusté
    ) {
        items(items, key = { item -> item.title + item.categoryId }) { item ->
            ProductItemCard(
                navController = navController,
                item = item
            )
        }
    }
}

@Composable
fun ProductItemCard(
    navController: NavController,
    item: ItemsModel
) {
    Card( // Utiliser Card de Material 3
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("${AppRoutes.PRODUCT_DETAIL_SCREEN}/${item.title}")
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White) // Pour Material 3
    ) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.picUrl.firstOrNull())
                    .crossfade(true)
//                    .placeholder(R.drawable.placeholder_image) // Décommentez si vous avez l'image
//                    .error(R.drawable.placeholder_image)       // Décommentez si vous avez l'image
                    .build(),
                contentDescription = item.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop
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
                    text = item.description,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Filled.Star, contentDescription = "Rating", tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = item.rating.toString(), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                    Icon(
                        imageVector = Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp).clickable { /* TODO: Logique favoris */ }
                    )
                }
            }
        }
    }
}


@Composable
private fun AppFooter(
    navController: NavController,
    isUserLoggedIn: Boolean,
    onLogoutClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth().height(56.dp).background(Color.Red)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp).align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FooterImageItem(R.drawable.home, "Accueil") { /* TODO */ }
            IconButton(onClick = { navController.navigate(AppRoutes.CART_SCREEN) }, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Filled.ShoppingCart, contentDescription = "Panier", tint = Color.White, modifier = Modifier.size(24.dp))
            }
            FooterImageItem(R.drawable.favorite, "Favoris") { /* TODO */ }
            if (isUserLoggedIn) {
                FooterImageItem(R.drawable.logout, "Déconnexion") { onLogoutClick() } // Assurez-vous d'avoir R.drawable.logout
            } else {
                FooterImageItem(R.drawable.account_circle, "Compte") { navController.navigate(AppRoutes.LOGIN_SCREEN) }
            }
        }
    }
}

@Composable
private fun FooterImageItem(drawableResId: Int, contentDescription: String, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.size(40.dp)) { // Utiliser IconButton de Material 3
        Icon(
            painter = painterResource(id = drawableResId),
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}