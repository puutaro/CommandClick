package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.edit.lib.FilePickerTool
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.EditSettingExtraArgsTool
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.SharePrefTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance

class JsFileOrDirListGetter(
    terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
    private val readSharePreferenceMap = terminalFragment.readSharePreferenceMap
    private val currentFannelName = SharePrefTool.getCurrentFannelName(
        readSharePreferenceMap
    )
    private val filterMapSeparator = '|'

    @JavascriptInterface
    fun get_S(
        onDirectoryPick: Boolean,
        filterMapCon: String,
    ){
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