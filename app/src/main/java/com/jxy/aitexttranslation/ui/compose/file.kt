package com.jxy.aitexttranslation.ui.compose

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.jxy.aitexttranslation.text.EpubLoader
import com.jxy.aitexttranslation.text.Loader
import com.jxy.aitexttranslation.text.MobiLoader
import com.jxy.aitexttranslation.text.TxtLoader
import java.io.InputStream

const val TXT_TYPE = "text/plain"
const val EPUB_TYPE = "application/epub+zip"
const val MOBI_TYPE = "application/octet-stream"

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ShowToast")
@Composable
fun FileImport(modifier: Modifier) {
    val context = LocalContext.current
    var filename by remember {
        mutableStateOf(TextFieldValue())
    }
//    val selectedFile = remember { mutableStateOf<File?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) {
            Toast.makeText(context, "请选择文件", Toast.LENGTH_SHORT).show()
        } else {
            val name = uri.lastPathSegment
            print(name)
            if (name != null) {
                //获取文件名称
                filename = TextFieldValue(name)
            }
            // 解析文本
            val inputStream = getInputStreamFromUri(context, uri)
            if (inputStream == null) {
                Toast.makeText(context, "文件获取失败", Toast.LENGTH_SHORT).show()
                return@rememberLauncherForActivityResult
            }
            val textLoader = getTextLoader(context.contentResolver.getType(uri))
            val content = textLoader.parse(inputStream)
            println("内容：${content}")
        }
    }

    Row(modifier = modifier) {
//        val (filenameRef, importFileRef) = createRefs()
        TextField(
            value = filename,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.weight(1.0F)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Button(
            onClick = {
                launcher.launch(
                    arrayOf(
                        TXT_TYPE,
                        EPUB_TYPE,
                        MOBI_TYPE
                    )
                )
            }) {
            Text(text = "导入文件")
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
