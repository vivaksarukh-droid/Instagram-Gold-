package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val authorHandle: String,
    val authorName: String,
    val authorVerified: Boolean,
    val authorAvatarKey: String,
    val location: String,
    val caption: String,
    val category: String,
    val mediaKey: String,
    val aspectRatio: Float,
    val filterName: String,
    val likesCount: Int,
    val isLiked: Boolean,
    val isBookmarked: Boolean,
    val isGoldExclusive: Boolean,
    val isUserAuthored: Boolean,
    val createdAtMillis: Long
)

@Entity(tableName = "stories")
data class StoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val authorHandle: String,
    val authorName: String,
    val mediaKey: String,
    val headline: String,
    val subline: String,
    val isSeen: Boolean,
    val isVerified: Boolean,
    val createdAtMillis: Long
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val postId: Long,
    val authorHandle: String,
    val text: String,
    val createdAtMillis: Long
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val handle: String,
    val displayName: String,
    val bio: String,
    val website: String,
    val followersCount: Int,
    val followingCount: Int
)
