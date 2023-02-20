package com.puutaro.commandclick.util

import java.io.File

class JavaScriptLoadUrl {
    companion object {
        fun make (
            execJsPath: String,
        ):String? {
            val jsFileObj = File(execJsPath)
            if(!jsFileObj.isFile) return null
            val recentAppdirPath = jsFileObj.parent
            if(recentAppdirPath.isNullOrEmpty()) return null
            val loadJsUrl = ReadText(
                recentAppdirPath,
                jsFileObj.name
            ).textToList().map {
                val trimJsRow = it
                    .trim(' ')
                    .trim('\t')
                    .trim(' ')
                    .trim('\t')
                if(
                    trimJsRow.startsWith("//")
                ) return@map String()
                trimJsRow
            }.joinToString(" ")
            if(
                loadJsUrl.isEmpty()
                || loadJsUrl.isBlank()
            ) return null
            return "javascript:(function() { ${loadJsUrl} })();"
        }
    }
}