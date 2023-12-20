package com.jxy.aitexttranslation

sealed class ProjectConfig {
    // todo token变量
    companion object {
        var SavePath: String = ""
        const val AI_MAX_TOKEN = 100
        lateinit var AI_TOKEN: String
    }
}