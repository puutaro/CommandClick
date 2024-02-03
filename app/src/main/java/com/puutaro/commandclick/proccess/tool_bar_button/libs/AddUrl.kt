package com.puutaro.commandclick.proccess.tool_bar_button.libs

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.extra_args.ExtraArgsTool
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import com.puutaro.commandclick.util.url.HistoryUrlContents

object AddUrl {
    private const val urlExtraKey = "url"
    private const val onSearchBtnKey = "onSearchBtn"

    private enum class OnSearchBtnValue {
        OFF,
    }

    fun add(
        toolbarButtonArgsMaker: ToolbarButtonArgsMaker,
        settingMenuMapList: List<Map<String, String>?>,
    ){
        val fragment = toolbarButtonArgsMaker.fragment
        if(
            fragment !is EditFragment
        ) return
        val clickConfigMap = toolbarButtonArgsMaker.createClickConfigMap()
        val clickExtraMap = ExtraArgsTool.createExtraMapFromMap(
            clickConfigMap,
            "!"
        )
        val urlStringOrMacro = clickExtraMap.get(urlExtraKey) ?: String()
        val readSharePreferenceMap = fragment.readSharePreferenceMap
        val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreferenceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val urlString = HistoryUrlContents.extract(
            currentAppDirPath,
            urlStringOrMacro
        ) ?: String()
        val onSearchBtn = clickExtraMap.get(onSearchBtnKey) ?: String()
        ExecJsLoad.execExternalJs(
            fragment,
            UsePath.cmdclickSystemAppDirPath,
            UsePath.savePageUrlDialogFannelName,
            listOf(
                urlString,
                onSearchBtn,
            ),
        )
    }
}