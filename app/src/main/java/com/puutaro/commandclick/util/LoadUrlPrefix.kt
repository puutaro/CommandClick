package com.puutaro.commandclick.util

import com.puutaro.commandclick.common.variable.WebUrlVariables

class LoadUrlPrefix {
    companion object {
        fun judge(
            urlStr: String?
        ): Boolean {
            if(
                urlStr.isNullOrEmpty()
            ) return false
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
        }
    }
}