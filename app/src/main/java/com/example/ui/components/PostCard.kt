package com.example.ui.components

import android.text.format.DateUtils
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.PostEntity
import com.example.data.model.GoldFilter
import com.example.ui.theme.LocalGoldTokens
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PostCard(
    post: PostEntity,
    commentCount: Int,
    showGoldFrame: Boolean,
    onToggleLike: (Boolean) -> Unit,
    onToggleBookmark: () -> Unit,
    onOpenComments: () -> Unit,
    onInspectMedia: () -> Unit,
    onSaveToGallery: () -> Unit,
    onCopyCaption: () -> Unit,
    onSharePost: () -> Unit,
    onDeletePost: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val tokens = LocalGoldTokens.current
    var menuExpanded by remember { mutableStateOf(false) }
    var doubleTapBurst by remember { mutableIntStateOf(0) }

    val likeColor by animateColorAsState(
        targetValue = if (post.isLiked) tokens.heartColor else MaterialTheme.colorScheme.onSurface,
        label = "like_color"
    )
    val likeScale by animateFloatAsState(
        targetValue = if (post.isLiked) 1.12f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "like_scale"
    )
    val bookmarkColor by animateColorAsState(
        targetValue = if (post.isBookmarked) tokens.accent else MaterialTheme.colorScheme.onSurface,
        label = "bookmark_color"
    )

    val cardShape = RoundedCornerShape(22.dp)
    val borderModifier = if (showGoldFrame) {
        Modifier.border(
            width = 1.dp,
            brush = if (post.isGoldExclusive) tokens.goldGradient else androidx.compose.ui.graphics.SolidColor(tokens.hairline),
            shape = cardShape
        )
    } else {
        Modifier
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .then(borderModifier)
            .testTag("post_card_${post.id}"),
        shape = cardShape,
        color = tokens.surfaceCard,
        tonalElevation = 2.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 14.dp, end = 6.dp, top = 12.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GoldAvatar(
                    mediaKey = post.authorAvatarKey,
                    contentDescription = "${post.authorName} avatar",
                    size = 40.dp,
                    hasUnseenStory = post.isGoldExclusive,
                    ringWidth = 1.75.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = post.authorHandle,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (post.authorVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            VerifiedGoldBadge()
                        }
                        if (post.isGoldExclusive) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(tokens.container)
                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "24K",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = tokens.highlight
                                )
                            }
                        }
                    }
                    Text(
                        text = post.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.mutedText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.testTag("post_more_button_${post.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "Post options",
                            tint = tokens.mutedText
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.background(tokens.surfaceElevated)
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.action_hd_preview)) },
                            leadingIcon = { Icon(Icons.Filled.ZoomIn, contentDescription = null, tint = tokens.accent) },
                            onClick = {
                                menuExpanded = false
                                onInspectMedia()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.action_save_to_gallery)) },
                            leadingIcon = { Icon(Icons.Filled.Download, contentDescription = null, tint = tokens.accent) },
                            onClick = {
                                menuExpanded = false
                                onSaveToGallery()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.action_copy_caption)) },
                            leadingIcon = { Icon(Icons.Filled.ContentCopy, contentDescription = null, tint = tokens.mutedText) },
                            onClick = {
                                menuExpanded = false
                                onCopyCaption()
                            }
                        )
                        if (onDeletePost != null) {
                            HorizontalDivider(color = tokens.hairline)
                            DropdownMenuItem(
                                text = { Text("Delete Post", color = tokens.heartColor) },
                                leadingIcon = {
                                    Icon(Icons.Filled.DeleteOutline, contentDescription = null, tint = tokens.heartColor)
                                },
                                onClick = {
                                    menuExpanded = false
                                    onDeletePost()
                                }
                            )
                        }
                    }
                }
            }

            // Media Viewport with Double-Tap Like + Tap to Inspect
            val filter = remember(post.filterName) { GoldFilter.fromName(post.filterName) }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .aspectRatio(post.aspectRatio.coerceIn(0.75f, 1.45f))
                    .clip(RoundedCornerShape(16.dp))
                    .background(tokens.surfaceElevated)
                    .pointerInput(post.id) {
                        detectTapGestures(
                            onDoubleTap = {
                                doubleTapBurst++
                                onToggleLike(true)
                            },
                            onTap = {
                                onInspectMedia()
                            }
                        )
                    }
            ) {
                GoldMediaImage(
                    mediaKey = post.mediaKey,
                    contentDescription = post.caption,
                    filter = filter,
                    modifier = Modifier.fillMaxSize()
                )

                // Top-right subtle filter/category pill
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.65f))
                        .padding(horizontal = 9.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = tokens.highlight,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = post.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                GoldHeartBurstOverlay(triggerCount = doubleTapBurst)
            }

            // Action Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onToggleLike(false) },
                        modifier = Modifier.testTag("like_button_${post.id}")
                    ) {
                        Icon(
                            imageVector = if (post.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = stringResource(R.string.action_like),
                            tint = likeColor,
                            modifier = Modifier.scale(likeScale)
                        )
                    }
                    IconButton(
                        onClick = onOpenComments,
                        modifier = Modifier.testTag("comment_button_${post.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = stringResource(R.string.action_comment),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(
                        onClick = onSharePost,
                        modifier = Modifier.testTag("share_button_${post.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = stringResource(R.string.action_share),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onSaveToGallery,
                        modifier = Modifier.testTag("hd_save_button_${post.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Download,
                            contentDescription = stringResource(R.string.action_save_to_gallery),
                            tint = tokens.mutedText
                        )
                    }
                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier.testTag("bookmark_button_${post.id}")
                    ) {
                        Icon(
                            imageVector = if (post.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = stringResource(R.string.action_save),
                            tint = bookmarkColor
                        )
                    }
                }
            }

            // Likes, Caption, Comments & Relative Time
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 14.dp)
            ) {
                val formattedLikes = remember(post.likesCount) {
                    NumberFormat.getNumberInstance(Locale.US).format(post.likesCount)
                }
                Text(
                    text = "$formattedLikes likes",
                    style = MaterialTheme.typography.titleSmall,
                    color = tokens.highlight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)) {
                            append(post.authorHandle)
                            append("  ")
                        }
                        withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.92f))) {
                            append(post.caption)
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium
                )

                if (commentCount > 0) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        onClick = onOpenComments,
                        color = androidx.compose.ui.graphics.Color.Transparent,
                        modifier = Modifier.testTag("view_comments_${post.id}")
                    ) {
                        Text(
                            text = "View all $commentCount comments",
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.mutedText,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                val relativeTime = remember(post.createdAtMillis) {
                    DateUtils.getRelativeTimeSpanString(
                        post.createdAtMillis,
                        System.currentTimeMillis(),
                        DateUtils.MINUTE_IN_MILLIS
                    ).toString()
                }
                Text(
                    text = "$relativeTime • ${filter.displayName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = tokens.mutedText.copy(alpha = 0.8f)
                )
            }
        }
    }
}
