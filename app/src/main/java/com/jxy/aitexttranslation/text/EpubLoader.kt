package com.jxy.aitexttranslation.text

import com.jxy.aitexttranslation.model.Epub
import com.jxy.aitexttranslation.model.Token
import nl.siegmann.epublib.domain.Book
import nl.siegmann.epublib.epub.EpubReader
import org.jsoup.Jsoup
import java.io.InputStream


class EpubLoader : Loader {
    private lateinit var book: Book
    private var textReadStatus = TextReadStatus()
    // used to reconstruct EPUB
    private val readEpubList = arrayListOf<Epub>()
    override fun parse(inputStream: InputStream) {
        // read epub file
        val epubReader = EpubReader()
        book = epubReader.readEpub(inputStream)
    }

    override fun readText(startPosition: Int, endPosition: Int): Any {
        var start = startPosition
        val tocReferences = book.tableOfContents.tocReferences
        var readWordNumber = 0
        val sb = StringBuilder()
        while (startPosition < endPosition) {
            val tocReference = tocReferences[textReadStatus.menuIndex]
            val title = tocReference.title
            // get text content
            val doc = Jsoup.parse(String(tocReference.resource.data))
            var menuReadWordNumber = 0
            val elements = doc.allElements
            val tokens = arrayListOf<Token>()
            for ((index, element) in elements.withIndex()) {
                val text = element.ownText()
                val readTextLength = start + text.length
                if (readTextLength >= endPosition) {
                    val diffWords = text.substring(0, readTextLength - endPosition)
                    sb.append(diffWords)
                    menuReadWordNumber += diffWords.length
                    readWordNumber = menuReadWordNumber
                    start += menuReadWordNumber
                    tokens.add(Token(index, diffWords))
                    break
                } else {
                    sb.append(text)
                    menuReadWordNumber += text.length
                    start += menuReadWordNumber
                }
            }
            readEpubList.add(Epub(index = textReadStatus.menuIndex, tokens))
            textReadStatus.menuIndex++

        }
        val text = sb.substring(textReadStatus.readWordNumber)
        textReadStatus.readWordNumber = readWordNumber
        return text
    }
}