package com.puutaro.commandclick.fragment_lib.edit_fragment.processor.lib

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditTextIdForEdit
import com.puutaro.commandclick.proccess.CommentOutLabelingSection
import com.puutaro.commandclick.proccess.edit.lib.ScriptContentsLister
import com.puutaro.commandclick.util.CommandClickVariables.substituteCmdClickVariable
import com.puutaro.commandclick.util.CommandClickVariables.extractValListFromHolder
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.str.ScriptPreWordReplacer
import com.puutaro.commandclick.util.state.FannelStateRooterManager
import java.io.File


object EditedTextContents {

    val settingSectionStart =  CommandClickScriptVariable.SETTING_SEC_START
    val settingSectionEnd =  CommandClickScriptVariable.SETTING_SEC_END

//    fun updateByCommandVariables(
//        editFragment: EditFragment,
//        scriptContentsList: List<String>,
//        recordNumToMapNameValueInCommandHolder: Map<Int, Map<String, String>?>?,
//    ): List<String> {
//        val fannelInfoMap = editFragment.fannelInfoMap
//        val currentScriptFileName = fannelInfoMap.get(
//            FannelInfoSetting.current_fannel_name.name
//        ) ?: FannelInfoSetting.current_fannel_name.defalutStr
//        return if(
//            recordNumToMapNameValueInCommandHolder.isNullOrEmpty()
////            || editFragment.existIndexList
//        ) {
//            scriptContentsList
//        } else {
//            ScriptContentsLister.update(
////                EditComponent.makeEditLinearLayoutList(editFragment),
//                recordNumToMapNameValueInCommandHolder,
//                scriptContentsList,
//                EditTextIdForEdit.COMMAND_VARIABLE.id
//            )
//        }
//    }
//
//    fun updateBySettingVariables(
//        editFragment: EditFragment,
//        editedScriptContentsList: List<String>,
//        recordNumToMapNameValueInSettingHolder: Map<Int, Map<String, String>?>? = null
//    ): List<String> {
//        return if (recordNumToMapNameValueInSettingHolder == null) {
//            editedScriptContentsList
//        } else {
//            ScriptContentsLister.update(
//                CcEditComponent.makeEditLinearLayoutList(editFragment),
//                recordNumToMapNameValueInSettingHolder,
//                editedScriptContentsList,
//                EditTextIdForEdit.SETTING_VARIABLE.id
//            )
//        }
//    }

    fun save(
        editFragment: EditFragment,
        currentScriptFileName: String,
        lastScriptContentsList: List<String>,
        isSettingEdit: Boolean
    ){
        if(
            lastScriptContentsList.isEmpty()
        ) return

        val submitScriptContentsList = CommentOutLabelingSection.commentOut(
            lastScriptContentsList,
//            currentScriptFileName
        )
        val settingFannelPath = when(
            isSettingEdit
        ){
            true -> FannelStateRooterManager.getSettingFannelPath(
                editFragment.fannelInfoMap,
                editFragment.setReplaceVariableMap
            )
            else -> File(
                UsePath.cmdclickDefaultAppDirPath,
                currentScriptFileName,
            ).absolutePath
        }
        FileSystems.writeFile(
            settingFannelPath,
            submitScriptContentsList.joinToString("\n")
        )
        judgeAndUpdateWeekAgoLastModify(
            submitScriptContentsList,
            currentScriptFileName,
        )
    }

    private fun judgeAndUpdateWeekAgoLastModify(
        submitScriptContentsList: List<String>,
        currentScriptFileName: String,
    ){
        if(
            howUpdateLastModify(
                submitScriptContentsList,
                currentScriptFileName,
            )
        ) return
        FileSystems.updateWeekPastLastModified(
            File(
                UsePath.cmdclickDefaultAppDirPath,
                currentScriptFileName
            ).absolutePath
        )
    }

    private fun howUpdateLastModify(
        submitScriptContentsList: List<String>,
        currentScriptFileName: String,
    ): Boolean {
        val settingVariableList = extractValListFromHolder(
            submitScriptContentsList,
            settingSectionStart,
            settingSectionEnd
        )?.joinToString("\n")?.let {
            ScriptPreWordReplacer.replace(
                it,
//                cmdclickDefaultAppDirPath,
                currentScriptFileName,
            )
        }?.split("\n")
        return substituteCmdClickVariable(
                    settingVariableList,
                    CommandClickScriptVariable.ON_UPDATE_LAST_MODIFY
                ) != SettingVariableSelects.OnUpdateLastModifySelects.OFF.name
    }

}
