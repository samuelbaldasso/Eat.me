package com.samuelbaldasso.ifoodclone.ui.theme.composables.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.samuelbaldasso.ifoodclone.core.designsystem.component.HomeScreenSkeleton
import com.samuelbaldasso.ifoodclone.core.designsystem.theme.EatMePurplePrimary
import com.samuelbaldasso.ifoodclone.core.designsystem.theme.EatMePurpleLight
import com.samuelbaldasso.ifoodclone.core.designsystem.theme.EatMeTheme
import com.samuelbaldasso.ifoodclone.core.domain.model.Money
import com.samuelbaldasso.ifoodclone.core.domain.model.Restaurant
import com.samuelbaldasso.ifoodclone.ui.cart.FloatingCartBar
import com.samuelbaldasso.ifoodclone.ui.theme.composables.restaurant.RestaurantItem

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onRestaurantClick: (String) -> Unit = {},
    onViewCartClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is HomeUiEffect.NavigateToRestaurant -> onRestaurantClick(effect.restaurantId)
                is HomeUiEffect.NavigateToCart -> onViewCartClick()
                is HomeUiEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    HomeScreenContent(
        uiState = uiState,
        onIntent = viewModel::handleIntent,
        onViewCartClick = onViewCartClick,
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    onIntent: (HomeUiIntent) -> Unit,
    onViewCartClick: () -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Entregar em",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = uiState.address,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "▼",
                                style = MaterialTheme.typography.labelSmall,
                                color = EatMePurplePrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            if (uiState.cart.isNotEmpty) {
                FloatingCartBar(
                    cart = uiState.cart,
                    onClick = onViewCartClick
                )
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            HomeScreenSkeleton(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // iFood-style Search Bar Pill
                item {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = { onIntent(HomeUiIntent.SearchQueryChange(it)) },
                        placeholder = {
                            Text(
                                text = "Buscar em Eat.io",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Buscar",
                                tint = EatMePurplePrimary
                            )
                        },
                        trailingIcon = {
                            if (uiState.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onIntent(HomeUiIntent.SearchQueryChange("")) }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Limpar busca"
                                    )
                                }
                            }
                        },
                        shape = RoundedCornerShape(28.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            focusedBorderColor = EatMePurplePrimary,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            focusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        singleLine = true
                    )
                }

                item {
                    CategoryCarousel(
                        categories = uiState.categories,
                        selectedCategory = uiState.selectedCategory,
                        onCategorySelected = { onIntent(HomeUiIntent.SelectCategory(it)) }
                    )
                }

                item {
                    PromotionalBanner(
                        title = "Entrega Grátis acima de R$ 30",
                        subtitle = "Aproveite cupons de até R$ 15 nas melhores lojas",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Lojas disponíveis",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${uiState.filteredRestaurants.size} opções",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (uiState.filteredRestaurants.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Nenhum restaurante encontrado para \"${uiState.selectedCategory}\".",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(
                        items = uiState.filteredRestaurants,
                        key = { it.id }
                    ) { restaurant ->
                        RestaurantItem(
                            restaurant = restaurant,
                            onClick = { onIntent(HomeUiIntent.RestaurantClick(restaurant.id)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryCarousel(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { category ->
            val isSelected = category.equals(selectedCategory, ignoreCase = true)
            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(category) },
                label = {
                    Text(
                        text = category,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = EatMePurplePrimary,
                    selectedLabelColor = Color.White,
                    containerColor = MaterialTheme.colorScheme.surface,
                    labelColor = MaterialTheme.colorScheme.onSurface
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                    selectedBorderColor = EatMePurplePrimary
                )
            )
        }
    }
}

@Composable
fun PromotionalBanner(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            EatMePurplePrimary,
                            Color(0xFF9333EA),
                            EatMePurpleLight
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.95f)
                )
            }
        }
    }
}

@Preview(name = "Home Screen Content", showBackground = true)
@Composable
private fun HomeScreenContentPreview() {
    EatMeTheme {
        HomeScreenContent(
            uiState = HomeUiState(
                isLoading = false,
                restaurants = listOf(
                    Restaurant(
                        id = "1",
                        name = "Burger King",
                        category = "Lanches",
                        deliveryFee = Money.ZERO,
                        deliveryTimeRange = "30-40 min",
                        rating = 4.5,
                        imageUrl = "https://example.com/logo.png"
                    )
                ),
                filteredRestaurants = listOf(
                    Restaurant(
                        id = "1",
                        name = "Burger King",
                        category = "Lanches",
                        deliveryFee = Money.ZERO,
                        deliveryTimeRange = "30-40 min",
                        rating = 4.5,
                        imageUrl = "https://example.com/logo.png"
                    )
                )
            ),
            onIntent = {}
        )
    }
}
