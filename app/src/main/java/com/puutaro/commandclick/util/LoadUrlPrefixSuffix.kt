package com.puutaro.commandclick.util

import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.WebUrlVariables

class LoadUrlPrefixSuffix {
    companion object {
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
    }
}