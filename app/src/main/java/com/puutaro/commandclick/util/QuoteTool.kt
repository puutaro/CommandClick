package com.puutaro.commandclick.util

object QuoteTool {

    private val cmdClickBackspaceQuote = "cmdClickBackspaceQuote"
    private val backSlachDoubleQuote = "\\\""
    fun trimBothEdgeQuote(
        targetStr: String?,
    ): String {
        return targetStr
            ?.trim()
            .let {
            execTrim(
                it,
                '"'
            )
        }
            .let {
            execTrim(
                it,
                '\''
            ) ?: String()
        }
    }


    private fun execTrim(
        targetStr: String?,
        targetQuote: Char
    ): String {
        if(
            targetStr.isNullOrEmpty()
        ) return String()
        if(
            targetStr.startsWith(targetQuote)
            && targetStr.endsWith(targetQuote)
        ) return targetStr.trim(targetQuote)
        return targetStr
    }

    fun removeDoubleQuoteByIgnoreBackSlash(
        targetStr: String?
    ): String? {
        return targetStr?.replace(
            backSlachDoubleQuote,
            cmdClickBackspaceQuote
        )
            ?.replace(
                "\"",
                ""
            )
            ?.replace(
                cmdClickBackspaceQuote,
                backSlachDoubleQuote
            )
    }
}