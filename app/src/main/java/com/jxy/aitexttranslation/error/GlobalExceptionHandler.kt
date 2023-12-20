package com.jxy.aitexttranslation.error

import kotlinx.coroutines.CoroutineExceptionHandler

class GlobalExceptionHandler(handleError: (throwable: Throwable) -> Unit) {
    val globalExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        handleError(throwable)
    }
}