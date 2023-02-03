package com.puutaro.commandclick.util

class UrlTitleTrimmer {
    companion object {
        fun trim(
            urlTitleSource: String
        ): String {
            val titleLimitStrNum = 100
            val urlTitleLength = urlTitleSource.length
            return if(
                urlTitleLength <= titleLimitStrNum
            )  urlTitleSource
            else urlTitleSource
                .substring(0, titleLimitStrNum) + "..."
        }
    }
}