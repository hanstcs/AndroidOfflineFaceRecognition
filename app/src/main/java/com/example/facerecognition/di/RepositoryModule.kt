package com.example.facerecognition.di

import com.example.facerecognition.ImageRepository
import com.example.facerecognition.ImagesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindImageRepo(
        imageRepo: ImagesRepositoryImpl
    ): ImageRepository
}
