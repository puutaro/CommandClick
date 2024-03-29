package com.puutaro.commandclick.fragment_lib.edit_fragment.common

import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.fannel.SystemFannel
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.history.AppHistoryManager
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.state.SharePrefTool
import java.io.File

object UpdateLastModifiedForAppHistory {
    fun update (
        editExecuteValue: String,
        readSharePreffernceMap: Map<String, String>
    ){
        val onShortCut = SharePrefTool.getOnShortcut(
            readSharePreffernceMap
        )
        if(
            onShortCut ==
            SharePrefferenceSetting.on_shortcut.defalutStr
        ) return
        if(
            editExecuteValue !=
            SettingVariableSelects.EditExecuteSelects.ALWAYS.name
        ) return
        val currentAppDirPath = SharePrefTool.getCurrentAppDirPath(
            readSharePreffernceMap
        )
        val fannelName = SharePrefTool.getCurrentFannelName(
            readSharePreffernceMap
        )
        if(
            currentAppDirPath == UsePath.cmdclickSystemAppDirPath
            && !SystemFannel.allowIntentSystemFannelList.contains(fannelName)
        ) return
        val currentFannelName = SharePrefTool.getCurrentFannelName(
            readSharePreffernceMap
        )
//        val isFDialogFannel = FDialogTempFile.howFDialogFile(currentFannelName)
//        if(
//            isFDialogFannel
//        ) return
        FileSystems.updateLastModified(
            File(
                UsePath.cmdclickAppHistoryDirAdminPath,
                AppHistoryManager.makeAppHistoryFileNameForInit(
                    currentAppDirPath,
                    currentFannelName
                )
            ).absolutePath
        )
    }
}