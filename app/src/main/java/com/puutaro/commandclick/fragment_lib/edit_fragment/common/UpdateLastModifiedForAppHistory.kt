package com.puutaro.commandclick.fragment_lib.edit_fragment.common

import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.fannel.SystemFannel
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.AppHistoryManager
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.SharePreffrenceMethod

object UpdateLastModifiedForAppHistory {
    fun update (
        editExecuteValue: String,
        readSharePreffernceMap: Map<String, String>
    ){
        val onShortCut = SharePreffrenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.on_shortcut
        )
        if(
            onShortCut ==
            SharePrefferenceSetting.on_shortcut.defalutStr
        ) return
        if(
            editExecuteValue !=
            SettingVariableSelects.EditExecuteSelects.ALWAYS.name
        ) return
        val currentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val fannelName = SharePreffrenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_script_file_name
        )
        if(
            currentAppDirPath == UsePath.cmdclickSystemAppDirPath
            && !SystemFannel.allowIntentSystemFannelList.contains(fannelName)
        ) return
        FileSystems.updateLastModified(
            UsePath.cmdclickAppHistoryDirAdminPath,
            AppHistoryManager.makeAppHistoryFileNameForInit(
                currentAppDirPath,
                SharePreffrenceMethod.getReadSharePreffernceMap(
                    readSharePreffernceMap,
                    SharePrefferenceSetting.current_script_file_name
                )
            )
        )
    }
}