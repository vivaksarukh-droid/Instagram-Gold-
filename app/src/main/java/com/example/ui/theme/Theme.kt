package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.data.model.GoldPalette

@Immutable
data class GoldTokens(
    val accent: Color,
    val highlight: Color,
    val deep: Color,
    val container: Color,
    val surfaceElevated: Color,
    val surfaceCard: Color,
    val hairline: Color,
    val mutedText: Color,
    val heartColor: Color,
    val goldGradient: Brush,
    val storyRingGradient: Brush
)

val LocalGoldTokens = staticCompositionLocalOf {
    GoldTokens(
        accent = Gold24KPrimary,
        highlight = Gold24KHighlight,
        deep = Gold24KDeep,
        container = Gold24KContainer,
        surfaceElevated = ObsidianElevated,
        surfaceCard = ObsidianCard,
        hairline = SubtleHairline,
        mutedText = WarmTaupe,
        heartColor = CrimsonRose,
        goldGradient = Brush.linearGradient(listOf(Gold24KHighlight, Gold24KPrimary, Gold24KDeep)),
        storyRingGradient = Brush.sweepGradient(listOf(Gold24KDeep, Gold24KHighlight, Gold24KPrimary, Gold24KDeep))
    )
}

private val InstaGoldShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun InstaGoldTheme(
    goldPalette: GoldPalette = GoldPalette.GOLD_24K,
    content: @Composable () -> Unit
) {
    val (accent, highlight, deep, container) = when (goldPalette) {
        GoldPalette.GOLD_24K -> listOf(Gold24KPrimary, Gold24KHighlight, Gold24KDeep, Gold24KContainer)
        GoldPalette.ROSE_CHAMPAGNE -> listOf(
            RoseChampagnePrimary,
            RoseChampagneHighlight,
            RoseChampagneDeep,
            RoseChampagneContainer
        )
        GoldPalette.PLATINUM_ONYX -> listOf(
            PlatinumPrimary,
            PlatinumHighlight,
            PlatinumDeep,
            PlatinumContainer
        )
    }

    val colorScheme = darkColorScheme(
        primary = accent,
        onPrimary = ObsidianBlack,
        primaryContainer = container,
        onPrimaryContainer = highlight,
        secondary = highlight,
        onSecondary = ObsidianBlack,
        secondaryContainer = ObsidianElevated,
        onSecondaryContainer = ChampagneIvory,
        tertiary = deep,
        onTertiary = ChampagneIvory,
        background = ObsidianBlack,
        onBackground = ChampagneIvory,
        surface = ObsidianSurface,
        onSurface = ChampagneIvory,
        surfaceVariant = ObsidianElevated,
        onSurfaceVariant = WarmTaupe,
        outline = SubtleHairline,
        outlineVariant = SubtleHairline,
        error = CrimsonRose,
        onError = ObsidianBlack
    )

    val tokens = GoldTokens(
        accent = accent,
        highlight = highlight,
        deep = deep,
        container = container,
        surfaceElevated = ObsidianElevated,
        surfaceCard = ObsidianCard,
        hairline = SubtleHairline,
        mutedText = WarmTaupe,
        heartColor = CrimsonRose,
        goldGradient = Brush.linearGradient(listOf(highlight, accent, deep)),
        storyRingGradient = Brush.sweepGradient(listOf(deep, highlight, accent, deep))
    )

    CompositionLocalProvider(LocalGoldTokens provides tokens) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = InstaGoldTypography,
            shapes = InstaGoldShapes,
            content = content
        )
    }
}
