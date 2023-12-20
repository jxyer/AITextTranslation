package com.jxy.aitexttranslation.error

class OpenAIException(error: String) : Throwable(message = error)