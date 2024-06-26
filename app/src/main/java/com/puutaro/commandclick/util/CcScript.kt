package com.puutaro.commandclick.util

import com.puutaro.commandclick.util.str.QuoteTool

object CcScript {

    fun makeKeyValuePairFromSeparatedString(
        keyValueString: String,
        separator: String
    ): Pair<String, String>{
        val keyValueList = keyValueString
            .split(separator)
        val keyValueListSize = keyValueList.size
        if(
            keyValueListSize < 2
        ) return String() to String()
        val parametarKey = keyValueList.first().trim()
        val parameterValue = getValueFromSeparatedList(
            keyValueList,
            separator
        )
        return parametarKey to parameterValue
    }


    private fun getValueFromSeparatedList(
        keyValueList: List<String>,
        separator: String,
    ): String {
        return keyValueList.filterIndexed{
            index, _ -> index >= 1
        }.joinToString(separator).let {
            QuoteTool.trimBothEdgeQuote(it)
        }
    }
}