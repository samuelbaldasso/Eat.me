package com.samuelbaldasso.ifoodclone.core.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Primary brand colors (Eat.me distinctive red palette)
val EatMeRedPrimary = Color(0xFFE5293E)
val EatMeRedOnPrimary = Color(0xFFFFFFFF)
val EatMeRedPrimaryContainer = Color(0xFFFFDAD9)
val EatMeRedOnPrimaryContainer = Color(0xFF410007)
val EatMeRedDarkContainer = Color(0xFF8C0018)

// Secondary & Tertiary
val EatMeSecondary = Color(0xFF775655)
val EatMeOnSecondary = Color(0xFFFFFFFF)
val EatMeSecondaryContainer = Color(0xFFFFDAD8)
val EatMeOnSecondaryContainer = Color(0xFF2C1515)

val EatMeTertiary = Color(0xFF1B8738) // Success/Free delivery green
val EatMeOnTertiary = Color(0xFFFFFFFF)
val EatMeTertiaryContainer = Color(0xFFD4F5DA)
val EatMeOnTertiaryContainer = Color(0xFF002107)

// Semantic status tokens
val EatMeWarning = Color(0xFFF57C00)
val EatMeRatingStar = Color(0xFFF8B000)
val EatMeError = Color(0xFFBA1A1A)
val EatMeOnError = Color(0xFFFFFFFF)
val EatMeErrorContainer = Color(0xFFFFDAD6)
val EatMeOnErrorContainer = Color(0xFF410002)

// Neutral surfaces - Light
val EatMeBackgroundLight = Color(0xFFFCFDF6)
val EatMeOnBackgroundLight = Color(0xFF1A1C18)
val EatMeSurfaceLight = Color(0xFFFFFFFF)
val EatMeOnSurfaceLight = Color(0xFF1A1C18)
val EatMeSurfaceVariantLight = Color(0xFFF4F4F4)
val EatMeOnSurfaceVariantLight = Color(0xFF757575)
val EatMeOutlineLight = Color(0xFFE0E0E0)

// Neutral surfaces - Dark
val EatMeBackgroundDark = Color(0xFF121410)
val EatMeOnBackgroundDark = Color(0xFFE2E3DC)
val EatMeSurfaceDark = Color(0xFF1C1E1A)
val EatMeOnSurfaceDark = Color(0xFFE2E3DC)
val EatMeSurfaceVariantDark = Color(0xFF2B2E28)
val EatMeOnSurfaceVariantDark = Color(0xFFA0A39A)
val EatMeOutlineDark = Color(0xFF3F423C)

val EatMeLightColorScheme = lightColorScheme(
    primary = EatMeRedPrimary,
    onPrimary = EatMeRedOnPrimary,
    primaryContainer = EatMeRedPrimaryContainer,
    onPrimaryContainer = EatMeRedOnPrimaryContainer,
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
    primary = Color(0xFFFFB3B3),
    onPrimary = Color(0xFF680010),
    primaryContainer = EatMeRedDarkContainer,
    onPrimaryContainer = Color(0xFFFFDAD9),
    secondary = Color(0xFFE7BDBB),
    onSecondary = Color(0xFF442928),
    secondaryContainer = Color(0xFF5D3F3E),
    onSecondaryContainer = Color(0xFFFFDAD8),
    tertiary = Color(0xFF86D98C),
    onTertiary = Color(0xFF003910),
    tertiaryContainer = Color(0xFF00531A),
    onTertiaryContainer = Color(0xFFA5F7AB),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = EatMeBackgroundDark,
    onBackground = EatMeOnBackgroundDark,
    surface = EatMeSurfaceDark,
    onSurface = EatMeOnSurfaceDark,
    surfaceVariant = EatMeSurfaceVariantDark,
    onSurfaceVariant = EatMeOnSurfaceVariantDark,
    outline = EatMeOutlineDark
)
