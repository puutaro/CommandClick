package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.util.map.CmdClickMap

class JsAddGmailCon(
    private val terminalFragment: TerminalFragment,
) {
    private val keySeparator = '|'

    @JavascriptInterface
    fun add(
        gmailAd: String,
//        urlConSaveParentDirPath: String,
//        compSuffix: String,
        extraMapCon: String,
    ){
        if(
            gmailAd.isEmpty()
        ) return
        val extraMap = CmdClickMap.createMap(
            extraMapCon,
            keySeparator
        ).toMap()
        val urlConSaveParentDirPath = extraMap.get(
            AddGmailConKey.URL_CON_SAVE_PARENT_DIR_PATH.key
        ) ?: String()
        val compSuffix = extraMap.get(
            AddGmailConKey.COMP_SUFFIX.key
        ) ?: String()
        ExecJsLoad.execExternalJs(
            terminalFragment,
            UsePath.cmdclickSystemAppDirPath,
            UsePath.saveGmailConDialogFannelName,
            listOf(
                gmailAd,
                urlConSaveParentDirPath,
                compSuffix,
            ),
        )
    }
}

enum class AddGmailConKey(
    val key: String,
) {
    URL_CON_SAVE_PARENT_DIR_PATH(AddUrlConKey.URL_CON_SAVE_PARENT_DIR_PATH.key),
    COMP_SUFFIX(AddUrlConKey.COMP_SUFFIX.key),
}