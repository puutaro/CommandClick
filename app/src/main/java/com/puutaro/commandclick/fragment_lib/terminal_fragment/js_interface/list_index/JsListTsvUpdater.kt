package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.list_index

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.tsv.TsvTool


class JsListTsvUpdater(
    terminalFragment: TerminalFragment
) {

    @JavascriptInterface
    fun update(
        settingTsvPath: String,
        updateListIndexMapCon: String,
        separator: String,
    ){
        val updateListIndexMap = CmdClickMap.createMap(
            updateListIndexMapCon,
            separator.firstOrNull() ?: '|'
        ).toMap()
        val updateListIndexTsv = updateListIndexMap.map {
            listOf(
                it.key,
                it.value
            ).joinToString("\t")
        }
        TsvTool.updateTsv(
            settingTsvPath,
            updateListIndexTsv
        )
    }

    @JavascriptInterface
    fun updateTsvByKey(
        tsvPath: String?,
        updateTsvConListCon: String,
    ){
        TsvTool.updateTsvByKey(
            tsvPath,
            updateTsvConListCon.split("\n")
        )
    }
}