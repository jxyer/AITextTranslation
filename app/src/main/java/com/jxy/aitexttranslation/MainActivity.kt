package com.jxy.aitexttranslation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.jxy.aitexttranslation.ui.compose.FileImport
import com.jxy.aitexttranslation.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                // export file
                // config openai key
                // translate text
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AITextTranslate()
                }
            }
        }
    }

    @Composable
    fun AITextTranslate() {
        var openaiKey by remember { mutableStateOf(TextFieldValue()) }
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
            LanguageSelect(modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.height(10.dp))
            FileImport(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = { startTranslating() }) {
                Text(text = "开始翻译")
            }
        }
    }

    private fun startTranslating() {
        TODO("Not yet implemented")
    }

    // 语言列表
    var languages = listOf("中文", "英文", "日语")

    @Composable
    fun LanguageSelect(modifier: Modifier) {
        var translatedLanguage by remember { mutableStateOf(TextFieldValue()) }
        var isExpandedLanguageMenu by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = isExpandedLanguageMenu,
            onExpandedChange = { isExpandedLanguageMenu = it }) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                readOnly = true,
                value = translatedLanguage,
                onValueChange = { },
                label = { Text(text = "翻译语言") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpandedLanguageMenu) },
                colors = TextFieldDefaults.textFieldColors()
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