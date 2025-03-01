package com.puutaro.commandclick.util.str

object AltRegexTool {
    fun replacePrefix(
        input: String,
        prefix: String,
        replaceStr: String,
    ): String {
        if (
            !input.startsWith(prefix)
        ) return input
        return replaceStr + input.substring(1)
    }

    fun removeFrom(
        input: String,
        fromStr: String,
    ): String {
        val index = input.indexOf(fromStr)
        if (
            index == -1
        ) return input
        return input.substring(0, index)
    }

    fun removeChars(
        input: String,
        charSeq: String,
    ): String {
        val result = StringBuilder()

        for (char in input) {
            if (
                charSeq.contains(char)
            ) continue
            result.append(char)
        }
        return result.toString()
    }

    fun consecCharToOne(
        input: String,
        targetChar: Char,
    ): String {
        val result = StringBuilder()
//        var lastCharWasNewline = false

        input.forEachIndexed {
            index, char ->
            if (char != targetChar) {
                result.append(char)
//                lastCharWasNewline = false
                return@forEachIndexed
            }
            if (
                input.getOrNull(index - 1) ==
                targetChar
            ) return@forEachIndexed
            result.append(char)
        }
        return result.toString()
    }

}