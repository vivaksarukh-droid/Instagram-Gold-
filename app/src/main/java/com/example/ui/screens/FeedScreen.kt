package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.PostEntity
import com.example.data.local.StoryEntity
import com.example.ui.components.GoldAvatar
import com.example.ui.components.PostCard
import com.example.ui.theme.LocalGoldTokens
import com.example.ui.viewmodel.InstaGoldUiState

@Composable
fun FeedScreen(
    uiState: InstaGoldUiState,
    onOpenGoldStudio: () -> Unit,
    onNavigateToCreate: () -> Unit,
    onStoryClick: (Int) -> Unit,
    onToggleLike: (Long, Boolean) -> Unit,
    onToggleBookmark: (Long) -> Unit,
    onOpenComments: (PostEntity) -> Unit,
    onInspectMedia: (PostEntity) -> Unit,
    onSaveToGallery: (PostEntity) -> Unit,
    onCopyCaption: (PostEntity) -> Unit,
    onSharePost: (PostEntity) -> Unit,
    onDeletePost: (PostEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalGoldTokens.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 620.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Editorial Gold Top Bar
            item(key = "header") {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(R.string.brand_wordmark),
                                style = MaterialTheme.typography.headlineMedium,
                                color = tokens.highlight
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(tokens.container)
                                    .border(1.dp, tokens.accent.copy(alpha = 0.5f), RoundedCornerShape(50))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.brand_edition_badge),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = tokens.highlight
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (uiState.studioSettings.ghostModeEnabled) {
                                Surface(
                                    onClick = onOpenGoldStudio,
                                    shape = RoundedCornerShape(50),
                                    color = tokens.container,
                                    modifier = Modifier.padding(end = 6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.VisibilityOff,
                                            contentDescription = "Ghost mode active",
                                            tint = tokens.highlight,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Ghost",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = tokens.highlight
                                        )
                                    }
                                }
                            }
                            IconButton(
                                onClick = onOpenGoldStudio,
                                modifier = Modifier.testTag("gold_studio_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AutoAwesome,
                                    contentDescription = stringResource(R.string.action_gold_studio),
                                    tint = tokens.accent
                                )
                            }
                        }
                    }
                    HorizontalDivider(color = tokens.hairline)
                }
            }

            // Stories Strip
            item(key = "stories") {
                StoriesStrip(
                    stories = uiState.stories,
                    userHandle = uiState.userProfile.handle,
                    onCreateClick = onNavigateToCreate,
                    onStoryClick = onStoryClick
                )
            }

            // Feed Posts
            items(uiState.posts, key = { it.id }) { post ->
                val commentCount = uiState.commentsByPost[post.id]?.size ?: 0
                PostCard(
                    post = post,
                    commentCount = commentCount,
                    showGoldFrame = uiState.studioSettings.goldFrameOverlay,
                    onToggleLike = { doubleTapOnly -> onToggleLike(post.id, doubleTapOnly) },
                    onToggleBookmark = { onToggleBookmark(post.id) },
                    onOpenComments = { onOpenComments(post) },
                    onInspectMedia = { onInspectMedia(post) },
                    onSaveToGallery = { onSaveToGallery(post) },
                    onCopyCaption = { onCopyCaption(post) },
                    onSharePost = { onSharePost(post) },
                    onDeletePost = if (post.isUserAuthored) {
                        { onDeletePost(post) }
                    } else null,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    }
}

@Composable
private fun StoriesStrip(
    stories: List<StoryEntity>,
    userHandle: String,
    onCreateClick: () -> Unit,
    onStoryClick: (Int) -> Unit
) {
    val tokens = LocalGoldTokens.current
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item(key = "your_post_shortcut") {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(72.dp)
                    .clickable(onClick = onCreateClick)
                    .testTag("story_create_shortcut")
            ) {
                Box(modifier = Modifier.size(66.dp), contentAlignment = Alignment.Center) {
                    GoldAvatar(
                        mediaKey = "drawable:img_gold_espresso",
                        contentDescription = "Your profile",
                        size = 64.dp,
                        hasUnseenStory = false
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(tokens.accent)
                            .border(2.dp, MaterialTheme.colorScheme.background, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Create post",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = userHandle,
                    style = MaterialTheme.typography.labelSmall,
                    color = tokens.mutedText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        itemsIndexed(stories, key = { _, s -> s.id }) { index, story ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(72.dp)
                    .clickable { onStoryClick(index) }
                    .testTag("story_item_${story.id}")
            ) {
                GoldAvatar(
                    mediaKey = story.mediaKey,
                    contentDescription = "${story.authorName} story",
                    size = 66.dp,
                    hasUnseenStory = !story.isSeen,
                    ringWidth = 2.25.dp
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = story.authorHandle,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (!story.isSeen) MaterialTheme.colorScheme.onSurface else tokens.mutedText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
