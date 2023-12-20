package com.jxy.aitexttranslation.net

import com.google.gson.Gson
import com.jxy.aitexttranslation.ProjectConfig
import com.jxy.aitexttranslation.error.OpenAIException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.retryWhen
import okhttp3.MediaType
import okhttp3.RequestBody

object GPT {
    data class Error(val error: ErrorResult)
    data class ErrorResult(
        val message: String,
        val type: String,
        val param: String,
        val code: String
    )

    suspend fun completions(json: String): Flow<String> {
        return flow {
            val response = BaseService.openaiService.completions(
                key = "Bearer ${ProjectConfig.AI_TOKEN}",
                requestParam = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"),
                    json
                )
            )
            if (response.isSuccessful) {
                val resultJson = response.body()!!
                    .get("choices").asJsonArray.get(0).asJsonObject.get("message").asJsonObject.get(
                        "content"
                    )
                println(
                    "result: ${resultJson.asString}"
                )
                emit(resultJson.asString)
            } else {
                throw OpenAIException(response.errorBody()!!.string())
            }
        }
            .flowOn(Dispatchers.IO)
            .retryWhen { cause, _ ->
                if (cause is OpenAIException) {
                    val error = Gson().fromJson(cause.message, Error::class.java)
                    if (error.error.code.contentEquals("rate_limit_exceeded")) {
                        delay(1000 * 60)
                        true
                    } else {
                        false
                    }
                } else {
                    false
                }
            }
    }
}