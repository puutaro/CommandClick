package com.puutaro.commandclick.fragment_lib.edit_fragment.common

import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting
import com.puutaro.commandclick.common.variable.fannel.SystemFannel
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.history.fannel_history.FannelHistoryManager
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.state.FannelInfoTool
import java.io.File

object UpdateLastModifiedForAppHistory {
    fun update (
        editExecuteValue: String,
        fannelInfoMap: Map<String, String>
    ){
        val onShortCut = FannelInfoTool.getOnShortcut(
            fannelInfoMap
        )
        if(
            onShortCut ==
            FannelInfoSetting.on_shortcut.defalutStr
        ) return
        if(
            editExecuteValue !=
            SettingVariableSelects.EditExecuteSelects.ALWAYS.name
        ) return
//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
        val fannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        if(
            !SystemFannel.allowIntentSystemFannelList.contains(fannelName)
        ) return
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
//        val isFDialogFannel = FDialogTempFile.howFDialogFile(currentFannelName)
//        if(
//            isFDialogFannel
//        ) return
        FileSystems.updateLastModified(
            File(
                UsePath.cmdclickAppHistoryDirAdminPath,
                FannelHistoryManager.makeAppHistoryFileNameForInit(
//                    currentAppDirPath,
                    currentFannelName
                )
            ).absolutePath
        )
    }
}