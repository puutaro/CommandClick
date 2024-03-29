package com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess

import com.puutaro.commandclick.common.variable.variables.WebUrlVariables

object EnableUrlPrefix {
    fun isHttpOrFilePrefix(
        url: String?
    ): Boolean {
        if(url.isNullOrEmpty()) return false
        return url.startsWith(WebUrlVariables.httpPrefix)
                || url.startsWith(WebUrlVariables.httpsPrefix)
                || url.startsWith(WebUrlVariables.filePrefix)
    }


    fun isHttpPrefix(
        url: String?
    ): Boolean {
        if(url.isNullOrEmpty()) return false
        return url.startsWith(WebUrlVariables.httpsPrefix)
                || url.startsWith(WebUrlVariables.httpPrefix)
    }
}