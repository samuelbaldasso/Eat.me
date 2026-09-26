package com.samuelbaldasso.ifoodclone.core.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Primary brand colors (Eat.me distinctive Purple palette - iFood conceptual clone with Purple branding)
val EatMePurplePrimary = Color(0xFF7C3AED)
val EatMePurpleOnPrimary = Color(0xFFFFFFFF)
val EatMePurplePrimaryContainer = Color(0xFFF3E8FF)
val EatMePurpleOnPrimaryContainer = Color(0xFF3B0764)
val EatMePurpleDark = Color(0xFF5B21B6)
val EatMePurpleLight = Color(0xFF8B5CF6)
val EatMePurpleSoft = Color(0xFFEDE9FE)

// Backward compatibility alias
val EatMeRedPrimary = EatMePurplePrimary

// Secondary & Tertiary
val EatMeSecondary = Color(0xFF6B7280)
val EatMeOnSecondary = Color(0xFFFFFFFF)
val EatMeSecondaryContainer = Color(0xFFF3F4F6)
val EatMeOnSecondaryContainer = Color(0xFF1F2937)

val EatMeTertiary = Color(0xFF10B981) // Success / Free delivery green
val EatMeOnTertiary = Color(0xFFFFFFFF)
val EatMeTertiaryContainer = Color(0xFFD1FAE5)
val EatMeOnTertiaryContainer = Color(0xFF065F46)

// Semantic status tokens
val EatMeWarning = Color(0xFFF59E0B)
val EatMeRatingStar = Color(0xFFFBBF24)
val EatMeError = Color(0xFFEF4444)
val EatMeOnError = Color(0xFFFFFFFF)
val EatMeErrorContainer = Color(0xFFFEE2E2)
val EatMeOnErrorContainer = Color(0xFF7F1D1D)

// Neutral surfaces - Light
val EatMeBackgroundLight = Color(0xFFF9FAFB)
val EatMeOnBackgroundLight = Color(0xFF111827)
val EatMeSurfaceLight = Color(0xFFFFFFFF)
val EatMeOnSurfaceLight = Color(0xFF1F2937)
val EatMeSurfaceVariantLight = Color(0xFFF3F4F6)
val EatMeOnSurfaceVariantLight = Color(0xFF6B7280)
val EatMeOutlineLight = Color(0xFFE5E7EB)

// Neutral surfaces - Dark
val EatMeBackgroundDark = Color(0xFF0F172A)
val EatMeOnBackgroundDark = Color(0xFFF8FAFC)
val EatMeSurfaceDark = Color(0xFF1E293B)
val EatMeOnSurfaceDark = Color(0xFFF1F5F9)
val EatMeSurfaceVariantDark = Color(0xFF334155)
val EatMeOnSurfaceVariantDark = Color(0xFF94A3B8)
val EatMeOutlineDark = Color(0xFF475569)

val EatMeLightColorScheme = lightColorScheme(
    primary = EatMePurplePrimary,
    onPrimary = EatMePurpleOnPrimary,
    primaryContainer = EatMePurplePrimaryContainer,
    onPrimaryContainer = EatMePurpleOnPrimaryContainer,
    secondary = EatMeSecondary,
    onSecondary = EatMeOnSecondary,
    secondaryContainer = EatMeSecondaryContainer,
    onSecondaryContainer = EatMeOnSecondaryContainer,
    tertiary = EatMeTertiary,
    onTertiary = EatMeOnTertiary,
    tertiaryContainer = EatMeTertiaryContainer,
    onTertiaryContainer = EatMeOnTertiaryContainer,
    error = EatMeError,
    onError = EatMeOnError,
    errorContainer = EatMeErrorContainer,
    onErrorContainer = EatMeOnErrorContainer,
    background = EatMeBackgroundLight,
    onBackground = EatMeOnBackgroundLight,
    surface = EatMeSurfaceLight,
    onSurface = EatMeOnSurfaceLight,
    surfaceVariant = EatMeSurfaceVariantLight,
    onSurfaceVariant = EatMeOnSurfaceVariantLight,
    outline = EatMeOutlineLight
)

val EatMeDarkColorScheme = darkColorScheme(
    primary = EatMePurpleLight,
    onPrimary = Color(0xFF2E1065),
    primaryContainer = EatMePurpleDark,
    onPrimaryContainer = EatMePurplePrimaryContainer,
    secondary = Color(0xFF9CA3AF),
    onSecondary = Color(0xFF111827),
    secondaryContainer = Color(0xFF374151),
    onSecondaryContainer = Color(0xFFF3F4F6),
    tertiary = Color(0xFF34D399),
    onTertiary = Color(0xFF064E3B),
    tertiaryContainer = Color(0xFF065F46),
    onTertiaryContainer = Color(0xFFA7F3D0),
    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFEE2E2),
    background = EatMeBackgroundDark,
    onBackground = EatMeOnBackgroundDark,
    surface = EatMeSurfaceDark,
    onSurface = EatMeOnSurfaceDark,
    surfaceVariant = EatMeSurfaceVariantDark,
    onSurfaceVariant = EatMeOnSurfaceVariantDark,
    outline = EatMeOutlineDark
)
