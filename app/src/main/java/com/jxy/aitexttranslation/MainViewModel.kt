package com.jxy.aitexttranslation

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {

    private var fileUriFlow = MutableStateFlow<Uri>(Uri.EMPTY)
    val fileUri = fileUriFlow.asStateFlow()

    fun setFileUri(uri: Uri) {
        fileUriFlow.value = uri
    }

}