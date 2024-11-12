package com.puutaro.commandclick.proccess.lib


private enum class OutputMark(
    val str: String
){
    NORMAL_OUTPUT_MARK(">>"),
    REFLESH_OUTPUT_MARK(">")
}


private fun trimBothEdgeQuote(
    targetStr: String,
): String {
    val singleQuote = '\''
    val doubleQuote = '"'
    return targetStr.let{
        trimBothEdge(
            it,
            singleQuote
        )
    }.let {
        trimBothEdge(
            it,
            doubleQuote
        )
    }
}

private fun trimBothEdge(
    targetStr: String,
    trimChar: Char
): String {
    val targetStrLength = targetStr.length - 1
    return if(
        targetStr.indexOf(trimChar) == 0
        && targetStr.lastIndexOf(trimChar) == targetStrLength
    ) targetStr.trim(trimChar)
    else targetStr

}

