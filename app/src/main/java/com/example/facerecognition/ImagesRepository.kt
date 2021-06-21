package com.example.facerecognition

import com.example.facerecognition.db.AppDatabase
import com.example.facerecognition.db.ImagesModel
import javax.inject.Inject

interface ImageRepository {
    suspend fun getImages(): List<ImagesModel>
    suspend fun getByName(fileName: String): ImagesModel
    suspend fun insertImage(fileName: String, label: String?)
}

class ImagesRepositoryImpl @Inject constructor(
    private val db: AppDatabase
) : ImageRepository {

    override suspend fun getImages(): List<ImagesModel> = db.imageDAO().getAll()

    override suspend fun getByName(fileName: String): ImagesModel =
        db.imageDAO().getByFileName(fileName)

    override suspend fun insertImage(fileName: String, label: String?) =
        db.imageDAO()
            .insert(ImagesModel(fileName, label))
}
