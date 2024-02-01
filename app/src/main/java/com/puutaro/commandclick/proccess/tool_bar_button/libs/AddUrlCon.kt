package com.puutaro.commandclick.proccess.tool_bar_button.libs

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.extra_args.ExtraArgsTool
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import com.puutaro.commandclick.util.url.HistoryUrlContents

object AddUrlCon {

    private const val urlExtraKey = "url"

    fun add(
        toolbarButtonArgsMaker: ToolbarButtonArgsMaker,
        settingMenuMapList: List<Map<String, String>?>,
    ){
        val fragment = toolbarButtonArgsMaker.fragment
        if(
            fragment !is EditFragment
        ) return
        val clickConfigMap = toolbarButtonArgsMaker.createClickConfigMap()
        val urlStringOrMacro = ExtraArgsTool.createExtraMapFromMap(
            clickConfigMap,
            "!"
        ).get(urlExtraKey)
            ?: String()
        val readSharePreferenceMap = fragment.readSharePreferenceMap
        val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreferenceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val urlString = HistoryUrlContents.extract(
            currentAppDirPath,
            urlStringOrMacro
        ) ?: String()
        ExecJsLoad.execExternalJs(
            fragment,
            UsePath.cmdclickSystemAppDirPath,
            UsePath.saveWebConDialogFannelName,
            listOf(
                urlString,
                toolbarButtonArgsMaker.toolbarButtonBariantForEdit.str,
                toolbarButtonArgsMaker.decideClickKey()
            ),
        )
    }
}