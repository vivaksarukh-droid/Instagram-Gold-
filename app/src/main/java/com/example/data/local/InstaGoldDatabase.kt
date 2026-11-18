package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        PostEntity::class,
        StoryEntity::class,
        CommentEntity::class,
        UserProfileEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class InstaGoldDatabase : RoomDatabase() {
    abstract fun instaGoldDao(): InstaGoldDao

    companion object {
        @Volatile
        private var INSTANCE: InstaGoldDatabase? = null

        fun getInstance(context: Context): InstaGoldDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    InstaGoldDatabase::class.java,
                    "instagold_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
