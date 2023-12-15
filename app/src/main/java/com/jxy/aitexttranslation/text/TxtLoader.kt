package com.jxy.aitexttranslation.text

import com.jxy.aitexttranslation.ProjectConfig
import com.jxy.aitexttranslation.Tool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

class TxtLoader(private val language: String) : Loader {
    private var textReadStatus = TextReadStatus()
    private lateinit var textStreamReader: BufferedReader
    private var textStreamWriter: OutputStreamWriter =
        OutputStreamWriter(
            FileOutputStream(File(ProjectConfig.SavePath + "/aa.txt")),
            StandardCharsets.UTF_8
        )

    private lateinit var languagePunctuation: Tool.LanguagePunctuationResult

    override fun total(): Int {
        return 1000
    }

    override suspend fun parse(inputStream: InputStream) {
        textStreamReader = BufferedReader(withContext(Dispatchers.IO) {
            InputStreamReader(inputStream, "UTF-8")
        })
        languagePunctuation = Tool.languagePunctuation(language)
    }

    override fun readText(maxWordNumber: Int): String {
        var charArray = CharArray(maxWordNumber)
        // 从上一次断句处开始读
        textStreamReader.reset()
        val readNum = textStreamReader.read(
            charArray,
            textReadStatus.currentNodeReadWordNumber,
            maxWordNumber
        )
        if (readNum == -1) return ""
        if (readNum < maxWordNumber) {
            charArray = charArray.copyOf(readNum)
        }
        // 寻找断句符号
        var punctuationIndex = -1
        for (i in charArray.size - 1 downTo 0) {
            if (languagePunctuation.punctuation.contains(charArray[i].toString())) {
                punctuationIndex = i
                break
            }
        }
        // 获取完整句子
        if (punctuationIndex == -1) return String(charArray)
        textStreamReader.mark(punctuationIndex)
        return String(charArray.copyOf(punctuationIndex))
    }

    override fun readText(): String {
        var charArray = CharArray(ProjectConfig.AI_MAX_TOKEN)
        val readNum = textStreamReader.read(
            charArray,
            0,
            ProjectConfig.AI_MAX_TOKEN
        )
        if (readNum == -1) return ""
        if (readNum < ProjectConfig.AI_MAX_TOKEN) {
            charArray = charArray.copyOf(readNum)
        }
        textReadStatus.currentNodeReadWordNumber += charArray.size
        return String(charArray)
    }

    override fun newText(text: String) {
        textStreamWriter.write(text)
    }


    override fun writeNewText() {
        TODO("Not yet implemented")
    }
}