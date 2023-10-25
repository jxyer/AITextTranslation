package com.jxy.aitexttranslation.model

data class Epub(var index: Int, var title: String, var tokens: List<Token>)
data class Token(var markFlag: Int, var text: String)
