package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.click.lib

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

class AppHistoryAdminEvent {
    companion object {
        fun invoke(
            fragment: Fragment,
            sharedPref: SharedPreferences?,
            selectedShellFileName: String,
        ): Boolean {

            FileSystems.updateLastModified(
                UsePath.cmdclickAppHistoryDirAdminPath,
                selectedShellFileName
            )
            val selectedAppDirName = AppHistoryManager.getAppDirNameFromAppHistoryFileName(
                selectedShellFileName
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
                    selectedShellFileName
                )

            val shellContentsList = ReadText(
                selectedAppDirPath,
                selectedAppShellFileName
            ).textToList()
            val updateEditExecuteValue = CommandClickVariables.returnEditExecuteValueStr(
                shellContentsList,
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
}