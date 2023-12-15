package com.jxy.aitexttranslation

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.jxy.aitexttranslation.ui.compose.FileImport
import com.jxy.aitexttranslation.ui.compose.FileSave
import com.jxy.aitexttranslation.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AITextTranslate(viewModel = MainViewModel())
                }
            }
        }
    }

    @Composable
    fun AITextTranslate(viewModel: MainViewModel) {
        val context = LocalContext.current
        var openaiKey by remember { viewModel.openaiKey }
        var fileUri by remember { viewModel.fileUri }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = openaiKey,
                onValueChange = { openaiKey = it },
                label = {
                    Text(text = "OpenAI Key")
                })

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LanguageSelect(
                    modifier = Modifier.weight(1f),
                    name = "原语言"
                ) {
                    viewModel.sourceLanguage.value = it
                }
                Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = null)
                LanguageSelect(
                    modifier = Modifier.weight(1f),
                    name = "翻译语言"
                ) {
                    viewModel.targetLanguage.value = it
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            FileImport(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                setFileUri = {
                    fileUri = it
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
            FileSave(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(10.dp))
            TextField(
                value = viewModel.fileSavePath.value,
                onValueChange = { viewModel.fileSavePath.value = it })
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = {
                if (fileUri == Uri.EMPTY) {
                    Toast.makeText(context, "请选择文件", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                ProjectConfig.AI_TOKEN = openaiKey.text
                viewModel.startTranslating(context)
            }) {
                Text(text = "开始翻译")
            }

            if (viewModel.isReadyTranslate.value) {
                Column {
                    LinearProgressIndicator(
                        progress = viewModel.translationProgress.value,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(text = "当前进度${viewModel.curTranslateProgress.value}/${viewModel.totalTranslateProgress.value}")
                }
            }
        }
    }

    // 语言列表
    private var languages = listOf("中文", "English", "Japanese")

    @Composable
    fun LanguageSelect(
        modifier: Modifier,
        name: String,
        setSelectLanguage: (targetLanguage: String) -> Unit
    ) {
        var translatedLanguage by remember { mutableStateOf(TextFieldValue()) }
        var isExpandedLanguageMenu by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            modifier = modifier,
            expanded = isExpandedLanguageMenu,
            onExpandedChange = { isExpandedLanguageMenu = it }) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                readOnly = true,
                value = translatedLanguage,
                onValueChange = { },
                label = { Text(text = name) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpandedLanguageMenu) },
            )
            ExposedDropdownMenu(expanded = isExpandedLanguageMenu, onDismissRequest = {
                isExpandedLanguageMenu = false
            }) {
                languages.forEach {
                    DropdownMenuItem(
                        text = {
                            Text(text = it)
                        },
                        onClick = {
                            setSelectLanguage(it)
                            translatedLanguage = TextFieldValue(it)
                            isExpandedLanguageMenu = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }

            }
        }
    }

}