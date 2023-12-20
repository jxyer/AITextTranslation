package com.jxy.aitexttranslation.text

import android.content.Context
import android.net.Uri
import androidx.core.net.toFile
import java.io.InputStream

abstract class Loader(private val filename: String, val language: String) {

    /**
     * text total
     */
    fun total(context: Context, uri: Uri): Long {
        return if (uri.scheme == "file") {
            uri.toFile().length()
        } else {
            val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
                ?: throw Exception("文件没有找到")
            val statSize = parcelFileDescriptor.statSize
            parcelFileDescriptor.close()
            statSize
        }
    }

    abstract suspend fun parse(inputStream: InputStream)

    /**
     * reads the specified number of words from the book.
     */
    abstract fun readText(maxWordNumber: Int): String

    /**
     * 尽量读取ai token最大字数,保存句子完整性
     */
    abstract fun readText(): String

    abstract fun newText(text: String)

    abstract fun close()
}