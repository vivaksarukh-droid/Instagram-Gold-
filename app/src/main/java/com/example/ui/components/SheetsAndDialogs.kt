package com.example.ui.components

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.local.CommentEntity
import com.example.data.local.PostEntity
import com.example.data.local.StoryEntity
import com.example.data.local.UserProfileEntity
import com.example.data.model.GoldFilter
import com.example.data.model.GoldPalette
import com.example.data.preferences.GoldStudioSettings
import com.example.ui.theme.LocalGoldTokens
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsBottomSheet(
    post: PostEntity,
    comments: List<CommentEntity>,
    onAddComment: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val tokens = LocalGoldTokens.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var draft by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = tokens.surfaceElevated,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        ) {
            Text(
                text = "Comments (${comments.size})",
                style = MaterialTheme.typography.titleLarge,
                color = tokens.highlight
            )
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = tokens.hairline)

            if (comments.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 36.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No comments yet. Start the conversation.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = tokens.mutedText
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 320.dp)
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(comments, key = { it.id }) { comment ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(tokens.container)
                                    .border(1.dp, tokens.accent.copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = comment.authorHandle.take(1).uppercase(),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = tokens.highlight
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "@${comment.authorHandle}",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = DateUtils.getRelativeTimeSpanString(
                                            comment.createdAtMillis,
                                            System.currentTimeMillis(),
                                            DateUtils.MINUTE_IN_MILLIS
                                        ).toString(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = tokens.mutedText
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = comment.text,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = draft,
                    onValueChange = { draft = it },
                    placeholder = { Text("Add a golden note...") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("comment_input")
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (draft.isNotBlank()) {
                            onAddComment(draft)
                            draft = ""
                        }
                    },
                    enabled = draft.isNotBlank(),
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (draft.isNotBlank()) tokens.accent else tokens.surfaceCard)
                        .testTag("send_comment_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send comment",
                        tint = if (draft.isNotBlank()) MaterialTheme.colorScheme.onPrimary else tokens.mutedText
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoldStudioBottomSheet(
    settings: GoldStudioSettings,
    onSelectPalette: (GoldPalette) -> Unit,
    onToggleGhostMode: (Boolean) -> Unit,
    onToggleHdSave: (Boolean) -> Unit,
    onToggleGoldFrame: (Boolean) -> Unit,
    onResetStories: () -> Unit,
    onDismiss: () -> Unit
) {
    val tokens = LocalGoldTokens.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = tokens.surfaceElevated,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = tokens.accent
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "InstaGold Studio",
                    style = MaterialTheme.typography.headlineSmall,
                    color = tokens.highlight
                )
            }
            Text(
                text = "Customize your luxury palette, stealth story viewing, and HD export preferences.",
                style = MaterialTheme.typography.bodySmall,
                color = tokens.mutedText
            )

            Text(
                text = "Metallic Theme",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                GoldPalette.entries.forEach { palette ->
                    val selected = settings.palette == palette
                    Surface(
                        onClick = { onSelectPalette(palette) },
                        shape = RoundedCornerShape(14.dp),
                        color = if (selected) tokens.container else tokens.surfaceCard,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = if (selected) 1.5.dp else 1.dp,
                                color = if (selected) tokens.accent else tokens.hairline,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .testTag("palette_option_${palette.name}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = palette.label,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = if (selected) tokens.highlight else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = palette.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = tokens.mutedText
                                )
                            }
                            if (selected) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = tokens.accent
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = tokens.hairline)

            StudioToggleRow(
                title = "Ghost Story Mode",
                subtitle = "View stories stealthily without marking rings as seen",
                checked = settings.ghostModeEnabled,
                onCheckedChange = onToggleGhostMode,
                testTag = "toggle_ghost_mode"
            )
            StudioToggleRow(
                title = "Lossless HD Export",
                subtitle = "Save photos at 98% quality to Pictures/InstaGold",
                checked = settings.hdSaveEnabled,
                onCheckedChange = onToggleHdSave,
                testTag = "toggle_hd_save"
            )
            StudioToggleRow(
                title = "24K Card Hairlines",
                subtitle = "Render brushed metallic borders around exclusive posts",
                checked = settings.goldFrameOverlay,
                onCheckedChange = onToggleGoldFrame,
                testTag = "toggle_gold_frame"
            )

            OutlinedButton(
                onClick = onResetStories,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reset_stories_button")
            ) {
                Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Refresh Unseen Story Rings")
            }
        }
    }
}

@Composable
private fun StudioToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    val tokens = LocalGoldTokens.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = tokens.mutedText
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = tokens.accent
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}

@Composable
fun HdMediaInspectorDialog(
    post: PostEntity,
    onSaveToGallery: () -> Unit,
    onDismiss: () -> Unit
) {
    val tokens = LocalGoldTokens.current
    val filter = remember(post.filterName) { GoldFilter.fromName(post.filterName) }
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF060504))
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            val newScale = (scale * zoom).coerceIn(1f, 3.5f)
                            scale = newScale
                            offset = if (newScale > 1f) offset + pan else Offset.Zero
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                if (scale > 1f) {
                                    scale = 1f
                                    offset = Offset.Zero
                                } else {
                                    scale = 2.2f
                                }
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                GoldMediaImage(
                    mediaKey = post.mediaKey,
                    contentDescription = post.caption,
                    filter = filter,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            translationX = offset.x
                            translationY = offset.y
                        }
                )
            }

            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Black.copy(alpha = 0.75f), Color.Transparent)
                        )
                    )
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "@${post.authorHandle}",
                        style = MaterialTheme.typography.titleMedium,
                        color = tokens.highlight
                    )
                    Text(
                        text = "${post.location} • Filter: ${filter.displayName} • Pinch to zoom",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_inspector_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.action_close),
                        tint = Color.White
                    )
                }
            }

            // Bottom export action
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                        )
                    )
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = post.caption,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.85f),
                    maxLines = 2,
                    modifier = Modifier.weight(1f).padding(end = 12.dp)
                )
                Button(
                    onClick = onSaveToGallery,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = tokens.accent,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.testTag("inspector_save_hd_button")
                ) {
                    Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save HD")
                }
            }
        }
    }
}

@Composable
fun StoryViewerDialog(
    stories: List<StoryEntity>,
    initialIndex: Int,
    ghostModeEnabled: Boolean,
    onStoryShown: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val tokens = LocalGoldTokens.current
    var currentIndex by remember(initialIndex) {
        mutableIntStateOf(initialIndex.coerceIn(0, (stories.size - 1).coerceAtLeast(0)))
    }
    var progress by remember(currentIndex) { mutableFloatStateOf(0f) }
    var isPaused by remember { mutableStateOf(false) }

    val currentStory = stories.getOrNull(currentIndex)
    if (currentStory == null) {
        onDismiss()
        return
    }

    LaunchedEffect(currentStory.id) {
        onStoryShown(currentStory.id)
    }

    LaunchedEffect(currentIndex, isPaused) {
        while (progress < 1f) {
            delay(50L)
            if (!isPaused) {
                progress += 0.01f
            }
        }
        if (currentIndex < stories.lastIndex) {
            currentIndex++
            progress = 0f
        } else {
            onDismiss()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .statusBarsPadding()
                .navigationBarsPadding()
                .pointerInput(currentIndex, stories.size) {
                    detectTapGestures(
                        onPress = {
                            isPaused = true
                            tryAwaitRelease()
                            isPaused = false
                        },
                        onTap = { offset ->
                            if (offset.x < size.width * 0.35f) {
                                if (currentIndex > 0) {
                                    currentIndex--
                                    progress = 0f
                                } else {
                                    progress = 0f
                                }
                            } else {
                                if (currentIndex < stories.lastIndex) {
                                    currentIndex++
                                    progress = 0f
                                } else {
                                    onDismiss()
                                }
                            }
                        }
                    )
                }
        ) {
            GoldMediaImage(
                mediaKey = currentStory.mediaKey,
                contentDescription = currentStory.headline,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Scrim & Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Black.copy(alpha = 0.78f), Color.Transparent)
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    stories.forEachIndexed { idx, _ ->
                        val barProgress = when {
                            idx < currentIndex -> 1f
                            idx == currentIndex -> progress.coerceIn(0f, 1f)
                            else -> 0f
                        }
                        LinearProgressIndicator(
                            progress = { barProgress },
                            color = tokens.highlight,
                            trackColor = Color.White.copy(alpha = 0.25f),
                            modifier = Modifier
                                .weight(1f)
                                .height(3.dp)
                                .clip(RoundedCornerShape(50))
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        GoldAvatar(
                            mediaKey = currentStory.mediaKey,
                            contentDescription = currentStory.authorName,
                            size = 38.dp,
                            hasUnseenStory = true
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentStory.authorHandle,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = Color.White
                                )
                                if (currentStory.isVerified) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    VerifiedGoldBadge()
                                }
                            }
                            if (ghostModeEnabled) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.VisibilityOff,
                                        contentDescription = null,
                                        tint = tokens.highlight,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Ghost View Active",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = tokens.highlight
                                    )
                                }
                            }
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.action_close),
                            tint = Color.White
                        )
                    }
                }
            }

            // Bottom caption card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                        )
                    )
                    .padding(20.dp)
            ) {
                Text(
                    text = currentStory.headline,
                    style = MaterialTheme.typography.headlineSmall,
                    color = tokens.highlight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = currentStory.subline,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
fun EditProfileDialog(
    profile: UserProfileEntity,
    onSave: (displayName: String, handle: String, bio: String, website: String) -> Unit,
    onDismiss: () -> Unit
) {
    var displayName by remember(profile) { mutableStateOf(profile.displayName) }
    var handle by remember(profile) { mutableStateOf(profile.handle) }
    var bio by remember(profile) { mutableStateOf(profile.bio) }
    var website by remember(profile) { mutableStateOf(profile.website) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit Gold Profile",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("Display Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_name_input")
                )
                OutlinedTextField(
                    value = handle,
                    onValueChange = { handle = it },
                    label = { Text("Handle") },
                    singleLine = true,
                    prefix = { Text("@") },
                    modifier = Modifier.fillMaxWidth().testTag("edit_handle_input")
                )
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth().testTag("edit_bio_input")
                )
                OutlinedTextField(
                    value = website,
                    onValueChange = { website = it },
                    label = { Text("Website / Link") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_website_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(displayName, handle, bio, website)
                    onDismiss()
                },
                modifier = Modifier.testTag("save_profile_button")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
