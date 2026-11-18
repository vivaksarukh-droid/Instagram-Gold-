package com.example.ui

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.R
import com.example.data.local.PostEntity
import com.example.data.model.GoldFilter
import com.example.ui.components.CommentsBottomSheet
import com.example.ui.components.EditProfileDialog
import com.example.ui.components.GoldStudioBottomSheet
import com.example.ui.components.HdMediaInspectorDialog
import com.example.ui.components.StoryViewerDialog
import com.example.ui.screens.CreatePostScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.FeedScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.VaultScreen
import com.example.ui.theme.InstaGoldTheme
import com.example.ui.theme.LocalGoldTokens
import com.example.ui.viewmodel.InstaGoldViewModel
import kotlinx.coroutines.launch

object InstaGoldRoutes {
    const val FEED = "feed"
    const val EXPLORE = "explore"
    const val CREATE = "create"
    const val VAULT = "vault"
    const val PROFILE = "profile"
}

private data class NavDestination(
    val route: String,
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

private val TopDestinations = listOf(
    NavDestination(
        route = InstaGoldRoutes.FEED,
        labelRes = R.string.nav_feed,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        testTag = "nav_feed"
    ),
    NavDestination(
        route = InstaGoldRoutes.EXPLORE,
        labelRes = R.string.nav_explore,
        selectedIcon = Icons.Filled.Explore,
        unselectedIcon = Icons.Outlined.Explore,
        testTag = "nav_explore"
    ),
    NavDestination(
        route = InstaGoldRoutes.CREATE,
        labelRes = R.string.nav_create,
        selectedIcon = Icons.Filled.AddCircle,
        unselectedIcon = Icons.Outlined.AddCircleOutline,
        testTag = "nav_create"
    ),
    NavDestination(
        route = InstaGoldRoutes.VAULT,
        labelRes = R.string.nav_vault,
        selectedIcon = Icons.Filled.Bookmark,
        unselectedIcon = Icons.Outlined.BookmarkBorder,
        testTag = "nav_vault"
    ),
    NavDestination(
        route = InstaGoldRoutes.PROFILE,
        labelRes = R.string.nav_profile,
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        testTag = "nav_profile"
    )
)

@Composable
fun InstaGoldApp(
    viewModel: InstaGoldViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    @Suppress("DEPRECATION")
    val clipboard = LocalClipboardManager.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    InstaGoldTheme(goldPalette = uiState.studioSettings.palette) {
        val tokens = LocalGoldTokens.current
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route ?: InstaGoldRoutes.FEED

        var commentsPostId by remember { mutableStateOf<Long?>(null) }
        var inspectPost by remember { mutableStateOf<PostEntity?>(null) }
        var storyStartIndex by remember { mutableStateOf<Int?>(null) }
        var showGoldStudio by remember { mutableStateOf(false) }
        var showEditProfile by remember { mutableStateOf(false) }

        val navigateToTab: (String) -> Unit = { route ->
            navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }

        val sharePost: (PostEntity) -> Unit = { post ->
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "InstaGold • @${post.authorHandle}")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "@${post.authorHandle} (${post.location}): ${post.caption} #InstaGold"
                )
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share via InstaGold"))
        }

        val copyCaption: (PostEntity) -> Unit = { post ->
            clipboard.setText(AnnotatedString(post.caption))
            coroutineScope.launch {
                snackbarHostState.showSnackbar(context.getString(R.string.msg_caption_copied))
            }
        }

        val copyBio: () -> Unit = {
            clipboard.setText(AnnotatedString(uiState.userProfile.bio))
            coroutineScope.launch {
                snackbarHostState.showSnackbar(context.getString(R.string.msg_bio_copied))
            }
        }

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val useRail = maxWidth >= 600.dp

            Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                contentWindowInsets = WindowInsets.safeDrawing,
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState) { data ->
                        Snackbar(
                            snackbarData = data,
                            containerColor = tokens.surfaceElevated,
                            contentColor = tokens.highlight,
                            actionColor = tokens.accent
                        )
                    }
                },
                bottomBar = {
                    if (!useRail) {
                        Box {
                            HorizontalDivider(
                                color = tokens.hairline,
                                modifier = Modifier.align(Alignment.TopCenter)
                            )
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = tokens.highlight
                            ) {
                                TopDestinations.forEach { dest ->
                                    val selected = currentRoute == dest.route
                                    NavigationBarItem(
                                        selected = selected,
                                        onClick = { navigateToTab(dest.route) },
                                        icon = {
                                            Icon(
                                                imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                                contentDescription = stringResource(dest.labelRes)
                                            )
                                        },
                                        label = { Text(stringResource(dest.labelRes)) },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = tokens.highlight,
                                            selectedTextColor = tokens.highlight,
                                            indicatorColor = tokens.container,
                                            unselectedIconColor = tokens.mutedText,
                                            unselectedTextColor = tokens.mutedText
                                        ),
                                        modifier = Modifier.testTag(dest.testTag)
                                    )
                                }
                            }
                        }
                    }
                }
            ) { innerPadding ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (useRail) {
                        NavigationRail(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = tokens.highlight,
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            TopDestinations.forEach { dest ->
                                val selected = currentRoute == dest.route
                                NavigationRailItem(
                                    selected = selected,
                                    onClick = { navigateToTab(dest.route) },
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                            contentDescription = stringResource(dest.labelRes)
                                        )
                                    },
                                    label = { Text(stringResource(dest.labelRes)) },
                                    colors = NavigationRailItemDefaults.colors(
                                        selectedIconColor = tokens.highlight,
                                        selectedTextColor = tokens.highlight,
                                        indicatorColor = tokens.container,
                                        unselectedIconColor = tokens.mutedText,
                                        unselectedTextColor = tokens.mutedText
                                    ),
                                    modifier = Modifier.testTag(dest.testTag)
                                )
                            }
                        }
                        VerticalDivider(color = tokens.hairline)
                    }

                    if (uiState.isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = tokens.accent)
                        }
                    } else {
                        NavHost(
                            navController = navController,
                            startDestination = InstaGoldRoutes.FEED,
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        ) {
                            composable(InstaGoldRoutes.FEED) {
                                FeedScreen(
                                    uiState = uiState,
                                    onOpenGoldStudio = { showGoldStudio = true },
                                    onNavigateToCreate = { navigateToTab(InstaGoldRoutes.CREATE) },
                                    onStoryClick = { idx -> storyStartIndex = idx },
                                    onToggleLike = { postId, forceOnly ->
                                        viewModel.toggleLike(postId, forceLikeOnly = forceOnly)
                                    },
                                    onToggleBookmark = { postId -> viewModel.toggleBookmark(postId) },
                                    onOpenComments = { post -> commentsPostId = post.id },
                                    onInspectMedia = { post -> inspectPost = post },
                                    onSaveToGallery = { post ->
                                        viewModel.saveMediaToGallery(
                                            post.mediaKey,
                                            GoldFilter.fromName(post.filterName)
                                        )
                                    },
                                    onCopyCaption = copyCaption,
                                    onSharePost = sharePost,
                                    onDeletePost = { post -> viewModel.deletePost(post) }
                                )
                            }
                            composable(InstaGoldRoutes.EXPLORE) {
                                ExploreScreen(
                                    posts = uiState.posts,
                                    onInspectMedia = { post -> inspectPost = post }
                                )
                            }
                            composable(InstaGoldRoutes.CREATE) {
                                CreatePostScreen(
                                    onPublish = { uri, fallbackKey, caption, location, category, filter, exclusive ->
                                        viewModel.createPost(
                                            pickedUri = uri,
                                            fallbackDrawableKey = fallbackKey,
                                            caption = caption,
                                            location = location,
                                            category = category,
                                            filter = filter,
                                            isGoldExclusive = exclusive,
                                            onPublished = { navigateToTab(InstaGoldRoutes.FEED) }
                                        )
                                    }
                                )
                            }
                            composable(InstaGoldRoutes.VAULT) {
                                VaultScreen(
                                    bookmarkedPosts = uiState.bookmarkedPosts,
                                    onInspectMedia = { post -> inspectPost = post },
                                    onToggleBookmark = { postId -> viewModel.toggleBookmark(postId) },
                                    onSaveToGallery = { post ->
                                        viewModel.saveMediaToGallery(
                                            post.mediaKey,
                                            GoldFilter.fromName(post.filterName)
                                        )
                                    }
                                )
                            }
                            composable(InstaGoldRoutes.PROFILE) {
                                ProfileScreen(
                                    profile = uiState.userProfile,
                                    myPosts = uiState.myPosts,
                                    savedPosts = uiState.bookmarkedPosts,
                                    onEditProfile = { showEditProfile = true },
                                    onOpenGoldStudio = { showGoldStudio = true },
                                    onCopyBio = copyBio,
                                    onInspectMedia = { post -> inspectPost = post }
                                )
                            }
                        }
                    }
                }
            }

            // Modals & Inspectors
            commentsPostId?.let { postId ->
                val targetPost = uiState.posts.firstOrNull { it.id == postId }
                if (targetPost != null) {
                    CommentsBottomSheet(
                        post = targetPost,
                        comments = uiState.commentsByPost[postId].orEmpty(),
                        onAddComment = { text -> viewModel.addComment(postId, text) },
                        onDismiss = { commentsPostId = null }
                    )
                }
            }

            inspectPost?.let { post ->
                HdMediaInspectorDialog(
                    post = post,
                    onSaveToGallery = {
                        viewModel.saveMediaToGallery(
                            post.mediaKey,
                            GoldFilter.fromName(post.filterName)
                        )
                    },
                    onDismiss = { inspectPost = null }
                )
            }

            storyStartIndex?.let { startIdx ->
                StoryViewerDialog(
                    stories = uiState.stories,
                    initialIndex = startIdx,
                    ghostModeEnabled = uiState.studioSettings.ghostModeEnabled,
                    onStoryShown = { storyId -> viewModel.onStoryViewed(storyId) },
                    onDismiss = { storyStartIndex = null }
                )
            }

            if (showGoldStudio) {
                GoldStudioBottomSheet(
                    settings = uiState.studioSettings,
                    onSelectPalette = viewModel::setGoldPalette,
                    onToggleGhostMode = viewModel::setGhostMode,
                    onToggleHdSave = viewModel::setHdSave,
                    onToggleGoldFrame = viewModel::setGoldFrameOverlay,
                    onResetStories = viewModel::resetAllStoriesUnseen,
                    onDismiss = { showGoldStudio = false }
                )
            }

            if (showEditProfile) {
                EditProfileDialog(
                    profile = uiState.userProfile,
                    onSave = viewModel::updateProfile,
                    onDismiss = { showEditProfile = false }
                )
            }
        }
    }
}
