package com.puutaro.commandclick.fragment_lib.edit_fragment.processor.lib

import android.content.Context
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.databinding.EditFragmentBinding
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditTextIdForEdit
import com.puutaro.commandclick.proccess.CommentOutLabelingSection
import com.puutaro.commandclick.proccess.edit.lib.ScriptContentsLister
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
    private val currentScriptFileName = readSharePreffernceMap.get(
        SharePrefferenceSetting.current_script_file_name.name
    ) ?: SharePrefferenceSetting.current_script_file_name.defalutStr
    private val scriptContentsLister = ScriptContentsLister(
        binding.editLinearLayout
    )

    fun updateByCommandVariables(
        scriptContentsList: List<String>,
        recordNumToMapNameValueInCommandHolder: Map<Int, Map<String, String>?>? = null,
    ): List<String> {
        return if(recordNumToMapNameValueInCommandHolder.isNullOrEmpty()) {
            scriptContentsList
        } else {
            this.scriptContentsLister.update(
                recordNumToMapNameValueInCommandHolder,
                scriptContentsList,
                EditTextIdForEdit.COMMAND_VARIABLE.id
            )
        }
    }

    fun updateBySettingVariables(
        editedScriptContentsList: List<String>,
        recordNumToMapNameValueInSettingHolder: Map<Int, Map<String, String>?>? = null
    ): List<String> {
        return if (recordNumToMapNameValueInSettingHolder == null) {
            editedScriptContentsList
        } else {
            scriptContentsLister.update(
                recordNumToMapNameValueInSettingHolder,
                editedScriptContentsList,
                EditTextIdForEdit.SETTING_VARIABLE.id
            )
        }
    }

    fun save(
        lastScriptContentsList: List<String>,
    ){
        if(lastScriptContentsList.isEmpty()) return

        val submitScriptContentsList = CommentOutLabelingSection.commentOut(
            lastScriptContentsList,
            currentScriptFileName
        )

        val updateScriptFileName = makeUpdateScriptFileName(
            submitScriptContentsList,
            currentScriptFileName
        )

        FileSystems.writeFile(
            curentAppDirPath,
            updateScriptFileName,
            submitScriptContentsList.joinToString("\n")
        )
        if(
            updateScriptFileName.lowercase() == currentScriptFileName.lowercase()
        ) return
        val sharePref =  editFragment.activity?.getPreferences(Context.MODE_PRIVATE)
        SharePreffrenceMethod.putSharePreffrence(
            sharePref,
            mapOf(
                SharePrefferenceSetting.current_script_file_name.name
                        to updateScriptFileName
            )
        )
        val currentFannelDir = makeFunnelDirPath(
            curentAppDirPath,
            currentScriptFileName
        )
        val updateFannelDir = makeFunnelDirPath(
            curentAppDirPath,
            updateScriptFileName
        )
        FileSystems.copyDirectory(
            currentFannelDir,
            updateFannelDir
        )
        FileSystems.removeDir(
            currentFannelDir
        )
        FileSystems.removeFiles(
            curentAppDirPath,
            currentScriptFileName,
        )
    }

    private fun makeUpdateScriptFileName(
        lastScriptContentsList: List<String>,
        currentScriptFileName: String
    ): String {
        val substituteSettingVariableList = substituteVariableListFromHolder(
            lastScriptContentsList,
            editFragment.settingSectionStart,
            editFragment.settingSectionEnd,
        )
        val updateScriptFileNameSource = substituteCmdClickVariable(
            substituteSettingVariableList,
            CommandClickShellScript.SCRIPT_FILE_NAME
        ) ?: currentScriptFileName
        val scriptFileSuffix = when(editFragment.languageType){
            LanguageTypeSelects.SHELL_SCRIPT -> CommandClickShellScript.SHELL_FILE_SUFFIX
            else -> CommandClickShellScript.JS_FILE_SUFFIX
        }
        return if(
            updateScriptFileNameSource.endsWith(scriptFileSuffix)
        ) {
            updateScriptFileNameSource
        } else {
            updateScriptFileNameSource + scriptFileSuffix
        }
    }

    private fun makeFunnelDirPath(
        curentAppDirPath: String,
        scriptFileName: String,
    ): String {
        val selectedFannelName =
            scriptFileName
                .removeSuffix(CommandClickShellScript.JS_FILE_SUFFIX)
                .removeSuffix(CommandClickShellScript.SHELL_FILE_SUFFIX)
        return curentAppDirPath + "/" + selectedFannelName + UsePath.fannelDirSuffix
    }

}
