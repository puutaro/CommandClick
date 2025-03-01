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

    fun findPrefixChars(
        input: String,
        chars: String,
    ): String? {
        if (
            input.isEmpty()
            ) return null

        var endIndex = 0
        while (
            endIndex < input.length
            && chars.contains(input[endIndex])
//            ||input[endIndex] == '?'
//                    || input[endIndex] == '&'
//                    || input[endIndex] == '|'
        ) {
            endIndex++
        }

        if (endIndex > 0) {
            return input.substring(0, endIndex)
        }
        return null
    }

    fun removeSpaceTagAfterNewline(input: String): String {
        val trimSeq = sequenceOf(
            ' ',
            '　',
            '\t',
        )
        val result = StringBuilder()
        var index = 0
        while (index < input.length) {
            if (input[index] != '\n') {
                result.append(input[index])
                index++
                continue
            }
            result.append('\n')
            index++
            while (
                index < input.length
                && trimSeq.contains(input[index])
            ) {
                index++
            }
        }
        return result.toString()
    }


    fun removeCommentLines(input: String): String {
        val result = StringBuilder()
        var index = 0

        while (index < input.length) {
            if (input[index] != '\n'){
                result.append(input[index])
                index++
                continue
            }
            result.append('\n')
            index++
            if (
                index + 2 < input.length
                && input.substring(index, index + 2) == "//"
                ) {
                while (
                    index < input.length
                    && input[index] != '\n'
                ) {
                    index++
                }
            }
        }
        return result.toString()
    }

    fun containsAlphaNumUnderscoreHyphenEquals(input: String): Boolean {
        if (input.isEmpty()) return false

        var index = 0
        while (
            index < input.length
            && (input[index].isAlphanumeric()
                    || input[index] == '_'
                    || input[index] == '-'
                    )
        ) {
            index++
        }

        return index < input.length
                && input[index] == '='
    }

    private fun Char.isAlphanumeric(): Boolean {
        return this in 'A'..'Z' || this in 'a'..'z' || this in '0'..'9'
    }

}