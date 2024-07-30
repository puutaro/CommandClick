package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.edit.lib.FilePickerTool
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.EditSettingExtraArgsTool
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.FannelInfoTool

class JsFileOrDirListGetter(
    terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
    private val fannelInfoMap = terminalFragment.fannelInfoMap
    private val currentFannelName = FannelInfoTool.getCurrentFannelName(
        fannelInfoMap
    )
    private val filterMapSeparator = '|'

    @JavascriptInterface
    fun get_S(
        onDirectoryPick: Boolean,
        filterMapCon: String,
    ){
        /*
        ## Description

        Get files or dirs by filej picker

        ## Corresponding macro

        -> [GET_FILES](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#get_files)

        -> [GET_DIRS](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#get_dirs)

        ## onDirectoryPick arg

        | Type        | Description                               |
        |-------------|------------------------------------------|
        | `true` | Pick dir |
        | `false` | Pick file |

        ## filterMapCon arg

        -> [args for GET_FILES macro in toolbar](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#args-for-get_files)

        - Each key-value is separated by `|`

        ## Example

        ```js.js
        run=getFile
            ?func=jsFileOrDirGetter.kt.get_S
            ?args=
                &onDirectoryPick=false
                &filterMapCon=`
                    |suffix=".mp3&m4a"
                    |initialPath="${STORAGE}/Music"
                    |macro=FROM_RECENT_DIR
                    |tag=addByOne
        ```
                `
        */

        val filterMap = CmdClickMap.createMap(
            filterMapCon,
            filterMapSeparator,
        ).toMap()
        val filterFilterPrefixListCon = filterMap.get(
            EditSettingExtraArgsTool.ExtraKey.FILTER_PREFIX.key,
        ) ?: String()
        val filterFilterSuffixListCon = filterMap.get(
            EditSettingExtraArgsTool.ExtraKey.FILTER_SUFFIX.key,
        ) ?: String()
        val tag = filterMap.get(
            EditSettingExtraArgsTool.ExtraKey.TAG.key,
        ) ?: String()
        val pickerMacroStr = filterMap.get(
            EditSettingExtraArgsTool.ExtraKey.MACRO.key,
        )
        val pickerMacro = FilePickerTool.PickerMacro.values().firstOrNull {
            it.name == pickerMacroStr
        }
        val initialPath = FilePickerTool.makeInitialDirPath(
            filterMap,
            currentFannelName,
            pickerMacro,
            tag,
        )
        val filterShellPathCon =
            EditSettingExtraArgsTool.makeShellCon(
                filterMap
            )
        val listener =
            context as? TerminalFragment.OnGetFileListListenerForTerm
                ?: return
        listener.onGetFileListForTerm(
            onDirectoryPick,
            filterFilterPrefixListCon,
            filterFilterSuffixListCon,
            filterShellPathCon,
            initialPath,
            pickerMacro,
            currentFannelName,
            tag,
        )
    }
}