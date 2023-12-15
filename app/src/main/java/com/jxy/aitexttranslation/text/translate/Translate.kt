package com.jxy.aitexttranslation.text.translate

interface Translate {
    suspend fun translate(tokens: String): String

//    data class TranslatedData(var tokens: List<Token>)
}