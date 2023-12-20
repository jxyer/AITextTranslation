package com.jxy.aitexttranslation.text

import java.io.InputStream

class MobiLoader(filename: String, language: String) : Loader(filename, language) {

    override suspend fun parse(inputStream: InputStream) {
        TODO("Not yet implemented")
    }

    override fun readText(maxWordNumber: Int): String {
        TODO("Not yet implemented")
    }

    override fun readText(): String {
        TODO("Not yet implemented")
    }

    override fun newText(text: String) {
        TODO("Not yet implemented")
    }

    override fun close() {
        TODO("Not yet implemented")
    }
}