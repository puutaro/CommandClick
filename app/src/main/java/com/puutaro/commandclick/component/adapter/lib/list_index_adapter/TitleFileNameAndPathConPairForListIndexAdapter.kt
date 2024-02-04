package com.puutaro.commandclick.component.adapter.lib.list_index_adapter

object TitleFileNameAndPathConPairForListIndexAdapter {
    fun get(
        tsvLine: String
    ): Pair<String, String>? {
        val titleConList = tsvLine.split("\t")
        val fileNameOrTitle = titleConList.firstOrNull()
            ?: return null
        val filePathOrCon = titleConList.getOrNull(1)
            ?: return null
        return fileNameOrTitle to filePathOrCon
    }
}