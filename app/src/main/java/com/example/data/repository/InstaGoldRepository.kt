package com.example.data.repository

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.example.R
import com.example.data.local.CommentEntity
import com.example.data.local.InstaGoldDao
import com.example.data.local.PostEntity
import com.example.data.local.StoryEntity
import com.example.data.local.UserProfileEntity
import com.example.data.model.GoldFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class InstaGoldRepository(
    private val context: Context,
    private val dao: InstaGoldDao
) {
    val postsFlow: Flow<List<PostEntity>> = dao.observePosts()
    val bookmarkedPostsFlow: Flow<List<PostEntity>> = dao.observeBookmarkedPosts()
    val storiesFlow: Flow<List<StoryEntity>> = dao.observeStories()
    val commentsFlow: Flow<List<CommentEntity>> = dao.observeAllComments()
    val userProfileFlow: Flow<UserProfileEntity?> = dao.observeUserProfile()

    suspend fun ensureSeedData() = withContext(Dispatchers.IO) {
        if (dao.getPostCount() > 0) return@withContext

        val now = System.currentTimeMillis()
        val hour = 3_600_000L

        dao.upsertUserProfile(
            UserProfileEntity(
                id = 1,
                handle = "aarav.atelier",
                displayName = "Aarav Mehta",
                bio = "24K minimalism, architectural light & horology.\nCurating golden hour moments across Mumbai & Milan.",
                website = "atelier.instagold.app",
                followersCount = 14820,
                followingCount = 312
            )
        )

        dao.insertStories(
            listOf(
                StoryEntity(
                    authorHandle = "casa.aurelia",
                    authorName = "Casa Aurelia",
                    mediaKey = "drawable:img_gold_architecture",
                    headline = "Travertine & Sunset",
                    subline = "Private courtyard walkthrough in Tuscany",
                    isSeen = false,
                    isVerified = true,
                    createdAtMillis = now - hour
                ),
                StoryEntity(
                    authorHandle = "kronos.geneve",
                    authorName = "Kronos Genève",
                    mediaKey = "drawable:img_gold_timepiece",
                    headline = "Calibre 24K Skeleton",
                    subline = "Hand-brushed 18k & 24k exhibition bridges",
                    isSeen = false,
                    isVerified = true,
                    createdAtMillis = now - 2 * hour
                ),
                StoryEntity(
                    authorHandle = "sahara.light",
                    authorName = "Sahara Light",
                    mediaKey = "drawable:img_gold_dunes",
                    headline = "Erg Chebbi Dusk",
                    subline = "Last 15 minutes of desert amber light",
                    isSeen = false,
                    isVerified = true,
                    createdAtMillis = now - 3 * hour
                ),
                StoryEntity(
                    authorHandle = "noir.espresso",
                    authorName = "Noir Espresso",
                    mediaKey = "drawable:img_gold_espresso",
                    headline = "Morning Ritual",
                    subline = "Single-origin ristretto in porcelain & brass",
                    isSeen = true,
                    isVerified = false,
                    createdAtMillis = now - 5 * hour
                )
            )
        )

        dao.insertPosts(
            listOf(
                PostEntity(
                    authorHandle = "casa.aurelia",
                    authorName = "Casa Aurelia",
                    authorVerified = true,
                    authorAvatarKey = "drawable:img_gold_architecture",
                    location = "Val d'Orcia, Italy",
                    caption = "When travertine stone catches the last 20 minutes of Mediterranean gold. Silence reflected in still water.",
                    category = "Architecture",
                    mediaKey = "drawable:img_gold_architecture",
                    aspectRatio = 4f / 3f,
                    filterName = GoldFilter.GOLD_24K.name,
                    likesCount = 3842,
                    isLiked = false,
                    isBookmarked = true,
                    isGoldExclusive = true,
                    isUserAuthored = false,
                    createdAtMillis = now - 2 * hour
                ),
                PostEntity(
                    authorHandle = "kronos.geneve",
                    authorName = "Kronos Genève",
                    authorVerified = true,
                    authorAvatarKey = "drawable:img_gold_timepiece",
                    location = "Geneva Salon",
                    caption = "Brushed 24K bullion finish over volcanic slate. Every gear chamfered by hand for 140 hours.",
                    category = "Horology",
                    mediaKey = "drawable:img_gold_timepiece",
                    aspectRatio = 1f,
                    filterName = GoldFilter.NATURAL.name,
                    likesCount = 5219,
                    isLiked = true,
                    isBookmarked = false,
                    isGoldExclusive = true,
                    isUserAuthored = false,
                    createdAtMillis = now - 5 * hour
                ),
                PostEntity(
                    authorHandle = "sahara.light",
                    authorName = "Sahara Light",
                    authorVerified = true,
                    authorAvatarKey = "drawable:img_gold_dunes",
                    location = "Merzouga Dunes",
                    caption = "Liquid amber sculpted by wind. No artificial grading—just pure desert sunset and long shadows.",
                    category = "Travel",
                    mediaKey = "drawable:img_gold_dunes",
                    aspectRatio = 3f / 4f,
                    filterName = GoldFilter.AMBER_GLOW.name,
                    likesCount = 6104,
                    isLiked = false,
                    isBookmarked = false,
                    isGoldExclusive = false,
                    isUserAuthored = false,
                    createdAtMillis = now - 9 * hour
                ),
                PostEntity(
                    authorHandle = "aarav.atelier",
                    authorName = "Aarav Mehta",
                    authorVerified = true,
                    authorAvatarKey = "drawable:img_gold_espresso",
                    location = "Kala Ghoda, Mumbai",
                    caption = "Slow mornings before sketching new studio layouts. Gold-rimmed crema, walnut grain, and quiet focus.",
                    category = "Lifestyle",
                    mediaKey = "drawable:img_gold_espresso",
                    aspectRatio = 1f,
                    filterName = GoldFilter.CHAMPAGNE_MATTE.name,
                    likesCount = 1924,
                    isLiked = false,
                    isBookmarked = true,
                    isGoldExclusive = true,
                    isUserAuthored = true,
                    createdAtMillis = now - 14 * hour
                )
            )
        )

        dao.insertComments(
            listOf(
                CommentEntity(
                    postId = 1L,
                    authorHandle = "milan.design",
                    text = "The symmetry on the reflection pool is unreal.",
                    createdAtMillis = now - hour
                ),
                CommentEntity(
                    postId = 1L,
                    authorHandle = "aarav.atelier",
                    text = "Saved straight to my Gold Vault for reference.",
                    createdAtMillis = now - (hour / 2)
                ),
                CommentEntity(
                    postId = 2L,
                    authorHandle = "watch.collector",
                    text = "That brushed bezel against dark slate is pure 24K perfection.",
                    createdAtMillis = now - 3 * hour
                )
            )
        )
    }

    suspend fun toggleLike(postId: Long) = dao.toggleLike(postId)

    suspend fun toggleBookmark(postId: Long) = dao.toggleBookmark(postId)

    suspend fun addComment(postId: Long, authorHandle: String, text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        dao.insertComment(
            CommentEntity(
                postId = postId,
                authorHandle = authorHandle,
                text = trimmed,
                createdAtMillis = System.currentTimeMillis()
            )
        )
    }

    suspend fun markStorySeen(storyId: Long) = dao.markStorySeen(storyId)

    suspend fun resetAllStoriesUnseen() = dao.resetAllStoriesUnseen()

    suspend fun updateProfile(
        displayName: String,
        handle: String,
        bio: String,
        website: String,
        existingFollowers: Int,
        existingFollowing: Int
    ) {
        dao.upsertUserProfile(
            UserProfileEntity(
                id = 1,
                handle = handle.trim().ifEmpty { "aarav.atelier" }.removePrefix("@"),
                displayName = displayName.trim().ifEmpty { "Aarav Mehta" },
                bio = bio.trim(),
                website = website.trim(),
                followersCount = existingFollowers,
                followingCount = existingFollowing
            )
        )
    }

    suspend fun createPost(
        pickedUri: Uri?,
        fallbackDrawableKey: String,
        caption: String,
        location: String,
        category: String,
        filter: GoldFilter,
        isGoldExclusive: Boolean,
        authorHandle: String,
        authorName: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val mediaKey = if (pickedUri != null) {
                val postsDir = File(context.filesDir, "posts").apply { mkdirs() }
                val destFile = File(postsDir, "gold_post_${System.currentTimeMillis()}.jpg")
                context.contentResolver.openInputStream(pickedUri)?.use { input ->
                    FileOutputStream(destFile).use { output ->
                        input.copyTo(output)
                    }
                } ?: error("Unable to read selected image")
                "file:${destFile.absolutePath}"
            } else {
                fallbackDrawableKey
            }

            val aspectRatio = computeAspectRatio(mediaKey)
            dao.insertPost(
                PostEntity(
                    authorHandle = authorHandle,
                    authorName = authorName,
                    authorVerified = true,
                    authorAvatarKey = "drawable:img_gold_espresso",
                    location = location.trim().ifEmpty { "InstaGold Studio" },
                    caption = caption.trim(),
                    category = category,
                    mediaKey = mediaKey,
                    aspectRatio = aspectRatio,
                    filterName = filter.name,
                    likesCount = 1,
                    isLiked = true,
                    isBookmarked = false,
                    isGoldExclusive = isGoldExclusive,
                    isUserAuthored = true,
                    createdAtMillis = System.currentTimeMillis()
                )
            )
            Unit
        }
    }

    suspend fun deletePost(postId: Long, mediaKey: String) = withContext(Dispatchers.IO) {
        dao.deleteCommentsForPost(postId)
        dao.deletePost(postId)
        if (mediaKey.startsWith("file:")) {
            val path = mediaKey.removePrefix("file:")
            File(path).takeIf { it.exists() }?.delete()
        }
    }

    suspend fun saveMediaToGallery(
        mediaKey: String,
        filter: GoldFilter,
        highQuality: Boolean
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val rawBitmap = decodeBitmap(mediaKey) ?: error("Could not load image")
            val finalBitmap = applyFilterToBitmap(rawBitmap, filter)
            if (finalBitmap !== rawBitmap) rawBitmap.recycle()

            val fileName = "InstaGold_${System.currentTimeMillis()}.jpg"
            val quality = if (highQuality) 98 else 85

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/InstaGold")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
                val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                    ?: error("Unable to create gallery entry")
                context.contentResolver.openOutputStream(uri)?.use { out ->
                    finalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
                } ?: error("Unable to open gallery stream")
                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                context.contentResolver.update(uri, values, null, null)
                finalBitmap.recycle()
                "Pictures/InstaGold/$fileName"
            } else {
                val dir = File(
                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "InstaGold"
                ).apply { mkdirs() }
                val outFile = File(dir, fileName)
                FileOutputStream(outFile).use { out ->
                    finalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
                }
                finalBitmap.recycle()
                outFile.name
            }
        }
    }

    private fun computeAspectRatio(mediaKey: String): Float {
        val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        when {
            mediaKey.startsWith("file:") -> {
                BitmapFactory.decodeFile(mediaKey.removePrefix("file:"), opts)
            }
            mediaKey.startsWith("drawable:") -> {
                val resId = resolveDrawableResId(mediaKey)
                BitmapFactory.decodeResource(context.resources, resId, opts)
            }
        }
        return if (opts.outWidth > 0 && opts.outHeight > 0) {
            (opts.outWidth.toFloat() / opts.outHeight.toFloat()).coerceIn(0.72f, 1.45f)
        } else {
            1f
        }
    }

    private fun decodeBitmap(mediaKey: String): Bitmap? {
        return when {
            mediaKey.startsWith("file:") -> BitmapFactory.decodeFile(mediaKey.removePrefix("file:"))
            mediaKey.startsWith("drawable:") -> BitmapFactory.decodeResource(context.resources, resolveDrawableResId(mediaKey))
            else -> null
        }
    }

    private fun applyFilterToBitmap(source: Bitmap, filter: GoldFilter): Bitmap {
        val matrix = filter.toColorMatrix() ?: return source
        val result = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            colorFilter = ColorMatrixColorFilter(android.graphics.ColorMatrix(matrix.values))
        }
        canvas.drawBitmap(source, 0f, 0f, paint)
        return result
    }

    private fun resolveDrawableResId(mediaKey: String): Int {
        return when (mediaKey.removePrefix("drawable:")) {
            "img_gold_architecture" -> R.drawable.img_gold_architecture
            "img_gold_timepiece" -> R.drawable.img_gold_timepiece
            "img_gold_dunes" -> R.drawable.img_gold_dunes
            "img_gold_espresso" -> R.drawable.img_gold_espresso
            else -> R.drawable.img_gold_architecture
        }
    }
}
