package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import com.puutaro.commandclick.util.url.HistoryUrlContents

class JsAddUrlCon(
    private val terminalFragment: TerminalFragment
) {

    private val activity = terminalFragment.activity
    private val readSharePreferenceMap =
        terminalFragment.readSharePreferenceMap
    private val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreferenceMap,
        SharePrefferenceSetting.current_app_dir
    )

    @JavascriptInterface
    fun add_S(
        urlStringOrMacro: String,
        onSearchBtn: String,
        urlConSaveParentDirPath: String,
        compSuffix: String,
    ){
        val urlString = HistoryUrlContents.extract(
            currentAppDirPath,
            urlStringOrMacro
        ) ?: String()
        ExecJsLoad.execExternalJs(
            terminalFragment,
            UsePath.cmdclickSystemAppDirPath,
            UsePath.saveWebConDialogFannelName,
            listOf(
                urlString,
                onSearchBtn,
                urlConSaveParentDirPath,
                compSuffix
            ),
        )
    }
}