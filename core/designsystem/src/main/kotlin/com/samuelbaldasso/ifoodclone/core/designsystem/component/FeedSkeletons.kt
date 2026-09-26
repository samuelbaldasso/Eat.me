package com.samuelbaldasso.ifoodclone.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun CategoryChipsSkeleton(
    modifier: Modifier = Modifier,
    chipCount: Int = 6
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(chipCount) {
            ShimmerPlaceholder(
                modifier = Modifier
                    .width(84.dp)
                    .height(34.dp),
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}

@Composable
fun PromoBannerSkeleton(
    modifier: Modifier = Modifier
) {
    ShimmerPlaceholder(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun RestaurantCardSkeleton(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circular Logo Skeleton
            ShimmerPlaceholder(
                modifier = Modifier.size(56.dp),
                shape = CircleShape
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Name Skeleton
                ShimmerPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .height(16.dp),
                    shape = RoundedCornerShape(4.dp)
                )

                // Info row Skeleton (rating, category, time)
                ShimmerPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(12.dp),
                    shape = RoundedCornerShape(4.dp)
                )

                // Coupon badge Skeleton
                ShimmerPlaceholder(
                    modifier = Modifier
                        .width(120.dp)
                        .height(14.dp),
                    shape = RoundedCornerShape(4.dp)
                )
            }
        }
    }
}

@Composable
fun DishCardSkeleton(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ShimmerPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(16.dp),
                    shape = RoundedCornerShape(4.dp)
                )
                ShimmerPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(12.dp),
                    shape = RoundedCornerShape(4.dp)
                )
                ShimmerPlaceholder(
                    modifier = Modifier
                        .width(70.dp)
                        .height(14.dp),
                    shape = RoundedCornerShape(4.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            ShimmerPlaceholder(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                shape = RoundedCornerShape(8.dp)
            )
        }
    }
}

@Composable
fun HomeScreenSkeleton(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        // Search bar skeleton
        ShimmerPlaceholder(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(28.dp)
        )

        // Category chips skeleton
        CategoryChipsSkeleton()

        // Promo Banner skeleton
        PromoBannerSkeleton()

        Spacer(modifier = Modifier.height(8.dp))

        // Restaurant cards skeleton
        repeat(5) {
            RestaurantCardSkeleton()
        }
    }
}

@Composable
fun RestaurantDetailSkeleton(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        // Banner Skeleton
        ShimmerPlaceholder(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            shape = RoundedCornerShape(0.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Restaurant Title Skeleton
            ShimmerPlaceholder(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(24.dp),
                shape = RoundedCornerShape(4.dp)
            )

            // Rating & Category Skeleton
            ShimmerPlaceholder(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(14.dp),
                shape = RoundedCornerShape(4.dp)
            )

            // Address Skeleton
            ShimmerPlaceholder(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(14.dp),
                shape = RoundedCornerShape(4.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Dishes Skeleton
        repeat(4) {
            DishCardSkeleton()
        }
    }
}

