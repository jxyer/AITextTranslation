package com.jxy.aitexttranslation.ui.compose

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.jxy.aitexttranslation.ProjectConfig

const val TXT_TYPE = "text/plain"
const val EPUB_TYPE = "application/epub+zip"
const val MOBI_TYPE = "application/octet-stream"

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ShowToast")
@Composable
fun FileImport(modifier: Modifier, setFileUri: (uri: Uri) -> Unit) {
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
            if (name != null) {
                //获取文件名称
                filename = TextFieldValue(name)
            }
            setFileUri(uri)
        }
    }

    Row(modifier = modifier) {
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

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ShowToast")
@Composable
fun FileSave(modifier: Modifier) {
    val context = LocalContext.current
    val fileSavePath = context.getExternalFilesDir("")

    if (fileSavePath != null) {
        ProjectConfig.SavePath = fileSavePath.path
    }
    val filepath by remember {
        mutableStateOf(TextFieldValue(ProjectConfig.SavePath))
    }
    Box(modifier = modifier) {
        TextField(
            value = filepath,
            onValueChange = { },
            readOnly = true,
            label = { Text(text = "文档保存路径") }
        )
    }
}