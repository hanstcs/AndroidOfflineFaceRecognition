package com.example.facerecognition.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = Constants.IMAGES_TABLE_NAME)
class ImagesModel(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "label") val label: String?
)
