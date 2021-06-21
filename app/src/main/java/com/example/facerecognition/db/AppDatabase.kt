package com.example.facerecognition.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ImagesModel::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun imageDAO(): ImageDAO
}
