package com.jxy.aitexttranslation

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jxy.aitexttranslation.net.GPT
import kotlinx.coroutines.flow.single

object Tool {

    private const val _flag = "!|"
    private const val t_flag = "!#"
    private const val n_flag = "!$"

    fun formatJsonString(js: String): String {
        return js.replace(" ", _flag)
            .replace("\t", t_flag)
            .replace("\n", n_flag)
            .replace(" ", _flag)
    }

    fun resumeString(js: String): String {
        return js.replace(_flag, " ")
            .replace(t_flag, "\t")
            .replace(n_flag, "\n")
    }

    data class LanguagePunctuationResult(
        var punctuation: List<String> = arrayListOf()
    )

    suspend fun languagePunctuation(language: String): LanguagePunctuationResult {
        val result = GPT.completions(
            """
                {
                    "model": "gpt-3.5-turbo-1106",
                    "messages": [
                        {
                            "role": "user",
                            "content": "你需要列出${language}中一句完整的话后所接的符号。并返回以json格式，json格式包括punctuation数组字段(数组包括了断句的符号)"
                        }
                    ],
                    "temperature": 0,
                    "response_format": { "type": "json_object" }
                }               
                """.trimIndent()
        ).single()
        return Gson().fromJson(
            result,
            TypeToken.get(LanguagePunctuationResult::class.java)
        )
    }

}