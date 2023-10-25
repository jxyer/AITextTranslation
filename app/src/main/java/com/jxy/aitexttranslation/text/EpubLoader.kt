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

    override fun readText(wordNumber: Int): List<Token> {
        val tocReferences = book.tableOfContents.tocReferences
        if (textReadStatus.currentChapterIndex == tocReferences.size) {
            return arrayListOf()
        }
        var readWordNumber = 0
        val resultTokens = arrayListOf<Token>()
        val lastChapterIndex = textReadStatus.currentChapterIndex
        while (readWordNumber < wordNumber) {
            val tocReference = tocReferences[textReadStatus.currentChapterIndex]
            val title = tocReference.title
            // get text content
            val doc = Jsoup.parse(String(tocReference.resource.data))
            var menuReadWordNumber = 0
            val elements = doc.allElements
            // record text location
            val tokens = arrayListOf<Token>()
            val elementWithIndex =
                if (lastChapterIndex == textReadStatus.currentChapterIndex) elements.withIndex()
                    .drop(textReadStatus.currentNodeIndex)
                else elements.withIndex()
            for ((index, element) in elementWithIndex) {
                var text = element.ownText()
                if (text.isEmpty()) continue
                // continue writing from the previous ending position.
                if (lastChapterIndex == textReadStatus.currentChapterIndex && index == textReadStatus.currentNodeIndex) {
                    text = text.substring(textReadStatus.currentNodeReadWordNumber)
                }
                val readTextLength = readWordNumber + text.length
                if (readTextLength >= wordNumber) {
                    val diffWords = text.substring(0, readTextLength - wordNumber)
                    menuReadWordNumber += diffWords.length
                    readWordNumber += diffWords.length
                    textReadStatus.currentNodeIndex = index
                    textReadStatus.currentNodeReadWordNumber = diffWords.length
                    val token = Token(index, diffWords)
                    tokens.add(token)
                    resultTokens.add(token)
                    break
                } else {
                    menuReadWordNumber += text.length
                    readWordNumber += text.length
                    val token = Token(index, text)
                    tokens.add(token)
                    resultTokens.add(token)
                }
            }
            readEpubList.add(
                Epub(
                    index = textReadStatus.currentChapterIndex, title = title, tokens = tokens
                )
            )
            if (readWordNumber < wordNumber) textReadStatus.currentChapterIndex++
        }
        return resultTokens
    }

    override fun newText(tokens: List<Token>) {
        TODO("Not yet implemented")
    }
}