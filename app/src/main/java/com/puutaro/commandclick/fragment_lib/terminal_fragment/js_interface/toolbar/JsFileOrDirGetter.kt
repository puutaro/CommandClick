package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.edit.lib.FilePickerTool
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.EditSettingExtraArgsTool
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance

class JsFileOrDirGetter(
    terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
    private val activity = terminalFragment.activity
    private val fannelInfoMap = terminalFragment.fannelInfoMap
//    private val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//        fannelInfoMap
//    )
    private val currentFannelName = FannelInfoTool.getCurrentFannelName(
        fannelInfoMap
    )
    private val currentFannelState = FannelInfoTool.getCurrentStateName(
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

        Get file or dir by file picker

        ## Corresponding macro

        -> [GET_FILE](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#get_file)

        -> [GET_DIR](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#get_dir)

        ## onDirectoryPick arg

        | Type        | Description                               |
        |-------------|------------------------------------------|
        | `true` | Pick dir |
        | `false` | Pick file |

        ## filterMapCon arg

        -> [args for GET_FILE macro in toolbar](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#args-for-get_file)

        - Each key-value is separated by `|`

        ## Example

        ```js.js
        run=getFile
            ?func=jsFileOrDirGetter.kt.kt.get_S
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
        val editFragment = TargetFragmentInstance().getCurrentEditFragmentFromFragment(
            activity,
//            currentAppDirPath,
            currentFannelName,
            currentFannelState
        ) ?: return
        val parentDirPath =
            when (
                editFragment.existIndexList
            ) {
                true -> ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
                    editFragment,
                    ListIndexForEditAdapter.indexListMap,
                    ListIndexForEditAdapter.listIndexTypeKey
                )
                else -> UsePath.cmdclickDefaultAppDirPath
            }
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
        val filterShellPathCon =
            EditSettingExtraArgsTool.makeShellCon(
                filterMap
            )
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
        val listener =
            context as? TerminalFragment.OnGetFileListenerForTerm
                ?: return
        listener.onGetFileForTerm(
            parentDirPath,
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