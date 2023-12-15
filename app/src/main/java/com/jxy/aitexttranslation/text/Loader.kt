package com.jxy.aitexttranslation.text

import java.io.InputStream

interface Loader {

    /**
     * text total
     */
    fun total(): Int

    suspend fun parse(inputStream: InputStream)

    /**
     * reads the specified number of words from the book.
     */
    fun readText(maxWordNumber: Int): String

    /**
     * 尽量读取ai token最大字数,保存句子完整性
     */
    fun readText(): String

    fun newText(text: String)

    fun writeNewText()
}