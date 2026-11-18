package com.example.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CommentEntity
import com.example.data.local.InstaGoldDatabase
import com.example.data.local.PostEntity
import com.example.data.local.StoryEntity
import com.example.data.local.UserProfileEntity
import com.example.data.model.GoldFilter
import com.example.data.model.GoldPalette
import com.example.data.preferences.GoldPreferencesRepository
import com.example.data.preferences.GoldStudioSettings
import com.example.data.repository.InstaGoldRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class InstaGoldUiState(
    val isLoading: Boolean = true,
    val posts: List<PostEntity> = emptyList(),
    val bookmarkedPosts: List<PostEntity> = emptyList(),
    val stories: List<StoryEntity> = emptyList(),
    val commentsByPost: Map<Long, List<CommentEntity>> = emptyMap(),
    val userProfile: UserProfileEntity = UserProfileEntity(
        id = 1,
        handle = "aarav.atelier",
        displayName = "Aarav Mehta",
        bio = "24K minimalism, architectural light & horology.",
        website = "atelier.instagold.app",
        followersCount = 14820,
        followingCount = 312
    ),
    val studioSettings: GoldStudioSettings = GoldStudioSettings()
) {
    val myPosts: List<PostEntity>
        get() = posts.filter { it.isUserAuthored }
}

class InstaGoldViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = InstaGoldDatabase.getInstance(application).instaGoldDao()
    private val repository = InstaGoldRepository(application, dao)
    private val preferences = GoldPreferencesRepository(application)

    private val _events = MutableSharedFlow<String>(extraBufferCapacity = 8)
    val events: SharedFlow<String> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            repository.ensureSeedData()
        }
    }

    val uiState: StateFlow<InstaGoldUiState> = combine(
        repository.postsFlow,
        repository.bookmarkedPostsFlow,
        repository.storiesFlow,
        repository.commentsFlow,
        repository.userProfileFlow,
        preferences.settingsFlow
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        val posts = values[0] as List<PostEntity>
        @Suppress("UNCHECKED_CAST")
        val bookmarked = values[1] as List<PostEntity>
        @Suppress("UNCHECKED_CAST")
        val stories = values[2] as List<StoryEntity>
        @Suppress("UNCHECKED_CAST")
        val comments = values[3] as List<CommentEntity>
        val profile = values[4] as UserProfileEntity?
        val settings = values[5] as GoldStudioSettings

        InstaGoldUiState(
            isLoading = false,
            posts = posts,
            bookmarkedPosts = bookmarked,
            stories = stories,
            commentsByPost = comments.groupBy { it.postId },
            userProfile = profile ?: InstaGoldUiState().userProfile,
            studioSettings = settings
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = InstaGoldUiState()
    )

    fun toggleLike(postId: Long, forceLikeOnly: Boolean = false) {
        viewModelScope.launch {
            val alreadyLiked = uiState.value.posts.firstOrNull { it.id == postId }?.isLiked == true
            if (forceLikeOnly && alreadyLiked) return@launch
            repository.toggleLike(postId)
        }
    }

    fun toggleBookmark(postId: Long) {
        viewModelScope.launch {
            val wasBookmarked = uiState.value.posts.firstOrNull { it.id == postId }?.isBookmarked == true
            repository.toggleBookmark(postId)
            _events.tryEmit(if (wasBookmarked) "Removed from Gold Vault" else "Saved to Gold Vault")
        }
    }

    fun addComment(postId: Long, text: String) {
        viewModelScope.launch {
            val handle = uiState.value.userProfile.handle
            repository.addComment(postId, handle, text)
        }
    }

    fun onStoryViewed(storyId: Long) {
        if (uiState.value.studioSettings.ghostModeEnabled) return
        viewModelScope.launch {
            repository.markStorySeen(storyId)
        }
    }

    fun resetAllStoriesUnseen() {
        viewModelScope.launch {
            repository.resetAllStoriesUnseen()
            _events.tryEmit("All story rings refreshed")
        }
    }

    fun createPost(
        pickedUri: Uri?,
        fallbackDrawableKey: String,
        caption: String,
        location: String,
        category: String,
        filter: GoldFilter,
        isGoldExclusive: Boolean,
        onPublished: () -> Unit
    ) {
        viewModelScope.launch {
            val profile = uiState.value.userProfile
            val result = repository.createPost(
                pickedUri = pickedUri,
                fallbackDrawableKey = fallbackDrawableKey,
                caption = caption,
                location = location,
                category = category,
                filter = filter,
                isGoldExclusive = isGoldExclusive,
                authorHandle = profile.handle,
                authorName = profile.displayName
            )
            result.onSuccess {
                _events.tryEmit("Published to your InstaGold feed")
                onPublished()
            }.onFailure { err ->
                _events.tryEmit(err.localizedMessage ?: "Failed to publish post")
            }
        }
    }

    fun deletePost(post: PostEntity) {
        viewModelScope.launch {
            repository.deletePost(post.id, post.mediaKey)
            _events.tryEmit("Post deleted")
        }
    }

    fun updateProfile(displayName: String, handle: String, bio: String, website: String) {
        viewModelScope.launch {
            val current = uiState.value.userProfile
            repository.updateProfile(
                displayName = displayName,
                handle = handle,
                bio = bio,
                website = website,
                existingFollowers = current.followersCount,
                existingFollowing = current.followingCount
            )
            _events.tryEmit("Profile updated")
        }
    }

    fun saveMediaToGallery(mediaKey: String, filter: GoldFilter) {
        viewModelScope.launch {
            val highQuality = uiState.value.studioSettings.hdSaveEnabled
            repository.saveMediaToGallery(mediaKey, filter, highQuality)
                .onSuccess { path ->
                    _events.tryEmit("Saved HD photo to $path")
                }
                .onFailure { err ->
                    _events.tryEmit(err.localizedMessage ?: "Could not save photo")
                }
        }
    }

    fun setGoldPalette(palette: GoldPalette) {
        viewModelScope.launch { preferences.setPalette(palette) }
    }

    fun setGhostMode(enabled: Boolean) {
        viewModelScope.launch { preferences.setGhostMode(enabled) }
    }

    fun setHdSave(enabled: Boolean) {
        viewModelScope.launch { preferences.setHdSave(enabled) }
    }

    fun setGoldFrameOverlay(enabled: Boolean) {
        viewModelScope.launch { preferences.setGoldFrameOverlay(enabled) }
    }
}
