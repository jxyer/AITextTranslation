package com.jxy.aitexttranslation.text

import com.jxy.aitexttranslation.ProjectConfig
import com.jxy.aitexttranslation.model.Epub
import com.jxy.aitexttranslation.model.Token
import nl.siegmann.epublib.domain.Book
import nl.siegmann.epublib.domain.TOCReference
import nl.siegmann.epublib.epub.EpubReader
import nl.siegmann.epublib.epub.EpubWriter
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.InputStream


class EpubLoader(filename: String, language: String) : Loader(filename, language) {
    private lateinit var book: Book
    private var textReadStatus = TextReadStatus()

    // used to reconstruct EPUB
    private val readEpubList = arrayListOf<Epub>()

    override suspend fun parse(inputStream: InputStream) {
        // read epub file
        val epubReader = EpubReader()
        book = epubReader.readEpub(inputStream)
    }

    override fun readText(maxWordNumber: Int): String {
        val tocReferences = book.tableOfContents.tocReferences
        val tocReference = tocReferences[textReadStatus.currentChapterIndex]
        val doc = Jsoup.parse(String(tocReference.resource.data))

        return ""
    }

    override fun readText(): String {
        val resultTokens = arrayListOf<Token>()

        val tocReferences = book.tableOfContents.tocReferences
        if (textReadStatus.currentChapterIndex == tocReferences.size) {
            return ""
        }
        var readWordNumber = 0
        val lastChapterIndex = textReadStatus.currentChapterIndex
        while (readWordNumber < ProjectConfig.AI_MAX_TOKEN) {
            if (textReadStatus.currentChapterIndex == tocReferences.size) {
                textReadStatus.currentChapterIndex -= 1
                break
            }
            val tocReference = tocReferences[textReadStatus.currentChapterIndex++]
            val title = tocReference.title
            val doc = Jsoup.parse(String(tocReference.resource.data))
            val elements = doc.allElements
            val elementWithIndex =
                if (lastChapterIndex == textReadStatus.currentChapterIndex) elements.withIndex()
                    .drop(textReadStatus.currentNodeIndex)
                else elements.withIndex()
            var nodeReadWordNumber = 0
            for ((index, element) in elementWithIndex) {
                val text = element.ownText()
                if (text.isEmpty()) continue
                // todo 当一段的内容长度大于MAX_TOKEN时，需要处理
                if (text.length > ProjectConfig.AI_MAX_TOKEN) continue
                readWordNumber += text.length
                nodeReadWordNumber += text.length
                if (readWordNumber >= ProjectConfig.AI_MAX_TOKEN) {
                    textReadStatus.currentNodeIndex = index
                    textReadStatus.currentNodeReadWordNumber = nodeReadWordNumber - text.length
                    readWordNumber -= text.length
                    break
                } else {
                    resultTokens.add(Token(index, text))
                }
            }
        }
        return ""
    }

    override fun newText(text: String) {
        TODO("Not yet implemented")
    }

    // 上一次读取的状态
    private var lastReadStatus: TextReadStatus = TextReadStatus()
    private val writer = EpubWriter()
    fun newText(tokens: List<Token>) {
        if (tokens.isEmpty()) return
        var lastChapterIndex = lastReadStatus.currentChapterIndex
        var lastNodeIndex = lastReadStatus.currentNodeIndex

        val tocReferences = book.tableOfContents.tocReferences
        var preMarkFlag = tokens[0].markFlag
        var doc = getNewChapter(lastChapterIndex, tocReferences)
        var elements = doc.allElements
        tokens.forEach {
            if (preMarkFlag > it.markFlag) {
                println("translated text: $doc")
                // 修改上一张的内容
                tocReferences[lastChapterIndex].resource.data = doc.text().toByteArray()
                // 换章节
                lastChapterIndex++
                doc = getNewChapter(lastChapterIndex, tocReferences)
                elements = doc.allElements
            }
            preMarkFlag = it.markFlag
            val node = elements[it.markFlag]
            node.text(it.text)
        }
        lastReadStatus = textReadStatus
    }

    private fun getNewChapter(chapterIndex: Int, tocReferences: List<TOCReference>): Document {
        val tocReference = tocReferences[chapterIndex]
        return Jsoup.parse(String(tocReference.resource.data))
    }

    override fun close() {
    }
}