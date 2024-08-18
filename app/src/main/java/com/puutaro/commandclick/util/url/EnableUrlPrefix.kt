package com.puutaro.commandclick.util.url

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