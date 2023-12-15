package com.jxy.aitexttranslation

sealed class ProjectConfig {
    // todo token变量
    companion object {
        lateinit var SavePath: String
        const val AI_MAX_TOKEN = 5
        lateinit var AI_TOKEN: String
    }
}