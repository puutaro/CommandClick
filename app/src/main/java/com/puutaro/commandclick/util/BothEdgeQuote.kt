package com.puutaro.commandclick.util

class BothEdgeQuote {
    companion object {
        fun trim(
            targetStr: String?,
        ): String {
            return targetStr
                ?.trim(' ')
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
    }
}