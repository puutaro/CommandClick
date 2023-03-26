package com.puutaro.commandclick.fragment_lib.edit_fragment.processor.lib

import android.content.Context
import android.widget.Toast
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.databinding.EditFragmentBinding
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditTextIdForEdit
import com.puutaro.commandclick.proccess.CommentOutLabelingSection
import com.puutaro.commandclick.proccess.edit.lib.ShellContentsLister
import com.puutaro.commandclick.util.CommandClickVariables.Companion.substituteCmdClickVariable
import com.puutaro.commandclick.util.CommandClickVariables.Companion.substituteVariableListFromHolder
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.SharePreffrenceMethod


class EditedTextContents(
    binding: EditFragmentBinding,
    private val editFragment: EditFragment,
    readSharePreffernceMap: Map<String, String>
) {

    private val curentAppDirPath = readSharePreffernceMap.get(
        SharePrefferenceSetting.current_app_dir.name
    ) ?: SharePrefferenceSetting.current_app_dir.defalutStr
    private val currentShellFileName = readSharePreffernceMap.get(
        SharePrefferenceSetting.current_script_file_name.name
    ) ?: SharePrefferenceSetting.current_script_file_name.defalutStr
    private val shellContentsLister = ShellContentsLister(
        binding.editLinearLayout
    )

    fun updateByCommandVariables(
        shellContentsList: List<String>,
        recordNumToMapNameValueInCommandHolder: Map<Int, Map<String, String>?>? = null,
    ): List<String> {
        return if(recordNumToMapNameValueInCommandHolder.isNullOrEmpty()) {
            shellContentsList
        } else {
            this.shellContentsLister.update(
                recordNumToMapNameValueInCommandHolder,
                shellContentsList,
                EditTextIdForEdit.COMMAND_VARIABLE.id
            )
        }
    }

    fun updateBySettingVariables(
        editedShellContentsList: List<String>,
        recordNumToMapNameValueInSettingHolder: Map<Int, Map<String, String>?>? = null
    ): List<String> {
        return if (recordNumToMapNameValueInSettingHolder == null) {
            editedShellContentsList
        } else {
            shellContentsLister.update(
                recordNumToMapNameValueInSettingHolder,
                editedShellContentsList,
                EditTextIdForEdit.SETTING_VARIABLE.id
            )
        }
    }

    fun save(
        lastShellContentsList: List<String>,
    ){
        if(lastShellContentsList.isEmpty()) return

        val submitScriptContentsList = CommentOutLabelingSection.commentOut(
            lastShellContentsList,
            currentShellFileName
        )

        val updateShellFileName = makeUpdateShellFileName(
            submitScriptContentsList,
            currentShellFileName
        )

        FileSystems.writeFile(
            curentAppDirPath,
            updateShellFileName,
            submitScriptContentsList.joinToString("\n")
        )
        if(
            updateShellFileName.lowercase() == currentShellFileName.lowercase()
        ) return
        val sharePref =  editFragment.activity?.getPreferences(Context.MODE_PRIVATE)
        SharePreffrenceMethod.putSharePreffrence(
            sharePref,
            mapOf(
                SharePrefferenceSetting.current_script_file_name.name
                        to updateShellFileName
            )
        )
        FileSystems.removeFiles(
            curentAppDirPath,
            currentShellFileName,
        )
    }

    private fun makeUpdateShellFileName(
        lastShellContentsList: List<String>,
        currentShellFileName: String
    ): String {
        val substituteSettingVariableList = substituteVariableListFromHolder(
            lastShellContentsList,
            editFragment.settingSectionStart,
            editFragment.settingSectionEnd,
        )
        val updateShellFileNameSource = substituteCmdClickVariable(
            substituteSettingVariableList,
            CommandClickShellScript.SCRIPT_FILE_NAME
        ) ?: currentShellFileName
        val scriptFileSuffix = when(editFragment.languageType){
            LanguageTypeSelects.SHELL_SCRIPT -> CommandClickShellScript.SHELL_FILE_SUFFIX
            else -> CommandClickShellScript.JS_FILE_SUFFIX
        }
        return if(
            updateShellFileNameSource.endsWith(scriptFileSuffix)
        ) {
            updateShellFileNameSource
        } else {
            updateShellFileNameSource + scriptFileSuffix
        }
    }

}
