package com.puutaro.commandclick.fragment_lib.edit_fragment

import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.edit.lib.SetVariableTyper
import com.puutaro.commandclick.util.RecordNumToMapNameValueInHolder

object SetVariableTypesSetter {

    val setVariabletypeTempConcatStr = "SET_VARIABLE_TYPE_CONCAT_STR"

    fun set(
        editFragment: EditFragment,
        currentAppDirPath: String,
        currentScriptFileName: String,
    ): List<String> {
        val recordNumToMapNameValueInSettingHolder =
            RecordNumToMapNameValueInHolder.parse(
                editFragment.currentScriptContentsList,
                editFragment.settingSectionStart,
                editFragment.settingSectionEnd,
                true,
            )
        val setVariableTypeListSrc = SetVariableTyper.makeSetVariableTypeList(
            recordNumToMapNameValueInSettingHolder,
            currentAppDirPath,
            currentScriptFileName,
        )
        val setVariableForSettingHolder =
            CommandClickScriptVariable.setVariableForSettingHolder
        return setVariableTypeListSrc.let {
            if(
                it.isNullOrEmpty()
            ) return@let setVariableForSettingHolder
            setVariableForSettingHolder + it
        }.joinToString(setVariabletypeTempConcatStr).let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                editFragment.setReplaceVariableMap,
                currentAppDirPath,
                currentScriptFileName
            )
        }.split(setVariabletypeTempConcatStr)
    }
}