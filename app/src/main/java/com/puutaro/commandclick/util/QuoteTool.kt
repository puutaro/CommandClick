package com.puutaro.commandclick.util


object QuoteTool {

    private val cmdClickBackspaceQuote = "cmdClickBackspaceQuote"
    private val backSlachDoubleQuote = "\\\""
    private val repSeparatorString = "CMDCLICK_SEPARATOR"
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
        }.let {
            execTrim(
                it,
                '\''
            )
        }.let {
            execTrim(
                it,
                '`'
            )
        }
    }

    fun compBothQuote(
        targetStr: String?
    ): String {
        if(
            targetStr.isNullOrEmpty()
        ) return "``"
        val isBothBackQuote = isBothChar(
            targetStr,
            '`'
        )
        if(
            isBothBackQuote
        ) return targetStr
        val isBothDoubleQuote = isBothChar(
            targetStr,
            '"'
        )
        if(
            isBothDoubleQuote
        ) return targetStr
        val isBothSingleQuote = isBothChar(
            targetStr,
            '\''
        )
        if(
            isBothSingleQuote
        ) return targetStr
        val backSlashDoubleQuoteStr = "\\\\\""
        val backSlashBackQuoteStr = "\\\\`"
        execCompQuote(
            targetStr,
            backSlashDoubleQuoteStr to "\"",
            backSlashBackQuoteStr to "`",
            "multiple backSlash",
        ).let {
            val compStr = it.first
            val isReturn = it.second
            if(!isReturn) return@let
            return compStr
        }
        return execCompQuote(
            targetStr,
            "\"" to "`",
            "`" to "\"",
            "both double & back quote exist",
        ).let {
            val compStrSrc = it.first
            val isReturn = it.second
            val compStr = when(isReturn){
                true -> compStrSrc
                false -> "`${compStrSrc}`"
            }
            compStr
        }
    }

    private fun execCompQuote(
        targetStr: String,
        countStr1ToCompStr: Pair<String, String>,
        countStr2ToCompStr: Pair<String, String>,
        errMessage: String
    ): Pair<String, Boolean>{
        val logSeparator = ": "
        val isCountStr1 = countChar(
            targetStr,
            countStr1ToCompStr.first
        ) > 0
        val isCountStr = countChar(
            targetStr,
            countStr2ToCompStr.first
        ) > 0
        if(
            isCountStr1 && isCountStr
        ){
            LogSystems.stdErrByNoBroad(
                listOf(
                    "comp quote err",
                    errMessage,
                    targetStr,
                ).joinToString(logSeparator)
            )
            return targetStr to true
        }
        return when(true) {
            isCountStr1
            -> compByQuote(
                targetStr,
                countStr1ToCompStr.second
            ) to true
            isCountStr -> compByQuote(
                targetStr,
                countStr2ToCompStr.second
            ) to true
            else -> targetStr to false
        }
    }

    private fun compByQuote(
        targetStr: String,
        compQuote: String,
    ): String {
        return listOf(
            compQuote,
            targetStr,
            compQuote,
        ).joinToString(String())

    }

    fun replaceBySurroundedIgnore(
        targetCon: String,
        targetSeparator: Char,
        wantRepStr: String,
    ): String {
        return surroundSeparatorReplace(
            targetCon,
            targetSeparator,
        ).replace(
            targetSeparator.toString(),
            wantRepStr
        ).let {
            backReplace(
                it,
                targetSeparator.toString()
            )
        }
    }

    fun splitBySurroundedIgnore(
        targetCon: String,
        targetSeparator: Char,
    ): List<String> {
        val targetSeparatorStr = targetSeparator.toString()
        return surroundSeparatorReplace(
            targetCon,
            targetSeparator,
        ).split(
            targetSeparatorStr,
        ).map {
            backReplace(
                it,
                targetSeparator.toString()
            )
        }
    }

    private fun surroundSeparatorReplace(
        targetString: String,
        targetSeparator: Char,
    ): String {

        var quoteType: Char? = null
        return targetString.toList().mapIndexed {
                index, char ->

            val isHitSurroundQuote = char.equals('`')
                    || char.equals('\"')
            if(
                quoteType == null
                && isHitSurroundQuote
            ) {
                quoteType = char
                return@mapIndexed char
            }
            val beforeChar = targetString.getOrNull(index - 1)
            if(
                quoteType != null
                && beforeChar != '\\'
                && char == quoteType
            ) {
                quoteType = null
                return@mapIndexed char
            }
            if(
                quoteType != null
                && char == targetSeparator
            ){
                return@mapIndexed repSeparatorString
            }
            char
        }.joinToString("")
    }

    private fun backReplace(
        replacedCon: String,
        separator: String,
    ): String {
        return replacedCon.replace(
            repSeparatorString,
            separator,
        )

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

    private fun isBothChar(
        targetStr: String?,
        targetQuote: Char
    ): Boolean {
        if(
            targetStr.isNullOrEmpty()
        ) return false
        if(
            targetStr.startsWith(targetQuote)
            && targetStr.endsWith(targetQuote)
        ) return true
        return false
    }

    private fun countCharWithBackSlash(
        targetStr: String,
        charStr: String,
    ): Int {
        val totalSize = countChar(
            targetStr,
            charStr
        )
        val backSlashChar = "\\${charStr}"
        val backSlashTotalSize = countChar(
            targetStr,
            backSlashChar
        )
        return totalSize - backSlashTotalSize
    }

    private fun countChar(
        targetStr: String,
        charStr: String
    ): Int {
        return targetStr.chunked(1).filter {
            it == charStr
        }.size
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