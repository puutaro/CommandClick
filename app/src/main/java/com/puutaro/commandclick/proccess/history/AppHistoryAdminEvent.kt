package com.puutaro.commandclick.proccess.history

import android.content.SharedPreferences
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.FannelStateManager
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import java.io.File

object AppHistoryAdminEvent {
    fun register(
        sharedPref: SharedPreferences?,
        selectedAppDirPath: String,
        selectedFannelName: String,
        mainFannelSettingConList: List<String>
    ) {
        val updateEditExecuteValue = CommandClickVariables.substituteCmdClickVariable(
            mainFannelSettingConList,
            CommandClickScriptVariable.EDIT_EXECUTE
        )
        val onEditExecute = updateEditExecuteValue ==
                SettingVariableSelects.EditExecuteSelects.ALWAYS.name
        if (!onEditExecute) {
            SharePreferenceMethod.putAllSharePreference(
                sharedPref,
                selectedAppDirPath,
                SharePrefferenceSetting.current_fannel_name.defalutStr,
                SharePrefferenceSetting.on_shortcut.defalutStr,
                SharePrefferenceSetting.current_fannel_state.defalutStr,
            )
            return
        }
        val selectedAppShellFilePathObj = File(
            selectedAppDirPath,
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
            SharePrefferenceSetting.on_shortcut.defalutStr
        } else {
            EditFragmentArgs.Companion.OnShortcutSettingKey.ON.key
        }
        val fannelState = FannelStateManager.getState(
            selectedAppDirPath,
            selectedFannelName,
            mainFannelSettingConList
        )
        SharePreferenceMethod.putAllSharePreference(
            sharedPref,
            selectedAppDirPath,
            selectedFannelName,
            onShortCut,
            fannelState,
        )
        return
    }
}