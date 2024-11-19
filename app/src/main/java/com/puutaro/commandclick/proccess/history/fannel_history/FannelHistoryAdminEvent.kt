package com.puutaro.commandclick.proccess.history.fannel_history

import android.content.Context
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.FannelStateManager
import com.puutaro.commandclick.util.state.FannelInfoTool
import java.io.File

object FannelHistoryAdminEvent {
    fun register(
        context: Context?,
        sharedPref: FannelInfoTool.FannelInfoSharePref?,
        selectedFannelName: String,
        mainFannelSettingConList: List<String>,
        setReplaceVariableMap: Map<String, String>?
    ) {
        val updateEditExecuteValue = CommandClickVariables.substituteCmdClickVariable(
            mainFannelSettingConList,
            CommandClickScriptVariable.EDIT_EXECUTE
        )
        val onEditExecute = updateEditExecuteValue ==
                SettingVariableSelects.EditExecuteSelects.ALWAYS.name
        if (!onEditExecute) {
            FannelInfoTool.putAllFannelInfo(
                sharedPref,
//                selectedAppDirPath,
                FannelInfoSetting.current_fannel_name.defalutStr,
                FannelInfoSetting.on_shortcut.defalutStr,
                FannelInfoSetting.current_fannel_state.defalutStr,
            )
            return
        }
        val selectedAppShellFilePathObj = File(
            UsePath.cmdclickDefaultAppDirPath,
            selectedFannelName
        )
        FileSystems.updateLastModified(
            selectedAppShellFilePathObj.absolutePath
        )
        val onShortCut = if(
            selectedFannelName ==
            CommandClickScriptVariable.EMPTY_STRING
            || selectedFannelName ==
            CommandClickScriptVariable.EMPTY_STRING +
            UsePath.JS_FILE_SUFFIX
        ) {
            FannelInfoSetting.on_shortcut.defalutStr
        } else {
            EditFragmentArgs.Companion.OnShortcutSettingKey.ON.key
        }
        val fannelState = FannelStateManager.getState(
            context,
            selectedFannelName,
            mainFannelSettingConList,
            setReplaceVariableMap,
        )
        FannelInfoTool.putAllFannelInfo(
            sharedPref,
//            selectedAppDirPath,
            selectedFannelName,
            onShortCut,
            fannelState,
        )
        return
    }
}