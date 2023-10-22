package com.jxy.aitexttranslation.ui.compose

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.jxy.aitexttranslation.text.EpubLoader
import com.jxy.aitexttranslation.text.Loader
import com.jxy.aitexttranslation.text.MobiLoader
import com.jxy.aitexttranslation.text.TxtLoader
import java.io.InputStream

const val TXT_TYPE = "text/plain"
const val EPUB_TYPE = "application/epub+zip"
const val MOBI_TYPE = "application/octet-stream"

@SuppressLint("ShowToast")
@Composable
fun FileImport() {
    val context = LocalContext.current
//    val selectedFile = remember { mutableStateOf<File?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) {
        if (it == null) {
            Toast.makeText(context, "请选择文件", Toast.LENGTH_SHORT).show()
        } else {
            // 解析文本
            val inputStream = getInputStreamFromUri(context, it)
            if (inputStream == null) {
                Toast.makeText(context, "文件获取失败", Toast.LENGTH_SHORT).show()
                return@rememberLauncherForActivityResult
            }
            val textLoader = getTextLoader(context.contentResolver.getType(it))
            val content = textLoader.parse(inputStream)
        }
    }
    Box {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    launcher.launch(
                        arrayOf(
                            TXT_TYPE,
                            EPUB_TYPE,
                            MOBI_TYPE
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(contentColor = MaterialTheme.colorScheme.onPrimary)
            ) {
                Text(
                    text = "选择文件",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

fun getTextLoader(mimeType: String?): Loader {
    var textLoader: Loader? = null
    when {
        mimeType.equals(TXT_TYPE) -> {
            textLoader = TxtLoader()
        }

        mimeType.equals(EPUB_TYPE) -> {
            textLoader = EpubLoader()
        }

        mimeType.equals(MOBI_TYPE) -> {
            textLoader = MobiLoader()
        }
    }
    if (textLoader == null) {
        textLoader = TxtLoader()
    }
    return textLoader
}

fun getInputStreamFromUri(context: Context, uri: Uri): InputStream? {
    val contentResolver = context.contentResolver
    return contentResolver.openInputStream(uri)
}
