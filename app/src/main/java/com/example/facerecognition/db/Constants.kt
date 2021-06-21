package com.example.facerecognition.db

object Constants {
    const val DB_NAME = "faceoff.db"

    const val IMAGES_TABLE_NAME = "images"

    const val QUERY_SELECT_ALL_IMAGES = "SELECT * FROM $IMAGES_TABLE_NAME"
    const val QUERY_GET_IMAGE_BY_ID = "SELECT * FROM $IMAGES_TABLE_NAME WHERE id = :name LIMIT 1"
}
