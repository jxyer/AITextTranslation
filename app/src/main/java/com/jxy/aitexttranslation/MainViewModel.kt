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


    // progress value: min:0 max:1
    var translationProgress = mutableStateOf(0f)
    var openaiKey =
        mutableStateOf(TextFieldValue("sk"))
    var sourceLanguage = mutableStateOf("")
    var targetLanguage = mutableStateOf("")
    var fileUri = mutableStateOf(Uri.EMPTY)
    var fileSavePath = mutableStateOf("")

    var curTranslateProgress = mutableStateOf(0f)
    var totalTranslateProgress = mutableStateOf(0)
    var isReadyTranslate = mutableStateOf(false)

    fun startTranslating(context: Context) {
        val inputStream = getInputStreamFromUri(context, fileUri.value)
        if (inputStream == null) {
            Toast.makeText(context, "文件获取失败", Toast.LENGTH_SHORT).show()
            return
        }
        val textLoader = getTextLoader(context.contentResolver.getType(fileUri.value))
        viewModelScope.launch(GlobalExceptionHandler(context).globalExceptionHandler) {
            textLoader.parse(inputStream)
            totalTranslateProgress.value = textLoader.total()
            isReadyTranslate.value = true
            val onePercent: Int = totalTranslateProgress.value / 100
            val translator = AITranslate(targetLanguage.value)
            do {
                val tokens = textLoader.readText()
                if (tokens.isEmpty()) {
                    break
                }
                val translatedData = translator.translate(tokens)
                textLoader.newText(translatedData)
                delay(1000)
                curTranslateProgress.value += 1
                if ((onePercent % curTranslateProgress.value).toInt() == onePercent) {
                    translationProgress.value += 0.1f
                }
            } while (true)
        }
    }


    private fun getTextLoader(mimeType: String?): Loader {
        var textLoader: Loader? = null
        when {
            mimeType.equals(TXT_TYPE) -> {
                textLoader = TxtLoader(sourceLanguage.value)
            }

            mimeType.equals(EPUB_TYPE) -> {
                textLoader = EpubLoader()
            }

            mimeType.equals(MOBI_TYPE) -> {
                textLoader = MobiLoader()
            }
        }
        if (textLoader == null) {
            textLoader = TxtLoader(sourceLanguage.value)
        }
        return textLoader
    }

    private fun getInputStreamFromUri(context: Context, uri: Uri): InputStream? {
        val contentResolver = context.contentResolver
        return contentResolver.openInputStream(uri)
    }

}