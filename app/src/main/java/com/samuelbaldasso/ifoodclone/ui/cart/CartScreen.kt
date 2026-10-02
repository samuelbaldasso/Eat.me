package com.samuelbaldasso.ifoodclone.ui.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.samuelbaldasso.ifoodclone.core.designsystem.theme.EatMePurplePrimary
import com.samuelbaldasso.ifoodclone.core.designsystem.theme.EatMeTertiary
import com.samuelbaldasso.ifoodclone.core.domain.model.CartItem

@Composable
fun CartScreen(
    onBackClick: () -> Unit,
    onAddMoreItems: (String) -> Unit,
    onProceedToCheckout: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is CartUiEffect.NavigateBack -> onBackClick()
                is CartUiEffect.NavigateToCheckout -> onProceedToCheckout(viewModel.uiState.value.discount.cents)
                is CartUiEffect.NavigateToRestaurant -> onAddMoreItems(effect.restaurantId)
                is CartUiEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    CartScreenContent(
        uiState = uiState,
        onIntent = viewModel::handleIntent,
        onBackClick = onBackClick,
        onAddMoreItems = {
            uiState.cart.restaurant?.id?.let { onAddMoreItems(it) } ?: onBackClick()
        },
        onProceedToCheckout = { onProceedToCheckout(uiState.discount.cents) },
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreenContent(
    uiState: CartUiState,
    onIntent: (CartUiIntent) -> Unit,
    onBackClick: () -> Unit,
    onAddMoreItems: () -> Unit,
    onProceedToCheckout: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    modifier: Modifier = Modifier
) {
    val cart = uiState.cart

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Sacola",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
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
                    if (cart.isNotEmpty) {
                        TextButton(onClick = { onIntent(CartUiIntent.ClearCart) }) {
                            Text(
                                text = "Limpar",
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.SemiBold
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
            if (cart.isNotEmpty) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding(),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (!cart.isMinOrderSatisfied) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.errorContainer)
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Falta ${cart.amountMissingForMinOrder.formatBrl()} para o pedido mínimo (${cart.minOrderValue.formatBrl()})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Button(
                            onClick = onProceedToCheckout,
                            enabled = cart.isMinOrderSatisfied,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EatMePurplePrimary
                            )
                        ) {
                            Text(
                                text = "Continuar • ${uiState.finalTotal.formatBrl()}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (cart.isEmpty) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(EatMePurplePrimary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = EatMePurplePrimary,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Text(
                        text = "Sua sacola está vazia",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Adicione itens de um restaurante para fazer seu pedido.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Button(
                        onClick = onBackClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EatMePurplePrimary)
                    ) {
                        Text("Ver restaurantes")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // Restaurant info header
                item {
                    val restaurant = cart.restaurant
                    if (restaurant != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = restaurant.imageUrl,
                                    contentDescription = restaurant.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = restaurant.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${restaurant.deliveryTimeRange} • ${if (restaurant.deliveryFee.isZero) "Entrega Grátis" else restaurant.deliveryFee.formatBrl()}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (restaurant.deliveryFee.isZero) EatMeTertiary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = if (restaurant.deliveryFee.isZero) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }

                // Section Title: Itens
                item {
                    Text(
                        text = "Itens adicionados",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // Items list
                items(
                    items = cart.items,
                    key = { it.id }
                ) { item ->
                    CartItemRow(
                        item = item,
                        onIncrease = { onIntent(CartUiIntent.UpdateQuantity(item.id, item.quantity + 1)) },
                        onDecrease = {
                            if (item.quantity == 1) {
                                onIntent(CartUiIntent.RemoveItem(item.id))
                            } else {
                                onIntent(CartUiIntent.UpdateQuantity(item.id, item.quantity - 1))
                            }
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }

                // Add more items button
                item {
                    TextButton(
                        onClick = onAddMoreItems,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "+ Adicionar mais itens",
                            color = EatMePurplePrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                }

                // Coupon Section
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Cupom de desconto",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (uiState.isCouponApplied) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = EatMeTertiary.copy(alpha = 0.12f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "✓ Cupom ${uiState.couponCode} aplicado!",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = EatMeTertiary
                                        )
                                        Text(
                                            text = "Desconto de ${uiState.discount.formatBrl()}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = EatMeTertiary
                                        )
                                    }
                                    IconButton(
                                        onClick = { onIntent(CartUiIntent.RemoveCoupon) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Remover cupom",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = uiState.couponCode,
                                    onValueChange = { onIntent(CartUiIntent.SetCouponCode(it)) },
                                    placeholder = { Text("Ex: PURPLE10") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    shape = RoundedCornerShape(8.dp)
                                )

                                Button(
                                    onClick = { onIntent(CartUiIntent.ApplyCoupon) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EatMePurplePrimary)
                                ) {
                                    Text("Aplicar")
                                }
                            }

                            if (uiState.couponError != null) {
                                Text(
                                    text = uiState.couponError ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                }

                // Summary Values
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Resumo de valores",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Subtotal", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = cart.subtotal.formatBrl(), style = MaterialTheme.typography.bodyMedium)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Taxa de entrega", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = if (cart.deliveryFee.isZero) "Grátis" else cart.deliveryFee.formatBrl(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (cart.deliveryFee.isZero) EatMeTertiary else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (cart.deliveryFee.isZero) FontWeight.Bold else FontWeight.Normal
                            )
                        }

                        if (uiState.isCouponApplied && !uiState.discount.isZero) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Descontos", style = MaterialTheme.typography.bodyMedium, color = EatMeTertiary)
                                Text(text = "- ${uiState.discount.formatBrl()}", style = MaterialTheme.typography.bodyMedium, color = EatMeTertiary, fontWeight = FontWeight.Bold)
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Total", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(text = uiState.finalTotal.formatBrl(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = EatMePurplePrimary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CartItemRow(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.dishName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            if (item.selectedOptions.isNotEmpty()) {
                Text(
                    text = item.selectedOptions.joinToString(separator = " • ") { opt ->
                        if (opt.extraPrice.isZero) opt.name else "${opt.name} (+${opt.extraPrice.formatBrl()})"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.totalPrice.formatBrl(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = EatMePurplePrimary
            )
        }

        // Stepper
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 4.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onDecrease,
                modifier = Modifier.size(32.dp)
            ) {
                if (item.quantity == 1) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remover item",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Text(
                        text = "−",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = EatMePurplePrimary
                    )
                }
            }

            Text(
                text = "${item.quantity}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 10.dp)
            )

            IconButton(
                onClick = onIncrease,
                enabled = item.quantity < 20,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Aumentar quantidade",
                    tint = if (item.quantity < 20) EatMePurplePrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
