package com.puutaro.commandclick.proccess

import android.content.SharedPreferences
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.util.FragmentTagManager
import java.io.File

object AppHistoryAdminEvent {
    fun invoke(
        fragment: Fragment,
        sharedPref: SharedPreferences?,
        selectedScriptFileName: String,
    ): Boolean {

        FileSystems.updateLastModified(
            UsePath.cmdclickAppHistoryDirAdminPath,
            selectedScriptFileName
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
            UsePath.cmdclickAppDirAdminPath,
            selectedAppDirName + UsePath.JS_FILE_SUFFIX
        )

        val selectedAppShellFileName = AppHistoryManager
            .getScriptFileNameFromAppHistoryFileName(
                selectedScriptFileName
            )

        val scriptContentsList = ReadText(
            selectedAppDirPath,
            selectedAppShellFileName
        ).textToList()
        val updateEditExecuteValue = CommandClickVariables.returnEditExecuteValueStr(
            scriptContentsList,
            CommandClickVariables.judgeJsOrShellFromSuffix(selectedAppShellFileName)
        )
        val onEditExecute = updateEditExecuteValue ==
                SettingVariableSelects.EditExecuteSelects.ALWAYS.name
        if (!onEditExecute) {
            SharePreffrenceMethod.putSharePreffrence(
                sharedPref,
                mapOf(
                    SharePrefferenceSetting.current_app_dir.name to
                            selectedAppDirPath,
                    SharePrefferenceSetting.current_script_file_name.name to
                            SharePrefferenceSetting.current_script_file_name.defalutStr,
                )
            )
            return true
        }
        if(
            !File(
                selectedAppDirPath,
                selectedAppShellFileName
            ).isFile
        ) {
            Toast.makeText(
                fragment.context,
                "No exist: ${selectedAppDirPath}",
                Toast.LENGTH_LONG
            ).show()
            return false
        }
        FileSystems.updateLastModified(
            selectedAppDirPath,
            selectedAppShellFileName
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
            FragmentTagManager.Suffix.ON.name
        }
        SharePreffrenceMethod.putSharePreffrence(
            sharedPref,
            mapOf(
                SharePrefferenceSetting.current_app_dir.name to
                        selectedAppDirPath,
                SharePrefferenceSetting.current_script_file_name.name to
                        selectedAppShellFileName,
                SharePrefferenceSetting.on_shortcut.name to
                        onShortCut,
            )
        )
        return true
    }
}