package com.jxy.aitexttranslation.error

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.CoroutineExceptionHandler

class GlobalExceptionHandler(private val context: Context) {
    val globalExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        println(throwable.message)
        when (throwable) {
            is OpenAIException -> {

            }

            else -> {
                Toast.makeText(
                    context,
                    throwable.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }
}