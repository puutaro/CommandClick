package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import android.widget.Toast
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.edit.EditTextSupportViewName
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.TerminalShowByTerminalDo
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.util.state.FragmentTagManager
import java.io.File


class ValidationSharePreferenceForEdit(
    private val editFragment: EditFragment,
) {
    private val context = editFragment.context
    private var shellContentsList: List<String> = emptyList()
    val prefixDirScriptSuffixList = FragmentTagManager.makeListFromTag(
        editFragment.tag as String,
    )
    fun checkCurrentAppDirPreference(
        checkCurrentAppDirPathSource: String? = null
    ): Boolean {
        val checkCurrentAppDirPath = if(
            checkCurrentAppDirPathSource.isNullOrEmpty()
        ) {
            prefixDirScriptSuffixList.get(
                FragmentTagManager.parentAppDirPathIndex
            )
        } else checkCurrentAppDirPathSource
        if(
            checkCurrentAppDirPath == UsePath.cmdclickSystemAppDirPath
        ) return true
//        val cmdclickAppDirAdminPath = UsePath.cmdclickAppDirAdminPath
//        val updateDirName = FileSystems.filterSuffixJsFiles(
//            cmdclickAppDirAdminPath,
//            "on"
//        ).firstOrNull()?.removeSuffix(
//            CommandClickScriptVariable.JS_FILE_SUFFIX
//        ).toString()
//        val updateAppDirPath =
//            "${UsePath.cmdclickAppDirPath}/${updateDirName}"
        if(
            !File(checkCurrentAppDirPath).isDirectory
        ) {
            val listener = context
                    as? EditFragment.OnInitEditFragmentListener
            listener?.onInitEditFragment()
            return false
        }
        return true
    }

    fun checkCurrentShellNamePreference(
        checkCurrentAppDirPathSource: String? = null,
        checkCurrentScriptNameSource: String? = null
    ): Boolean {
        val onShortcut = prefixDirScriptSuffixList.get(
            FragmentTagManager.modeIndex
        )
        val checkCurrentAppDirPath = if(
            checkCurrentAppDirPathSource.isNullOrEmpty()
        ) {
            prefixDirScriptSuffixList.get(
                FragmentTagManager.parentAppDirPathIndex
            )
        } else checkCurrentAppDirPathSource
        val checkCurrentScriptName = if(
            checkCurrentScriptNameSource.isNullOrEmpty()
        ) {
            prefixDirScriptSuffixList.get(
                FragmentTagManager.scriptFileNameIndex
            )
        } else  checkCurrentScriptNameSource
        if(
            checkCurrentScriptName !=
            SharePrefferenceSetting.current_fannel_name.defalutStr
            && File(
                "${checkCurrentAppDirPath}/${checkCurrentScriptName}"
            ).isFile
        ) return editExecuteCheck(
                onShortcut,
                checkCurrentAppDirPath,
                checkCurrentScriptName
            )
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
            onShortcut != FragmentTagManager.Suffix.ON.name
        ) return true
        shellContentsList = ReadText(
            checkCurrentAppDirPath,
            recentShellFileName
        ).textToList()
        val languageType =
            CommandClickVariables.judgeJsOrShellFromSuffix(
                recentShellFileName
            )

        val languageTypeToSectionHolderMap =
            CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP
                .get(languageType)
        val settingSectionStart = languageTypeToSectionHolderMap
            ?.get(
                CommandClickScriptVariable
                    .HolderTypeName.SETTING_SEC_START
        ) as String
        val settingSectionEnd = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable
                .HolderTypeName.SETTING_SEC_END
        ) as String

        val commandSectionStart = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable
                .HolderTypeName.CMD_SEC_START
        ) as String
        val commandSectionEnd = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable
                .HolderTypeName.CMD_SEC_END
        ) as String
        val variablesSettingHolderList =
            CommandClickVariables.substituteVariableListFromHolder(
                shellContentsList,
                settingSectionStart,
                settingSectionEnd
            )
        val onPassCmdVariableEdit = CommandClickVariables.substituteCmdClickVariable(
            variablesSettingHolderList,
            CommandClickScriptVariable.PASS_CMDVARIABLE_EDIT
        ) == CommandClickScriptVariable.PASS_CMDVARIABLE_EDIT_ON_VALUE
        val variablesCommandHolderListSize =
            CommandClickVariables.substituteVariableListFromHolder(
                shellContentsList,
                commandSectionStart,
                commandSectionEnd
            )?.size ?: 0
        if(
            variablesCommandHolderListSize <= 2
            && !onPassCmdVariableEdit
        ){
            val listener = context
                    as? EditFragment.OnInitEditFragmentListener
            listener?.onInitEditFragment()
            return false
        }
        val editExecuteValue = CommandClickVariables.substituteCmdClickVariable(
            variablesSettingHolderList,
            CommandClickScriptVariable.EDIT_EXECUTE
        )
        if(
            editExecuteValue
            != SettingVariableSelects.EditExecuteSelects.ALWAYS.name
        ){
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
