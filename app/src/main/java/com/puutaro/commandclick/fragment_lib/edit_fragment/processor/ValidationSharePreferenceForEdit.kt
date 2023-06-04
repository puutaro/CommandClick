package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.material.contentColorFor
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.common.variable.edit.EditTextSupportViewName
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.TerminalShowByTerminalDo
import com.puutaro.commandclick.util.*
import java.io.File


class ValidationSharePreferenceForEdit(
    private val editFragment: EditFragment,
    private val sharePref: SharedPreferences?
) {
    private val context = editFragment.context
    private val appDirCheckErrMessage =
        "app dire check err in edit validation\npath: %s"
    private val shellFileNameCheckErrMessage =
        "shell file check err in edit validation\n" +
            "shellName: %s"
    private val api_cmd_variable_edit_api_fragment =
        editFragment.context?.getString(
        R.string.api_cmd_variable_edit_api_fragment
    )
    private var shellContentsList: List<String> = emptyList()
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
            && File(
                checkCurrentAppDirPath
            ).isDirectory
        ) return true
        if(
            editFragment.tag ==
            api_cmd_variable_edit_api_fragment
        ) {
            Toast.makeText(
                context,
                appDirCheckErrMessage.format(
                    checkCurrentAppDirPath
                ),
                Toast.LENGTH_LONG
            ).show()
            editFragment.activity?.finish()
            return false
        }
        val cmdclickAppDirPath = UsePath.cmdclickAppDirAdminPath
        val updateDirName = FileSystems.filterSuffixJsFiles(
            cmdclickAppDirPath,
            "on"
        ).firstOrNull()?.removeSuffix(
            CommandClickScriptVariable.JS_FILE_SUFFIX
        ).toString()
        val updateAppDirPath =
            "${UsePath.cmdclickAppDirPath}/${updateDirName}"
        if(
            !File(updateAppDirPath).isDirectory
        ) {
            Toast.makeText(
                context,
                appDirCheckErrMessage.format(
                    checkCurrentAppDirPath
                ),
                Toast.LENGTH_LONG
            ).show()
            val listener = context
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
        val onShortcut = SharePreffrenceMethod.getStringFromSharePreffrence(
            sharePref,
            SharePrefferenceSetting.on_shortcut
        )
        val checkCurrentAppDirPath = if(
            checkCurrentAppDirPathSource.isNullOrEmpty()
        ) {
            sharePref?.getString(
                SharePrefferenceSetting.current_app_dir.name,
                SharePrefferenceSetting.current_app_dir.defalutStr
            ) ?: SharePrefferenceSetting.current_app_dir.defalutStr
        } else checkCurrentAppDirPathSource

        val checkCurrentShellName = if(
            checkCurrentShellNameSource.isNullOrEmpty()
        ) {
            sharePref?.getString(
                SharePrefferenceSetting.current_script_file_name.name,
                SharePrefferenceSetting.current_script_file_name.defalutStr
            ) ?: SharePrefferenceSetting.current_script_file_name.defalutStr
        } else  checkCurrentShellNameSource

        if(
            checkCurrentShellName !=
            SharePrefferenceSetting.current_script_file_name.defalutStr
            && File(
                "${checkCurrentAppDirPath}/${checkCurrentShellName}"
            ).isFile
        ) {
            return editExecuteCheck(
                onShortcut,
                checkCurrentAppDirPath,
                checkCurrentShellName
            )
        }
        if(
            editFragment.tag ==
            api_cmd_variable_edit_api_fragment
        ) {
            Toast.makeText(
                context,
                shellFileNameCheckErrMessage.format(
                    checkCurrentShellName
                ),
                Toast.LENGTH_LONG
            ).show()
            editFragment.activity?.finish()
            return false
        }
        val listener = context
                as? EditFragment.OnInitEditFragmentListener
        listener?.onInitEditFragment()
        return false
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
        shellContentsList = ReadText(
            checkCurrentAppDirPath,
            recentShellFileName
        ).textToList()
        val languageType =
            JsOrShellFromSuffix.judge(
                recentShellFileName
            )

        val languageTypeToSectionHolderMap =
            CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP
                .get(languageType)
        val settingSectionStart = languageTypeToSectionHolderMap
            ?.get(
                CommandClickScriptVariable
                    .Companion.HolderTypeName.SETTING_SEC_START
        ) as String
        val settingSectionEnd = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable.Companion
                .HolderTypeName.SETTING_SEC_END
        ) as String

        val commandSectionStart = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable
                .Companion.HolderTypeName.CMD_SEC_START
        ) as String
        val commandSectionEnd = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable
                .Companion.HolderTypeName.CMD_SEC_END
        ) as String

        val variablesCommandHolderListSize =
            CommandClickVariables.substituteVariableListFromHolder(
                shellContentsList,
                commandSectionStart,
                commandSectionEnd
            )?.size ?: 0
        if(variablesCommandHolderListSize <= 2){
            Toast.makeText(
                context,
                shellFileNameCheckErrMessage.format(
                    recentShellFileName
                ),
                Toast.LENGTH_LONG
            ).show()
            val listener = context
                    as? EditFragment.OnInitEditFragmentListener
            listener?.onInitEditFragment()
            return false
        }
        val variablesSettingHolderList =
            CommandClickVariables.substituteVariableListFromHolder(
                shellContentsList,
                settingSectionStart,
                settingSectionEnd
            )
        val editExecuteValue = CommandClickVariables.substituteCmdClickVariable(
            variablesSettingHolderList,
            CommandClickScriptVariable.EDIT_EXECUTE
        )
        if(
            editExecuteValue
            != SettingVariableSelects.Companion.EditExecuteSelects.ALWAYS.name
        ){
            Toast.makeText(
                context,
                shellFileNameCheckErrMessage.format(
                    recentShellFileName
                ),
                Toast.LENGTH_LONG
            ).show()
            val listener = context
                    as? EditFragment.OnInitEditFragmentListener
            listener?.onInitEditFragment()
            return false
        }
        TerminalShowByTerminalDo.show(
            editFragment,
            variablesSettingHolderList
        )
        return true
    }

    fun checkIndexList(): Boolean {
        val indexSize =
            CommandClickVariables.substituteCmdClickVariableList(
                shellContentsList,
                CommandClickScriptVariable.SET_VARIABLE_TYPE
            )?.filter {
                it.contains(
                    ":${EditTextSupportViewName.LIST_INDEX.str}="
                )
            }?.size ?: 1
        if(
            indexSize > 1
        ) {
            Toast.makeText(
                context,
                "list index option must be one",
                Toast.LENGTH_LONG
            ).show()
            val listener = context
                    as? EditFragment.OnInitEditFragmentListener
            listener?.onInitEditFragment()
            return false
        }
        return true
    }

}
