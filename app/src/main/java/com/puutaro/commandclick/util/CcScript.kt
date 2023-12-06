package com.puutaro.commandclick.util

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
        val parametarKey = keyValueList.first()
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
//        val keyValueListSize = keyValueList.size
        return keyValueList.filterIndexed{
            index, _ -> index >= 1
        }.joinToString(separator).let {
            QuoteTool.trimBothEdgeQuote(it)
        }
//        if(
//            keyValueListSize == 2
//        ) return keyValueList
//            .lastOrNull()?.let {
//                QuoteTool.trimBothEdgeQuote(it)
//            } ?: String()
//        return keyValueList
//            .slice(1 until keyValueListSize)
//            .joinToString(separator).let {
//                QuoteTool.trimBothEdgeQuote(it)
//            }
    }
}