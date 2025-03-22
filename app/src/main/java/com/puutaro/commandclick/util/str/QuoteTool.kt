package com.puutaro.commandclick.util.str

import com.puutaro.commandclick.util.LogSystems


object QuoteTool {

    private val cmdClickBackspaceQuote = "cmdClickBackspaceQuote"
    private const val backSlachDoubleQuote = "\\\""
    private const val repSeparatorString = "CMDCLICK_SEPARATOR"
    private const val layoutSeparator = '廳'

    fun trimBothEdgeQuote(
        targetStr: String?,
    ): String {
        return targetStr?.let {
            AltRegexTool.trim(it)
        }.let {
                val trimCon = execTrim(
                    it,
                '"'
                )
                val isEqual = trimCon == it
                if(
                    !isEqual
                ) return trimCon
                trimCon
        }.let {
            val trimCon = execTrim(
                it,
                '\''
            )
                val isEqual = trimCon == it
                if(
                    !isEqual
                    ) return trimCon
                trimCon
        }.let {
            val trimCon = execTrim(
                it,
                '`'
            )
                val isEqual = trimCon == it
                if(
                    !isEqual
                ) return trimCon
                trimCon
        }
    }

    fun extractBothQuote(
        targetStr: String?,
    ): Char {
        if(
            targetStr.isNullOrEmpty()
        ) return '"'
        val quoteList = sequenceOf('\'', '"', '`',)
        quoteList.forEach {
            if (
                isBothChar(
                    targetStr,
                    it
                )
            ) return it
        }
        return '"'
    }

    fun compBothQuote(
        targetStr: String?,
        primaryQuote: String = "`"
    ): String {
        if(
            targetStr.isNullOrEmpty()
        ) return "${primaryQuote}${primaryQuote}"
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
                false -> "${primaryQuote}${compStrSrc}${primaryQuote}"
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
                sequenceOf(
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
        return sequenceOf(
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
//        val targetConWithCompOneSideQuote = compOneSideQuote(
//            targetCon
//        ) ?: return targetCon
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


    fun layoutSplitBySurroundedIgnore(
        targetCon: String,
    ): List<String> {
        val layoutSeparatorStr = layoutSeparator.toString()
        return surroundLayoutSeparatorReplace(
            targetCon,
        ).let {
            AltRegexTool.consecCharToOne(
                it,
                layoutSeparator,
            )
        }
//            .replace(Regex("[${layoutSeparator}]+"), layoutSeparator.toString())
            .split(
            layoutSeparatorStr,
        ).filter {
            it.isNotEmpty()
        }
    }

    private fun surroundSeparatorReplace(
        targetString: String,
        targetSeparator: Char,
    ): String {
        val result = StringBuilder(targetString.length)
        var quoteType: Char? = null
        val length = targetString.length

        for (index in 0 until length) {
            val char = targetString[index]
            val isHitSurroundQuote = char == '`'
                        || char == '"'
            val beforeChar =
                if (index > 0) targetString[index - 1]
                else null

            if (
                quoteType == null
                && beforeChar != '\\'
                && isHitSurroundQuote
                ) {
                quoteType = char
                result.append(char)
            } else if (
                quoteType != null
                && beforeChar != '\\'
                && char == quoteType
                ) {
                quoteType = null
                result.append(char)
            } else if (
                quoteType != null
                && char == targetSeparator
                ) {
                result.append(repSeparatorString)
            } else {
                result.append(char)
            }
        }
        return result.toString()
    }
//    private fun surroundSeparatorReplace(
//        targetString: String,
//        targetSeparator: Char,
//    ): String {
//
//        var quoteType: Char? = null
//        return targetString.toList().mapIndexed {
//                index, char ->
//
//            val isHitSurroundQuote = char.equals('`')
//                    || char.equals('\"')
//            val beforeChar = targetString.getOrNull(index - 1)
//            if(
//                quoteType == null
//                && beforeChar != '\\'
//                && isHitSurroundQuote
//            ) {
//                quoteType = char
//                return@mapIndexed char
//            }
//            if(
//                quoteType != null
//                && beforeChar != '\\'
//                && char == quoteType
//            ) {
//                quoteType = null
//                return@mapIndexed char
//            }
//            if(
//                quoteType != null
//                && char == targetSeparator
//            ){
//                return@mapIndexed repSeparatorString
//            }
//            char
//        }.joinToString("")
//    }

    private fun surroundLayoutSeparatorReplace(targetString: String): String {
        val targetSeparator = '-'
        val result = StringBuilder(targetString.length)
        var quoteType: Char? = null
        val length = targetString.length

        for (index in 0 until length) {
            val char = targetString[index]
            val isHitSurroundQuote = char == '`'
                    || char == '"'
            val beforeChar =
                if (index > 0) targetString[index - 1]
                else null

            if (
                quoteType == null
                && beforeChar != '\\'
                && isHitSurroundQuote
                ) {
                quoteType = char
                result.append(char)
            } else if (
                quoteType != null
                && beforeChar != '\\'
                && char == quoteType
                ) {
                quoteType = null
                result.append(char)
            } else if (
                quoteType == null
                && char == targetSeparator
                ) {
                result.append(layoutSeparator)
            } else {
                result.append(char)
            }
        }
        return result.toString()
    }
//    private fun surroundLayoutSeparatorReplace(
//        targetString: String,
//    ): String {
//        val targetSeparator = '-'
//        var quoteType: Char? = null
//        return targetString.toList().mapIndexed {
//                index, char ->
//
//            val isHitSurroundQuote = char.equals('`')
//                    || char.equals('\"')
//            val beforeChar = targetString.getOrNull(index - 1)
//            if(
//                quoteType == null
//                && beforeChar != '\\'
//                && isHitSurroundQuote
//            ) {
//                quoteType = char
//                return@mapIndexed char
//            }
//            if(
//                quoteType != null
//                && beforeChar != '\\'
//                && char == quoteType
//            ) {
//                quoteType = null
//                return@mapIndexed char
//            }
//            if(
//                quoteType == null
//                && char == targetSeparator
//            ){
//                return@mapIndexed layoutSeparator
//            }
//            char
//        }.joinToString("")
//    }

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
            targetStr.length == 1
        ) return targetStr
        if(
            targetStr.startsWith(targetQuote)
            && targetStr.endsWith(targetQuote)
        ) return removeBothEdgeChar(
            targetStr,
            targetQuote
        )
//        targetStr.trim(targetQuote)
        return targetStr
    }

    private fun removeBothEdgeChar(targetStr: String, edgeChar: Char): String {
        var endIndex = targetStr.length
        if(
            endIndex == 0
        ) return targetStr
        // 先頭の edgeChar をスキップ
        var startIndex = 0
        if (
           targetStr[0] == edgeChar
        ) {
            startIndex++
        }

        // 末尾の edgeChar をスキップ
        if (
            endIndex > startIndex
            && targetStr[endIndex - 1] == edgeChar
        ) {
            endIndex--
        }

        return targetStr.substring(startIndex, endIndex)
    }

    private fun removeBothEdgeCharBk(
        targetStr: String,
        edgeChar: Char
    ): String {
        val edgeStr = edgeChar.toString()
        return targetStr
            .removePrefix(edgeStr)
            .removeSuffix(edgeStr)
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
        return targetStr?.let {
            SpeedReplacer.replace(
                it,
                sequenceOf(
                    Pair("\"", ""),
                    Pair(cmdClickBackspaceQuote, backSlachDoubleQuote),
                )
            )
        }
//        targetStr?.replace(
//            backSlachDoubleQuote,
//            cmdClickBackspaceQuote
//        )
//            ?.replace(
//                "\"",
//                ""
//            )
//            ?.replace(
//                cmdClickBackspaceQuote,
//                backSlachDoubleQuote
//            )
    }

    fun compOneSideQuote(
        targetStr: String?
    ): String? {
        val trimTargetStr =
            targetStr?.trim()
        if(
            trimTargetStr.isNullOrEmpty()
        ) return trimTargetStr
        val targetQuoteList = sequenceOf(
            '\"',
            '\'',
            '`',
        )
        targetQuoteList.forEach {
            val compStr = execCompOneSideQuote(
                trimTargetStr,
                it
            )
            if(
                compStr != trimTargetStr
            ) return compStr
        }
        return trimTargetStr
    }

    private fun execCompOneSideQuote(
        targetStr: String,
        compQuote: Char
    ): String {
        val isBothQuote = targetStr.startsWith(compQuote)
                && targetStr.endsWith(compQuote)
        if(
            isBothQuote
        ) return targetStr
        if(
            targetStr.startsWith(compQuote)
        ) return "${targetStr}${compQuote}"
        if(
            targetStr.endsWith(compQuote)
        ) return "${compQuote}${targetStr}"
        return targetStr
    }

    fun maskSurroundQuote(targetString: String): String {
        val result = StringBuilder(targetString.length)
        var quoteType: Char? = null
        val length = targetString.length

        for (index in 0 until length) {
            val char = targetString[index]
            val isHitSurroundQuote = char == '`'
                    || char == '"'
            val beforeChar =
                if (index > 0) targetString[index - 1]
                else null

            if (
                quoteType == null
                && isHitSurroundQuote
                && beforeChar != '\\'
                ) {
                quoteType = char
                result.append(char)
            } else if (
                quoteType != null
                && char == quoteType
                && beforeChar != '\\'
                ) {
                quoteType = null
                result.append(char)
            } else if (
                quoteType != null
                ) {
                result.append('a')
            } else {
                result.append(char)
            }
        }
        return result.toString()
    }
//    fun maskSurroundQuote(
//        targetString: String,
//    ): String {
//
//        var quoteType: Char? = null
//        return targetString.toList().mapIndexed {
//                index, char ->
//            val isHitSurroundQuote = char.equals('`')
//                    || char.equals('\"')
//            val beforeChar = targetString.getOrNull(index - 1)
//            if(
//                quoteType == null
//                && isHitSurroundQuote
//                && beforeChar != '\\'
//            ) {
//                quoteType = char
//                return@mapIndexed char
//            }
//
//            if(
//                quoteType != null
//                && char == quoteType
//                && beforeChar != '\\'
//            ) {
//                quoteType = null
//                return@mapIndexed char
//            }
//            if(
//                quoteType != null
//            ){
//                return@mapIndexed 'a'
//            }
//            char
//        }.joinToString("")
//    }
}