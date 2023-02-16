package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import android.content.SharedPreferences
import android.widget.Toast
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.SharePreffrenceMethod
import java.io.File


class ValidationSharePreferenceForEdit(
    private val editFragment: EditFragment,
    private val sharePref: SharedPreferences?
) {

    private val appDirCheckErrMessage = "app dire check err in edit validation\npath: %s"
    private val shellFileNameCheckErrMessage = "shell file check err in edit validation\n" +
            "shellName: %s"
    private val api_cmd_variable_edit_api_fragment = editFragment.context?.getString(
        R.string.api_cmd_variable_edit_api_fragment
    )

    fun checkCurrentAppDirPreference(
        checkCurrentAppDirPathSource: String? = null
    ): Boolean {
        val checkCurrentAppDirPath = if(
            checkCurrentAppDirPathSource.isNullOrEmpty()
        ) {
            sharePref?.getString(
                SharePrefferenceSetting.current_app_dir.name,
                SharePrefferenceSetting.current_app_dir.defalutStr
            ) ?: SharePrefferenceSetting.current_app_dir.defalutStr
        } else checkCurrentAppDirPathSource

        if(
            checkCurrentAppDirPath !=
            UsePath.cmdclickAppHistoryDirAdminPath
            && File(checkCurrentAppDirPath).isDirectory
        ) return true
        if(
            editFragment.tag ==
            api_cmd_variable_edit_api_fragment
        ) {
            Toast.makeText(
                editFragment.context,
                appDirCheckErrMessage.format(checkCurrentAppDirPath),
                Toast.LENGTH_LONG
            ).show()
            editFragment.activity?.finish()
            return false
        }
        val cmdclickAppDirPath = UsePath.cmdclickAppDirAdminPath
        val updateDirName = FileSystems.filterSuffixShellFiles(
            cmdclickAppDirPath,
            "on"
        ).firstOrNull()?.removeSuffix(
            CommandClickShellScript.SHELL_FILE_SUFFIX
        ).toString()
        val updateAppDirPath = "${UsePath.cmdclickAppDirPath}/${updateDirName}"
        if(
            !File(updateAppDirPath).isDirectory
        ) {
            Toast.makeText(
                editFragment.context,
                appDirCheckErrMessage.format(checkCurrentAppDirPath),
                Toast.LENGTH_LONG
            ).show()
            val listener = editFragment.context
                    as? EditFragment.OnInitEditFragmentListener
            listener?.onInitEditFragment()
            return false
        }
        SharePreffrenceMethod.putSharePreffrence(
            sharePref,
            mapOf(
                SharePrefferenceSetting.current_app_dir.name
                        to updateAppDirPath
            )
        )
        return true
    }

    fun checkCurrentShellNamePreference(
        checkCurrentAppDirPathSource: String? = null,
        checkCurrentShellNameSource: String? = null
    ): Boolean {
        val on_shortcut = SharePreffrenceMethod.getStringFromSharePreffrence(
            sharePref,
            SharePrefferenceSetting.on_shortcut
        )
        val checkCurrentAppDirPath = if(checkCurrentAppDirPathSource.isNullOrEmpty()) {
            sharePref?.getString(
                SharePrefferenceSetting.current_app_dir.name,
                SharePrefferenceSetting.current_app_dir.defalutStr
            ) ?: SharePrefferenceSetting.current_app_dir.defalutStr
        } else checkCurrentAppDirPathSource

        val checkCurrentShellName = if(checkCurrentShellNameSource.isNullOrEmpty()) {
            sharePref?.getString(
                SharePrefferenceSetting.current_shell_file_name.name,
                SharePrefferenceSetting.current_shell_file_name.defalutStr
            ) ?: SharePrefferenceSetting.current_shell_file_name.defalutStr
        } else  checkCurrentShellNameSource

        if(
            checkCurrentShellName !=
            SharePrefferenceSetting.current_shell_file_name.defalutStr
            && File("${checkCurrentAppDirPath}/${checkCurrentShellName}").isFile
        ) {
            return editExecuteCheck(
                on_shortcut,
                checkCurrentAppDirPath,
                checkCurrentShellName
            )
        }
        if(
            editFragment.tag ==
            api_cmd_variable_edit_api_fragment
        ) {
            Toast.makeText(
                editFragment.context,
                shellFileNameCheckErrMessage.format(checkCurrentShellName),
                Toast.LENGTH_LONG
            ).show()
            editFragment.activity?.finish()
            return false
        }

        val recentShellFileName = FileSystems.filterSuffixShellFiles(
            checkCurrentAppDirPath,
            "on"
        ).firstOrNull().toString()
        if(
            !File("${checkCurrentAppDirPath}/${recentShellFileName}").isFile
        ){
            Toast.makeText(
                editFragment.context,
                shellFileNameCheckErrMessage.format(recentShellFileName),
                Toast.LENGTH_LONG
            ).show()
            val listener = editFragment.context
                    as? EditFragment.OnInitEditFragmentListener
            listener?.onInitEditFragment()
            return false
        }
        val checkOk = editExecuteCheck(
            on_shortcut,
            checkCurrentAppDirPath,
            checkCurrentShellName
        )
        if(!checkOk) return false
        SharePreffrenceMethod.putSharePreffrence(
            sharePref,
            mapOf(
                SharePrefferenceSetting.current_shell_file_name.name
                        to recentShellFileName,

            )
        )
        return true
    }

    private fun editExecuteCheck(
        onShortcut: String,
        checkCurrentAppDirPath: String,
        recentShellFileName: String
    ): Boolean {
        if(
            onShortcut == SharePrefferenceSetting.on_shortcut.defalutStr
            || editFragment.tag ==
               api_cmd_variable_edit_api_fragment
        ) return true
        val readText = ReadText(
            checkCurrentAppDirPath,
            recentShellFileName
        )
        val shellContentsList = readText.textToList()
        val variablesCommandHolderListSize = CommandClickVariables.substituteVariableListFromHolder(
            shellContentsList,
            CommandClickShellScript.CMD_VARIABLE_SECTION_START,
            CommandClickShellScript.CMD_VARIABLE_SECTION_END
        )?.size ?: 0
        if(variablesCommandHolderListSize <= 2){
            Toast.makeText(
                editFragment.context,
                shellFileNameCheckErrMessage.format(recentShellFileName),
                Toast.LENGTH_LONG
            ).show()
            val listener = editFragment.context
                    as? EditFragment.OnInitEditFragmentListener
            listener?.onInitEditFragment()
            return false
        }
        val variablesSettingHolderList = CommandClickVariables.substituteVariableListFromHolder(
            shellContentsList,
            CommandClickShellScript.SETTING_SECTION_START,
            CommandClickShellScript.SETTING_SECTION_END
        )
        val editExecuteValue = CommandClickVariables.substituteCmdClickVariable(
            variablesSettingHolderList,
            CommandClickShellScript.EDIT_EXECUTE
        )
        if(
            editExecuteValue != SettingVariableSelects.Companion.EditExecuteSelects.ALWAYS.name
        ){
            Toast.makeText(
                editFragment.context,
                shellFileNameCheckErrMessage.format(recentShellFileName),
                Toast.LENGTH_LONG
            ).show()
            val listener = editFragment.context
                    as? EditFragment.OnInitEditFragmentListener
            listener?.onInitEditFragment()
            return false
        }
        return true
    }
}
