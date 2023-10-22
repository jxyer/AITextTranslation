package com.jxy.aitexttranslation.text

import nl.siegmann.epublib.epub.EpubReader
import java.io.InputStream


class EpubLoader : Loader {

    override fun parse(inputStream: InputStream): String {
        // read epub file
        val epubReader = EpubReader()
        val book =epubReader.readEpub(inputStream)
        val titles=book.metadata.titles
        titles.forEach {
            println("标题：${it}")
        }
        return "text"
    }
}