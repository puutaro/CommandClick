package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsMap
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.UrlHistoryAddToTsv
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class JsAddFromUrlHistory(
    terminalFragment: TerminalFragment
) {
    private val activity = terminalFragment.activity
    private val fannelInfoMap = terminalFragment.fannelInfoMap
    private val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
        fannelInfoMap
    )
    private val currentFannelName = FannelInfoTool.getCurrentFannelName(
        fannelInfoMap
    )
    private val currentFannelState = FannelInfoTool.getCurrentStateName(
        fannelInfoMap
    )

    @JavascriptInterface
    fun add_S(
        argsMapCon: String,
        separator: String,
    ){
        /*
        ## Description

        Add url to tsv from selected one in url history recent's 5s

        ## Corresponding macro

        -> [ADD_URL_HISTORY](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#add_url_history)

        ## argsMapCon arg

        | arg | type | description |
        | --------- | --------- | --------- |
        | [Optional] shellPath | string | shell path for filtering url |
        | [Optional] replace vars | arg list | replace var with this args |

        ## separator arg

        separator for argsMapCon

        ## Example without shell path

        ```js.js
        var=runAddFromUrlHistory
            ?func=jsAddFromUrlHistory.add_S
            ?args=
                argsMapCon=""
                &separator="\n"
        ```

        ## Example with shell path

        ```js.js
        var=runAddFromUrlHistory
            ?func=jsAddFromUrlHistory.add_S
            args=
                argsMapCon=`
                    shellPath=${shell path}\n
                    youtube_name=youtuve\n
                    httpPrefix=http\n
                `
                &separator="\n"
        ```

        - `${shell path}` con

        ```sh.sh

        echo "${TARGET_CON}" \
            | ${b} grep "${youtube_name}" \
            | grep ^E "^${httpPrefix}"
        ```

        - `${b}` -> busybox symlink path


        */

        val editFragment = TargetFragmentInstance().getCurrentEditFragmentFromFragment(
            activity,
            currentAppDirPath,
            currentFannelName,
            currentFannelState
        ) ?: return
        val argsMap = JsMap.createMapFromCon(
            argsMapCon,
            separator
        ) ?: emptyMap()
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.Main) {
                UrlHistoryAddToTsv(
                    editFragment,
                    argsMap
                ).invoke()
            }
        }
    }
}