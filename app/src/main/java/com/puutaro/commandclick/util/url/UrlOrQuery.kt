package com.puutaro.commandclick.util.url

import com.puutaro.commandclick.util.LoadUrlPrefixSuffix

object UrlOrQuery {
    fun convert(urlSrcStr: String?): String? {
        if(
            urlSrcStr.isNullOrEmpty()
        ) return null
        if (
            LoadUrlPrefixSuffix.judge(urlSrcStr)
        ) return urlSrcStr
        return "${WebUrlVariables.queryUrl}${urlSrcStr}"
    }
}