package com.jxy.aitexttranslation.text.translate

import com.jxy.aitexttranslation.Tool
import com.jxy.aitexttranslation.net.GPT
import kotlinx.coroutines.flow.single

class AITranslate(private val targetLanguage: String) : Translate {

    override suspend fun translate(tokens: String): String {
        val json = """
            {
                "model": "gpt-3.5-turbo-1106",
                "messages": [
                    {
                        "role": "system",
                        "content": "You are a translator with vast knowledge of human languages.You need to translate $targetLanguage language.Don't say extra words!"
                    },
                    {
                        "role": "user",
                        "content": "${Tool.formatJsonString(tokens)}"
                    }
                ],
                "temperature": 0,
                "response_format": { "type": "text" }
            }
        """.trimIndent()
        return GPT.completions(json).single()
    }

}