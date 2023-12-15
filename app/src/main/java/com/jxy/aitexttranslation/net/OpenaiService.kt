package com.jxy.aitexttranslation.net

import com.google.gson.JsonObject
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenaiService {
    @POST("chat/completions")
    suspend fun completions(
        @Header("Authorization") key: String,
        @Body requestParam: RequestBody
    ): Response<JsonObject>

    data class RequestParam(
        var model: String = "gpt-3.5-turbo-1106",
        var messages: List<Message>,
        var function_call: FunctionCall = FunctionCall("jxy_function"),
        var functions: List<Function>
    )

    data class Message(var role: String, var content: String)

    data class FunctionCall(var name: String)

    data class Function(
        val name: String,
        val description: String,
        var parameters: Parameter
    )

    data class Parameter(
        var type: String,
        var properties: TranslateSchema,
        var required: List<String>
    )

    data class TranslateListSchema(var tokens: ArrayParameter)
    data class ArrayParameter(var type: String = "array", var items: ItemsProperties)
    data class ItemsProperties(var type: String = "object", var properties: TranslateSchema)

    data class TranslateSchema(
        var markFlag: Field,
        var text: Field
    )

    data class Field(
        var type: String,
        var description: String
    )

}