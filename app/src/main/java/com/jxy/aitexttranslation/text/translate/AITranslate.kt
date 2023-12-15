package com.jxy.aitexttranslation.text.translate

import com.jxy.aitexttranslation.ProjectConfig
import com.jxy.aitexttranslation.Tool
import com.jxy.aitexttranslation.net.BaseService
import okhttp3.MediaType
import okhttp3.RequestBody

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
        val response = BaseService.openaiService.completions(
            key = "Bearer ${ProjectConfig.AI_TOKEN}",
            requestParam = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                json
            )
        )
        if (response.isSuccessful) {
            val resultJson = response.body()!!
                .get("choices").asJsonArray.get(0).asJsonObject.get("message").asJsonObject.get("content")
            println(
                "translated:$resultJson"
            )
            return resultJson.toString()
        } else {
            throw Exception("response:" + response.errorBody()!!.string())
        }
    }

}