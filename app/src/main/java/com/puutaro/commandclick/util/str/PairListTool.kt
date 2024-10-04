package com.puutaro.commandclick.util.str


object PairListTool {

    fun getValue(
        pairList: List<Pair<String, String>>?,
        key: String
    ): String? {
        return getPair(
            pairList,
            key
        )?.second?.let {
            QuoteTool.trimBothEdgeQuote(it)
        }
    }

    fun getPair(
        pairList: List<Pair<String, String>>?,
        key: String
    ): Pair<String, String>? {
        if(
            pairList.isNullOrEmpty()
        ) return null
        return pairList.firstOrNull {
            it.first == key
        }
    }
}