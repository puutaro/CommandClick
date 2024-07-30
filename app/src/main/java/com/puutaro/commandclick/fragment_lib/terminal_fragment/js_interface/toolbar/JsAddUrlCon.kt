package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.url.HistoryUrlContents

class JsAddUrlCon(
    private val terminalFragment: TerminalFragment
) {

    private val fannelInfoMap =
        terminalFragment.fannelInfoMap
    private val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
        fannelInfoMap
    )
    private val keySeparator = '|'

    @JavascriptInterface
    fun add_S(
        extraMapCon: String,
    ){
        /*
        Add url contents to list in [list index](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md)

        ### Corresponding macro

        -> [ADD_URL_CON](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#add_url_con)

        ### extraMapCon arg

        -> [Args for add url con](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#args-for-add_url_con)

        ### Example

        ```js
        var=runAddUrlCon
            ?func=jsAddUrlCon.add_S
            ?args=
                extraMapCon=`
                    urlStringOrMacro=RECENT
                    |onSearchBtn=ON
                    |urlConSaveParentDirPath=`${listDir}`
                    |compSuffix=".txt"
                `
        ```

        */
        val extraMap = CmdClickMap.createMap(
            extraMapCon,
            keySeparator
        ).toMap()
        val urlStrOrMacro = extraMap.get(
            AddUrlConKey.URL_STRING_OR_MACRO.key
        ) ?: String()
        val urlString = HistoryUrlContents.extract(
            currentAppDirPath,
            urlStrOrMacro
        ) ?: String()
        val onSearchBtn = extraMap.get(
            AddUrlConKey.ON_SEARCH_BTN.key
        ) ?: "-"
        val urlConSaveParentDirPath = extraMap.get(
            AddUrlConKey.URL_CON_SAVE_PARENT_DIR_PATH.key
        ) ?: String()
        val compSuffix = extraMap.get(
            AddUrlConKey.COMP_SUFFIX.key
        ) ?: String()
        val saveUrlHistory = extraMap.get(
            AddUrlConKey.ON_SAVE_URL_HISTORY.key
        ) ?: "-"
        ExecJsLoad.execExternalJs(
            terminalFragment,
            UsePath.cmdclickSystemAppDirPath,
            UsePath.saveWebConDialogFannelName,
            listOf(
                urlString,
                onSearchBtn,
                urlConSaveParentDirPath,
                compSuffix,
                saveUrlHistory,
            ),
        )
    }
}

enum class AddUrlConKey(
    val key: String,
) {
    URL_STRING_OR_MACRO("url"),
    ON_SEARCH_BTN("onSearchBtn"),
    URL_CON_SAVE_PARENT_DIR_PATH("urlConSaveParentDirPath"),
    COMP_SUFFIX("compSuffix"),
    ON_SAVE_URL_HISTORY("onSaveUrlHistory"),
}