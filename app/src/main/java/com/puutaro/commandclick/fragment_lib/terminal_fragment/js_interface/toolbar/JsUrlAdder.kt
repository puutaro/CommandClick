package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.util.url.HistoryUrlContents
import java.lang.ref.WeakReference

class JsUrlAdder(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

    @JavascriptInterface
    fun add_S(
        urlStringOrMacro: String,
        onSearchBtn: String,
    ){
        /*
        ## Description

        Add url to list in [list index](https://github.com/puutaro/CommandClick/blob/master/md/developer/configs/listIndexConfig.md)

        ## Corresponding macro

        -> [ADD_URL](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#add_url)

        ## urlStringOrMacro arg

        -> [Args for add url con](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#args-for-add_url_con)

        ## onSearchBtn arg

        -> [Args for add url con](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#args-for-add_url_con)

        ## Example

        ```js.js
        var=runAddUrl
            ?args=
                urlStringOrMacro="https://www.youtube.com/"
                &onSearchBtn=OFF
        ```

        */

        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val activity = terminalFragment.activity
        val fannelInfoMap = terminalFragment.fannelInfoMap
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val currentFannelState = FannelInfoTool.getCurrentStateName(
            fannelInfoMap
        )

        val urlString = HistoryUrlContents.extract(
//            currentAppDirPath,
            urlStringOrMacro
        ) ?: String()
        val editFragment = TargetFragmentInstance.getCurrentEditFragmentFromFragment(
            activity,
//            currentAppDirPath,
            currentFannelName,
            currentFannelState
        ) ?: return
        ExecJsLoad.execExternalJs(
            editFragment,
//            UsePath.cmdclickDefaultAppDirPath,
            UsePath.savePageUrlDialogFannelName,
            sequenceOf(
                urlString,
                onSearchBtn,
            ),
        )
    }
}