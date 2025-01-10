package com.puutaro.commandclick.util.map

object StrToMapListTool {
    fun getValue(
        pairList: List<Pair<String, Map<String, String>>>?,
        key: String
    ): Map<String, String>? {
        return getPair(
            pairList,
            key
        )
    }

    fun getPair(
        pairList: List<Pair<String, Map<String, String>>>?,
        key: String
    ): Map<String, String>? {
        if(
            pairList.isNullOrEmpty()
        ) return null
        return pairList.firstOrNull {
            it.first == key
        }?.second
    }
}