package com.samuelbaldasso.ifoodclone.ui.restaurant

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.samuelbaldasso.ifoodclone.core.designsystem.component.DeliveryInfoRow
import com.samuelbaldasso.ifoodclone.core.designsystem.component.RatingBadge

@Composable
fun RestaurantDetailScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RestaurantDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is RestaurantDetailEffect.NavigateBack -> onBackClick()
                is RestaurantDetailEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
                is RestaurantDetailEffect.AddedToCart -> {
                    // Feedback snackbar handled in ViewModel
                }
            }
        }
    }

    RestaurantDetailContent(
        uiState = uiState,
        onIntent = viewModel::handleIntent,
        onBackClick = onBackClick,
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )

    // Dish Customization BottomSheet
    uiState.customizationState?.let { customizationState ->
        DishCustomizationBottomSheet(
            state = customizationState,
            onToggleOption = { groupId, optionId ->
                viewModel.handleIntent(RestaurantDetailIntent.ToggleOption(groupId, optionId))
            },
            onChangeQuantity = { newQty ->
                viewModel.handleIntent(RestaurantDetailIntent.ChangeQuantity(newQty))
            },
            onConfirmAddToCart = {
                viewModel.handleIntent(RestaurantDetailIntent.ConfirmAddToCart)
            },
            onDismiss = {
                viewModel.handleIntent(RestaurantDetailIntent.CloseDishCustomization)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDetailContent(
    uiState: RestaurantDetailUiState,
    onIntent: (RestaurantDetailIntent) -> Unit,
    onBackClick: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    modifier: Modifier = Modifier
) {
    var isFavorite by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.restaurantDetails?.restaurant?.name ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { isFavorite = !isFavorite }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favoritar",
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Compartilhar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            uiState.errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = uiState.errorMessage,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(onClick = { onIntent(RestaurantDetailIntent.Retry) }) {
                            Text("Tentar novamente")
                        }
                    }
                }
            }
            uiState.restaurantDetails != null -> {
                val details = uiState.restaurantDetails
                val restaurant = details.restaurant

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    // Header Banner & Restaurant Info
                    item {
                        Column {
                            // Cover Image with Logo
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                            ) {
                                AsyncImage(
                                    model = restaurant.imageUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(110.dp)
                                )

                                AsyncImage(
                                    model = restaurant.imageUrl,
                                    contentDescription = restaurant.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .align(Alignment.BottomStart)
                                        .offset(x = 16.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                                        .background(MaterialTheme.colorScheme.surface)
                                )
                            }

                            // Restaurant Details Info
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = restaurant.name,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RatingBadge(rating = restaurant.rating)

                                    if (restaurant.ratingCount > 0) {
                                        Text(
                                            text = "(${restaurant.ratingCount} avaliações)",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Text(
                                        text = "•",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Text(
                                        text = restaurant.category,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                DeliveryInfoRow(
                                    deliveryTimeRange = restaurant.deliveryTimeRange,
                                    deliveryFee = restaurant.deliveryFee,
                                    distanceKm = restaurant.distanceKm
                                )

                                if (!restaurant.minOrderValue.isZero) {
                                    Text(
                                        text = "Pedido mínimo: ${restaurant.minOrderValue.formatBrl()}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Text(
                                    text = details.address,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
                        }
                    }

                    // Section Tabs
                    if (details.menuSections.isNotEmpty()) {
                        item {
                            ScrollableTabRow(
                                selectedTabIndex = uiState.selectedSectionIndex,
                                edgePadding = 16.dp,
                                containerColor = MaterialTheme.colorScheme.surface,
                                divider = {}
                            ) {
                                details.menuSections.forEachIndexed { index, section ->
                                    Tab(
                                        selected = uiState.selectedSectionIndex == index,
                                        onClick = {
                                            onIntent(RestaurantDetailIntent.SelectSection(index))
                                        },
                                        text = {
                                            Text(
                                                text = section.name,
                                                fontWeight = if (uiState.selectedSectionIndex == index) FontWeight.Bold else FontWeight.Normal
                                            )
                                        }
                                    )
                                }
                            }
                            HorizontalDivider()
                        }
                    }

                    // Menu Section Items
                    details.menuSections.forEachIndexed { sectionIndex, section ->
                        // Show all or highlighted section
                        item(key = "header_${section.id}") {
                            Text(
                                text = section.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp)
                            )
                        }

                        items(
                            items = section.dishes,
                            key = { it.id }
                        ) { dish ->
                            DishItemCard(
                                dish = dish,
                                onClick = {
                                    onIntent(RestaurantDetailIntent.OpenDishCustomization(dish))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
