package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface InstaGoldDao {
    @Query("SELECT * FROM posts ORDER BY createdAtMillis DESC")
    fun observePosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE isBookmarked = 1 ORDER BY createdAtMillis DESC")
    fun observeBookmarkedPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM stories ORDER BY isSeen ASC, createdAtMillis DESC")
    fun observeStories(): Flow<List<StoryEntity>>

    @Query("SELECT * FROM comments ORDER BY createdAtMillis ASC")
    fun observeAllComments(): Flow<List<CommentEntity>>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun observeUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT COUNT(*) FROM posts")
    suspend fun getPostCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(stories: List<StoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<CommentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUserProfile(profile: UserProfileEntity)

    @Query(
        """
        UPDATE posts
        SET isLiked = CASE WHEN isLiked = 1 THEN 0 ELSE 1 END,
            likesCount = CASE WHEN isLiked = 1 THEN MAX(0, likesCount - 1) ELSE likesCount + 1 END
        WHERE id = :postId
        """
    )
    suspend fun toggleLike(postId: Long)

    @Query("UPDATE posts SET isBookmarked = CASE WHEN isBookmarked = 1 THEN 0 ELSE 1 END WHERE id = :postId")
    suspend fun toggleBookmark(postId: Long)

    @Query("UPDATE stories SET isSeen = 1 WHERE id = :storyId")
    suspend fun markStorySeen(storyId: Long)

    @Query("UPDATE stories SET isSeen = 0")
    suspend fun resetAllStoriesUnseen()

    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePost(postId: Long)

    @Query("DELETE FROM comments WHERE postId = :postId")
    suspend fun deleteCommentsForPost(postId: Long)
}
