package com.puutaro.commandclick.proccess.history

import android.content.SharedPreferences
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.FannelStateManager
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import java.io.File

object AppHistoryAdminEvent {
    fun invoke(
        fragment: Fragment,
        sharedPref: SharedPreferences?,
        selectedScriptFileName: String,
    ): Boolean {

        FileSystems.updateLastModified(
            File(
                UsePath.cmdclickAppHistoryDirAdminPath,
                selectedScriptFileName
            ).absolutePath
        )
        val selectedAppDirName = AppHistoryManager.getAppDirNameFromAppHistoryFileName(
            selectedScriptFileName
        )
        val selectedAppDirPath = "${UsePath.cmdclickAppDirPath}/${selectedAppDirName}"
        if(!File(selectedAppDirPath).isDirectory) {
            Toast.makeText(
                fragment.context,
                "No exist: ${selectedAppDirPath}",
                Toast.LENGTH_LONG
            ).show()
            return false
        }
        FileSystems.updateLastModified(
            File(
                UsePath.cmdclickAppDirAdminPath,
            selectedAppDirName + UsePath.JS_FILE_SUFFIX
            ).absolutePath
        )

        val selectedAppShellFileName = AppHistoryManager
            .getScriptFileNameFromAppHistoryFileName(
                selectedScriptFileName
            )

        val scriptContentsList = ReadText(
            File(
                selectedAppDirPath,
                selectedAppShellFileName
            ).absolutePath
        ).textToList()
        val updateEditExecuteValue = CommandClickVariables.returnEditExecuteValueStr(
            scriptContentsList,
            CommandClickVariables.judgeJsOrShellFromSuffix(selectedAppShellFileName)
        )
        val onEditExecute = updateEditExecuteValue ==
                SettingVariableSelects.EditExecuteSelects.ALWAYS.name
        if (!onEditExecute) {
            SharePreferenceMethod.putAllSharePreference(
                sharedPref,
                selectedAppDirPath,
                SharePrefferenceSetting.current_fannel_name.defalutStr,
                SharePrefferenceSetting.on_shortcut.defalutStr,
                SharePrefferenceSetting.fannel_state.defalutStr,
            )
            return true
        }
        val selectedAppShellFilePathObj = File(
            selectedAppDirPath,
            selectedAppShellFileName
        )
        if(
            !selectedAppShellFilePathObj.isFile
        ) {
            Toast.makeText(
                fragment.context,
                "No exist: ${selectedAppDirPath}",
                Toast.LENGTH_LONG
            ).show()
            return false
        }
        FileSystems.updateLastModified(
            selectedAppShellFilePathObj.absolutePath
        )
        val onShortCut = if(
            selectedAppShellFileName ==
            CommandClickScriptVariable.EMPTY_STRING
            || selectedAppShellFileName ==
            CommandClickScriptVariable.EMPTY_STRING +
            UsePath.JS_FILE_SUFFIX
        ) {
            SharePrefferenceSetting.on_shortcut.defalutStr
        } else {
            EditFragmentArgs.Companion.OnShortcutSettingKey.ON.key
        }
        val fannelState = FannelStateManager.getSate(
            selectedAppDirPath,
            selectedAppShellFileName,
        )
        SharePreferenceMethod.putAllSharePreference(
            sharedPref,
            selectedAppDirPath,
            selectedAppShellFileName,
            onShortCut,
            fannelState,
        )
        return true
    }
}