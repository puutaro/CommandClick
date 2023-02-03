package com.puutaro.commandclick.fragment_lib.edit_fragment.common

import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.util.AppHistoryManager
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.SharePreffrenceMethod

class UpdateLastModifiedForAppHistory {
    companion object {
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
                SettingVariableSelects.Companion.EditExecuteSelects.ALWAYS.name
            ) return
            FileSystems.updateLastModified(
                UsePath.cmdclickAppHistoryDirAdminPath,
                AppHistoryManager.makeAppHistoryFileNameForInit(
                    SharePreffrenceMethod.getReadSharePreffernceMap(
                        readSharePreffernceMap,
                        SharePrefferenceSetting.current_app_dir
                    ),
                    SharePreffrenceMethod.getReadSharePreffernceMap(
                        readSharePreffernceMap,
                        SharePrefferenceSetting.current_shell_file_name
                    )
                )
            )
        }
    }
}