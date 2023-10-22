package com.jxy.aitexttranslation.text

import java.io.InputStream

interface Loader {

    fun parse(inputStream: InputStream): String
}