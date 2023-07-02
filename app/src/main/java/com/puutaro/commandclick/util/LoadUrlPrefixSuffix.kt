package com.puutaro.commandclick.util

import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.WebUrlVariables

object LoadUrlPrefixSuffix {

    fun judge(
        urlStr: String?
    ): Boolean {
        if(
            urlStr.isNullOrEmpty()
        ) return false
        val enableHtmlSuffix = urlStr.endsWith(
            CommandClickScriptVariable.HTML_FILE_SUFFIX
        )
                || urlStr.endsWith(
            CommandClickScriptVariable.HTM_FILE_SUFFIX
        )
        val enableHtml =
                urlStr.startsWith(
                    WebUrlVariables.slashPrefix
                ) && enableHtmlSuffix
        return urlStr.startsWith(
            WebUrlVariables.filePrefix
        )
                || urlStr.startsWith(
            WebUrlVariables.httpPrefix
        )
                || urlStr.startsWith(
            WebUrlVariables.httpsPrefix
        )
                || urlStr.startsWith(
            WebUrlVariables.jsPrefix
        )
                || enableHtml
    }

    fun judgeTextFile(
        urlStr: String?
    ): Boolean {
        if(
            urlStr.isNullOrEmpty()
        ) return false
        return judgeFilePrefix(urlStr)
                && judgeNoHtmlSuffix(urlStr)
                && judgeNoJsSuffix(urlStr)
    }

    private fun judgeFilePrefix(
        launchUrl: String,
    ): Boolean {
        return launchUrl.startsWith(
            WebUrlVariables.slashPrefix
        ) || launchUrl.startsWith(
            WebUrlVariables.filePrefix
        )
    }


    private fun judgeNoHtmlSuffix(
        launchUrl: String,
    ): Boolean {
        return !launchUrl.endsWith(
            CommandClickScriptVariable.HTML_FILE_SUFFIX
        ) && !launchUrl.endsWith(
            CommandClickScriptVariable.HTM_FILE_SUFFIX
        )
    }

    private fun judgeNoJsSuffix(
        launchUrl: String,
    ): Boolean {
        return !launchUrl.endsWith(
            CommandClickScriptVariable.JS_FILE_SUFFIX
        ) && !launchUrl.endsWith(
            CommandClickScriptVariable.JSX_FILE_SUFFIX
        )
    }
}