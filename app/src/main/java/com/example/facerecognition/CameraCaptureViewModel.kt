package com.example.facerecognition

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraCaptureViewModel @Inject constructor(
    private val imagesRepo: ImageRepository
) : ViewModel() {
    private val _state: MutableLiveData<CameraCaptureViewState> = MutableLiveData()
    val state: LiveData<CameraCaptureViewState> get() = _state

    fun saveImageName(name: String) {
        _state.value = CameraCaptureViewState.ShowLoading
        CoroutineScope(viewModelScope.coroutineContext).launch {
            imagesRepo.insertImage(name, "")
            _state.value = CameraCaptureViewState.SuccessSaveImage(name)
        }
    }

    fun failedSaveImage(message: String?) {
        _state.value = CameraCaptureViewState.FailedSaveImage(message)
    }
}

sealed class CameraCaptureViewState {
    object ShowLoading : CameraCaptureViewState()

    data class FailedSaveImage(val message: String?) : CameraCaptureViewState()
    data class SuccessSaveImage(val imagePath: String) : CameraCaptureViewState()
}
