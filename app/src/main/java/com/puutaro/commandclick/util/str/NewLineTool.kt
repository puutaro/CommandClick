package com.puutaro.commandclick.util.str

object NewLineTool {
    fun replaceMultipleNewlines(input: String): String {
        val result = StringBuilder()
        var previousChar: Char? = null
        for (char in input) {
            if (char != '\n'){
                result.append(char)
                previousChar = char
                continue
            }
            if (previousChar != '\n') {
                result.append(char)
            }
            previousChar = char
        }

        return result.toString()
    }
}