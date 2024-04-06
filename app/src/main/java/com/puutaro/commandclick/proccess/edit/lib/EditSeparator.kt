package com.puutaro.commandclick.proccess.edit.lib

object EditSeparator {

    private val separatorOrderList = listOf(
        '|', '?', '&'
    )

    fun getNextSeparator(
        currentSeparator: Char
    ): Char? {
        val currentSeparatorIndex =
            separatorOrderList.indexOf(currentSeparator)
        val nextSeparatorIndex = currentSeparatorIndex + 1
        return separatorOrderList.getOrNull(nextSeparatorIndex)
    }
}