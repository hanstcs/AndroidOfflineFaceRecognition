package com.example.facerecognition.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.facerecognition.db.Constants.QUERY_GET_IMAGE_BY_ID
import com.example.facerecognition.db.Constants.QUERY_SELECT_ALL_IMAGES

@Dao
interface ImageDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(image: ImagesModel)

    @Query(QUERY_SELECT_ALL_IMAGES)
    suspend fun getAll(): List<ImagesModel>

    @Query(QUERY_GET_IMAGE_BY_ID)
    suspend fun getByFileName(name: String): ImagesModel
}
