package com.jxy.aitexttranslation.text

import com.jxy.aitexttranslation.model.Token
import java.io.InputStream

interface Loader {

    fun parse(inputStream: InputStream)

    /**
     * reads the specified number of words from the book.
     */
    fun readText(wordNumber: Int): List<Token>

    fun newText(tokens: List<Token>)
}