package com.puutaro.commandclick.util.str

object AltRegexTool {

    fun trim(input: String): String {
        var startIndex = 0
        var endIndex = input.length

        // 先頭の空白文字をスキップ
        while (
            startIndex < endIndex
            && input[startIndex].isWhitespace()
        ) {
            startIndex++
        }

        // 末尾の空白文字をスキップ
        while (
            endIndex > startIndex
            && input[endIndex - 1].isWhitespace()
        ) {
            endIndex--
        }

        return input.substring(startIndex, endIndex)
    }
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
        val result = StringBuilder(input.length)

        for (char in input) {
            if (
                charSeq.contains(char)
            ) continue
            result.append(char)
        }
        return result.toString()
    }

    fun consecCharToOne(input: String, targetChar: Char): String {
        val result = StringBuilder(input.length)
        val length = input.length

        for (index in 0 until length) {
            val char = input[index]
            if (char != targetChar) {
                result.append(char)
            } else {
                if (
                    index == 0
                    || input[index - 1] != targetChar
                    ) {
                    result.append(char)
                }
            }
        }
        return result.toString()
    }
//    fun consecCharToOne(
//        input: String,
//        targetChar: Char,
//    ): String {
//        val result = StringBuilder(input.length)
////        var lastCharWasNewline = false
//
//        input.forEachIndexed {
//            index, char ->
//            if (char != targetChar) {
//                result.append(char)
////                lastCharWasNewline = false
//                return@forEachIndexed
//            }
//            if (
//                input.getOrNull(index - 1) ==
//                targetChar
//            ) return@forEachIndexed
//            result.append(char)
//        }
//        return result.toString()
//    }

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

    fun removeSpaceTabCommentAndConsecNewLineAfterNewline(input: String): String {
        val length = input.length
        val result = CharArray(length)
        var writeIndex = 0
        var readIndex = 0
        var lastCharWasNewline = false

        while (readIndex < length) {
            val currentChar = input[readIndex++]

            if (currentChar != '\n') {
                result[writeIndex++] = currentChar
                lastCharWasNewline = false
            } else if (!lastCharWasNewline) {
                result[writeIndex++] = '\n'
                lastCharWasNewline = true

                // Skip spaces, tabs, and comment lines
                while (readIndex < length) {
                    val nextChar = input[readIndex]
                    when(nextChar) {
                        ' ', '　', '\t' -> {
                            readIndex++
                        }
                        '/' -> {
                            if (
                                readIndex + 1 < length
                                && input[readIndex + 1] == '/'
                            ) {
                                readIndex =
                                    input.indexOf(
                                        '\n',
                                        readIndex
                                    ).let {
                                        if (it == -1) length
                                        else it
                                    }
                            } else {
                                break
                            }
                        }
                        else -> break
                    }
//                    if (trimSet.contains(nextChar)) {
//                        readIndex++
//                    } else if (
//                        readIndex + 1 < length
//                        && nextChar == '/'
//                        && input[readIndex + 1] == '/'
//                        ) {
//                        readIndex =
//                            input.indexOf(
//                                '\n',
//                                readIndex
//                            ).let {
//                                if (it == -1) length
//                                else it
//                            }
//                    } else {
//                        break
//                    }
                }
            }
        }

        return String(result, 0, writeIndex)
    }

//    fun removeSpaceTabCommentAndConsecNewLineAfterNewline(input: String): String {
//        val trimSeq = sequenceOf(
//            ' ',
//            '　',
//            '\t',
//        )
//        val result = StringBuilder()
//        var index = 0
//        while (index < input.length) {
//            if (input[index] != '\n') {
//                result.append(input[index])
//                index++
//                continue
//            }
//            if(result.lastOrNull() != '\n') {
//                result.append('\n')
//            }
//            index++
//            while (
//                index < input.length
//                && trimSeq.contains(input[index])
//            ) {
//                index++
//            }
//            if (
//                index + 2 < input.length
//                && input.substring(index, index + 2) == "//"
//            ) {
//                while (
//                    index < input.length
//                    && input[index] != '\n'
//                ) {
//                    index++
//                }
//            }
//        }
//        return result.toString()
//    }

    fun removeSpaceTabAfterNewline(input: String): String {
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