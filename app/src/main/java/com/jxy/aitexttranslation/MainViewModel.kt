package com.jxy.aitexttranslation

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jxy.aitexttranslation.error.GlobalExceptionHandler
import com.jxy.aitexttranslation.text.EpubLoader
import com.jxy.aitexttranslation.text.Loader
import com.jxy.aitexttranslation.text.MobiLoader
import com.jxy.aitexttranslation.text.TxtLoader
import com.jxy.aitexttranslation.text.translate.AITranslate
import com.jxy.aitexttranslation.ui.compose.EPUB_TYPE
import com.jxy.aitexttranslation.ui.compose.MOBI_TYPE
import com.jxy.aitexttranslation.ui.compose.TXT_TYPE
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.InputStream

class MainViewModel : ViewModel() {

    enum class UIState {
        IDLE,
        Translating,
        Translated
    }

    var uiState = mutableStateOf(UIState.IDLE)

    var openaiKey =
        mutableStateOf(TextFieldValue("sk"))
    var sourceLanguage = mutableStateOf("")
    var targetLanguage = mutableStateOf("")
    var fileUri = mutableStateOf(Uri.EMPTY)
    var fileSavePath = mutableStateOf("")

    var curTranslateTextNumber = mutableStateOf(0L)
    var totalTranslateTextNumber = mutableStateOf(1L)

    fun startTranslating(context: Context) {
        val inputStream = getInputStreamFromUri(context, fileUri.value)
        if (inputStream == null) {
            Toast.makeText(context, "文件获取失败", Toast.LENGTH_SHORT).show()
            return
        }
        if (fileUri.value.lastPathSegment == null) {
            Toast.makeText(context, "文件名称获取失败", Toast.LENGTH_SHORT).show()
            return
        }
        val textLoader = getTextLoader(
            fileUri.value.lastPathSegment!!,
            context.contentResolver.getType(fileUri.value)
        )
        viewModelScope.launch(GlobalExceptionHandler {
            println(it.message)
            Toast.makeText(
                context,
                it.message,
                Toast.LENGTH_SHORT
            ).show()
            reset()
        }.globalExceptionHandler) {
            textLoader.parse(inputStream)
            uiState.value = UIState.Translating
            totalTranslateTextNumber.value = textLoader.total(context,fileUri.value)
            val translator = AITranslate(targetLanguage.value)
            do {
                val text = textLoader.readText()
                if (text.isBlank()) {
                    textLoader.close()
                    uiState.value = UIState.Translated
                    break
                }
                val translatedData = translator.translate(text)
                textLoader.newText(translatedData)
                delay(1000)
                curTranslateTextNumber.value += text.length
            } while (true)
        }
    }


    private fun getTextLoader(filename: String, mimeType: String?): Loader {
        var textLoader: Loader? = null
        when {
            mimeType.equals(TXT_TYPE) -> {
                textLoader = TxtLoader(filename, sourceLanguage.value)
            }

            mimeType.equals(EPUB_TYPE) -> {
                textLoader = EpubLoader(filename, sourceLanguage.value)
            }

            mimeType.equals(MOBI_TYPE) -> {
                textLoader = MobiLoader(filename, sourceLanguage.value)
            }
        }
        if (textLoader == null) {
            textLoader = TxtLoader(filename, sourceLanguage.value)
        }
        return textLoader
    }

    private fun getInputStreamFromUri(context: Context, uri: Uri): InputStream? {
        val contentResolver = context.contentResolver
        return contentResolver.openInputStream(uri)
    }

    private fun reset() {
        uiState = mutableStateOf(UIState.IDLE)
        curTranslateTextNumber = mutableStateOf(0L)
        totalTranslateTextNumber = mutableStateOf(1L)
    }

}