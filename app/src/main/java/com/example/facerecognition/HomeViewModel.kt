package com.example.facerecognition

import androidx.lifecycle.*
import com.example.facerecognition.db.ImagesModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val imagesRepo: ImageRepository
) : ViewModel() {
    private val _state: MutableLiveData<HomeViewState> = MutableLiveData()
    val state: LiveData<HomeViewState> get() = _state

    fun loadImages() {
        _state.value = HomeViewState.ShowLoading
        CoroutineScope(viewModelScope.coroutineContext).launch {
            val images = imagesRepo.getImages()
            if (images.isNullOrEmpty()) {
                _state.value = HomeViewState.EmptyImages
                return@launch
            }
            _state.value = HomeViewState.ShowImages(images)
        }
    }
}

sealed class HomeViewState {
    object ShowLoading : HomeViewState()
    object EmptyImages : HomeViewState()

    data class ShowImages(val images: List<ImagesModel>) : HomeViewState()
}
