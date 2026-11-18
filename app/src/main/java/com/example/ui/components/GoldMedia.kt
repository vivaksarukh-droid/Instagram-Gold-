package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.model.GoldFilter
import com.example.ui.theme.LocalGoldTokens
import java.io.File

fun resolveDrawableResource(mediaKey: String): Int {
    return when (mediaKey.removePrefix("drawable:")) {
        "img_gold_architecture" -> R.drawable.img_gold_architecture
        "img_gold_timepiece" -> R.drawable.img_gold_timepiece
        "img_gold_dunes" -> R.drawable.img_gold_dunes
        "img_gold_espresso" -> R.drawable.img_gold_espresso
        else -> R.drawable.img_gold_architecture
    }
}

@Composable
fun GoldMediaImage(
    mediaKey: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    filter: GoldFilter = GoldFilter.NATURAL,
    contentScale: ContentScale = ContentScale.Crop
) {
    val colorFilter: ColorFilter? = remember(filter) { filter.toColorFilter() }
    if (mediaKey.startsWith("file:")) {
        val file = remember(mediaKey) { File(mediaKey.removePrefix("file:")) }
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(file)
                .crossfade(true)
                .build(),
            contentDescription = contentDescription,
            contentScale = contentScale,
            colorFilter = colorFilter,
            modifier = modifier
        )
    } else {
        Image(
            painter = painterResource(id = resolveDrawableResource(mediaKey)),
            contentDescription = contentDescription,
            contentScale = contentScale,
            colorFilter = colorFilter,
            modifier = modifier
        )
    }
}

@Composable
fun GoldAvatar(
    mediaKey: String,
    contentDescription: String,
    size: Dp = 44.dp,
    hasUnseenStory: Boolean = true,
    ringWidth: Dp = 2.dp,
    modifier: Modifier = Modifier
) {
    val tokens = LocalGoldTokens.current
    val ringBrush = if (hasUnseenStory) {
        tokens.storyRingGradient
    } else {
        SolidColor(tokens.hairline)
    }

    Box(
        modifier = modifier
            .size(size)
            .border(width = ringWidth, brush = ringBrush, shape = CircleShape)
            .padding(ringWidth + 2.dp)
            .clip(CircleShape)
            .background(tokens.surfaceElevated),
        contentAlignment = Alignment.Center
    ) {
        GoldMediaImage(
            mediaKey = mediaKey,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun VerifiedGoldBadge(
    modifier: Modifier = Modifier,
    size: Dp = 15.dp
) {
    val tokens = LocalGoldTokens.current
    Icon(
        imageVector = Icons.Filled.Verified,
        contentDescription = "Verified 24K Creator",
        tint = tokens.accent,
        modifier = modifier.size(size)
    )
}

@Composable
fun GoldHeartBurstOverlay(
    triggerCount: Int,
    modifier: Modifier = Modifier
) {
    if (triggerCount == 0) return
    val tokens = LocalGoldTokens.current
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(triggerCount) {
        scale.snapTo(0.4f)
        alpha.snapTo(1f)
        scale.animateTo(
            targetValue = 1.25f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
        alpha.animateTo(0f)
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = null,
            tint = tokens.highlight,
            modifier = Modifier
                .size(84.dp)
                .scale(scale.value)
                .alpha(alpha.value)
        )
    }
}
