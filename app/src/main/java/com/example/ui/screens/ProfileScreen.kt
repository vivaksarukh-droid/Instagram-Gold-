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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.PostEntity
import com.example.data.local.UserProfileEntity
import com.example.data.model.GoldFilter
import com.example.ui.components.GoldAvatar
import com.example.ui.components.GoldMediaImage
import com.example.ui.components.VerifiedGoldBadge
import com.example.ui.theme.LocalGoldTokens
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProfileScreen(
    profile: UserProfileEntity,
    myPosts: List<PostEntity>,
    savedPosts: List<PostEntity>,
    onEditProfile: () -> Unit,
    onOpenGoldStudio: () -> Unit,
    onCopyBio: () -> Unit,
    onInspectMedia: (PostEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalGoldTokens.current
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val activePosts = if (selectedTab == 0) myPosts else savedPosts

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 640.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            item(span = { GridItemSpan(3) }, key = "profile_header") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "@${profile.handle}",
                                style = MaterialTheme.typography.titleLarge,
                                color = tokens.highlight
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            VerifiedGoldBadge(size = 18.dp)
                        }
                        IconButton(
                            onClick = onOpenGoldStudio,
                            modifier = Modifier.testTag("profile_gold_studio_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = stringResource(R.string.action_gold_studio),
                                tint = tokens.accent
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GoldAvatar(
                            mediaKey = "drawable:img_gold_espresso",
                            contentDescription = profile.displayName,
                            size = 82.dp,
                            hasUnseenStory = true,
                            ringWidth = 2.5.dp
                        )
                        Spacer(modifier = Modifier.width(18.dp))
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ProfileStat(label = "Posts", value = "${myPosts.size}")
                            ProfileStat(
                                label = "Followers",
                                value = formatCompactCount(profile.followersCount)
                            )
                            ProfileStat(label = "Vault", value = "${savedPosts.size}")
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = profile.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (profile.bio.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = profile.bio,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.88f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (profile.website.isNotBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Link,
                                    contentDescription = null,
                                    tint = tokens.accent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = profile.website,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = tokens.highlight,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        Surface(
                            onClick = onCopyBio,
                            shape = RoundedCornerShape(50),
                            color = tokens.surfaceCard,
                            modifier = Modifier
                                .border(1.dp, tokens.hairline, RoundedCornerShape(50))
                                .testTag("copy_bio_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ContentCopy,
                                    contentDescription = null,
                                    tint = tokens.mutedText,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = stringResource(R.string.action_copy_bio),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = tokens.mutedText
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onEditProfile,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("edit_profile_button")
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(stringResource(R.string.action_edit_profile))
                        }
                        Button(
                            onClick = onOpenGoldStudio,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = tokens.accent,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(stringResource(R.string.action_gold_studio))
                        }
                    }
                }
            }

            item(span = { GridItemSpan(3) }, key = "profile_tabs") {
                Column(modifier = Modifier.fillMaxWidth()) {
                    HorizontalDivider(color = tokens.hairline)
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = tokens.highlight,
                        indicator = { tabPositions ->
                            if (selectedTab < tabPositions.size) {
                                SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                    color = tokens.accent
                                )
                            }
                        }
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            icon = { Icon(Icons.Filled.GridOn, contentDescription = "My Posts") },
                            text = { Text("My Posts") }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            icon = { Icon(Icons.Filled.Bookmark, contentDescription = "Saved Vault") },
                            text = { Text("Vault") }
                        )
                    }
                }
            }

            if (activePosts.isEmpty()) {
                item(span = { GridItemSpan(3) }, key = "profile_empty") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp, horizontal = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (selectedTab == 0) {
                                "Tap Create to publish your first 24K photo."
                            } else {
                                "Save any post in the feed to keep it in your Vault."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = tokens.mutedText
                        )
                    }
                }
            } else {
                items(activePosts, key = { "${selectedTab}_${it.id}" }) { post ->
                    val filter = remember(post.filterName) { GoldFilter.fromName(post.filterName) }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { onInspectMedia(post) }
                            .testTag("profile_grid_post_${post.id}")
                    ) {
                        GoldMediaImage(
                            mediaKey = post.mediaKey,
                            contentDescription = post.caption,
                            filter = filter,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileStat(label: String, value: String) {
    val tokens = LocalGoldTokens.current
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = tokens.highlight
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = tokens.mutedText
        )
    }
}

private fun formatCompactCount(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format(Locale.US, "%.1fM", count / 1_000_000.0)
        count >= 1_000 -> String.format(Locale.US, "%.1fK", count / 1_000.0)
        else -> count.toString()
    }
}
