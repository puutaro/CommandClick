package com.puutaro.commandclick.fragment_lib.edit_fragment.processor.lib

import android.content.Context
import android.widget.Toast
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.databinding.EditFragmentBinding
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditTextIdForEdit
import com.puutaro.commandclick.proccess.CommentOutLabelingSection
import com.puutaro.commandclick.proccess.edit.lib.ScriptContentsLister
import com.puutaro.commandclick.util.CommandClickVariables.substituteCmdClickVariable
import com.puutaro.commandclick.util.CommandClickVariables.substituteVariableListFromHolder
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.SharePreffrenceMethod


class EditedTextContents(
    binding: EditFragmentBinding,
    private val editFragment: EditFragment,
    readSharePreffernceMap: Map<String, String>
) {

    private val currentAppDirPath = readSharePreffernceMap.get(
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
        return if(
            recordNumToMapNameValueInCommandHolder.isNullOrEmpty()
            || editFragment.existIndexList
        ) {
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
            currentAppDirPath,
            updateScriptFileName,
            submitScriptContentsList.joinToString("\n")
        )
        if(
            updateScriptFileName.lowercase()
            == currentScriptFileName.lowercase()
        ) {
            judgeAndUpdateWeekAgoLastModify(
                currentScriptFileName
            )
            return
        }
        val sharePref =  editFragment.activity?.getPreferences(Context.MODE_PRIVATE)
        SharePreffrenceMethod.putSharePreffrence(
            sharePref,
            mapOf(
                SharePrefferenceSetting.current_script_file_name.name
                        to updateScriptFileName
            )
        )
        val currentFannelDir = makeFunnelDirPath(
            currentAppDirPath,
            currentScriptFileName
        )
        val updateFannelDir = makeFunnelDirPath(
            currentAppDirPath,
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
            currentAppDirPath,
            currentScriptFileName,
        )
    }

    private fun judgeAndUpdateWeekAgoLastModify(
        scriptFileName: String,
    ){
        if(
            editFragment.onUpdateLastModify
        ) return
        FileSystems.updateWeekPastLastModified(
            currentAppDirPath,
            scriptFileName
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
            CommandClickScriptVariable.SCRIPT_FILE_NAME
        ) ?: currentScriptFileName
        val scriptFileSuffix = when(editFragment.languageType){
            LanguageTypeSelects.SHELL_SCRIPT -> UsePath.SHELL_FILE_SUFFIX
            else -> UsePath.JS_FILE_SUFFIX
        }
        return UsePath.compExtend(
            updateScriptFileNameSource,
            scriptFileSuffix
        )
    }

    private fun makeFunnelDirPath(
        curentAppDirPath: String,
        scriptFileName: String,
    ): String {
        val selectedFannelName =
            scriptFileName
                .removeSuffix(UsePath.JS_FILE_SUFFIX)
                .removeSuffix(UsePath.SHELL_FILE_SUFFIX)
        return curentAppDirPath + "/" + selectedFannelName + UsePath.fannelDirSuffix
    }

}
