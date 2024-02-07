package com.puutaro.commandclick.fragment_lib.edit_fragment

import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.IsCmdEdit
import com.puutaro.commandclick.proccess.edit.lib.ListSettingVariableListMaker
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.util.state.SharePreferenceMethod

object SetVariableTypesSetterForEdit {

    const val setVariabletypeTempConcatStr = "SET_VARIABLE_TYPE_CONCAT_STR"

    fun set(
        editFragment: EditFragment,
        readSharePreferenceMap: Map<String, String>
    ): List<String> {
        val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreferenceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val currentFannelName = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreferenceMap,
            SharePrefferenceSetting.current_fannel_name
        )

        val setVariableTypeListSrc = ListSettingVariableListMaker.make(
            CommandClickScriptVariable.SET_VARIABLE_TYPE,
            readSharePreferenceMap,
            editFragment.setReplaceVariableMap,
            editFragment.currentScriptContentsList,
            editFragment.settingSectionStart,
            editFragment.settingSectionEnd,
        )
        val setVariableForSettingHolder =
            CommandClickScriptVariable.setVariableForSettingHolder
        val isCmdEdit = IsCmdEdit.judge(editFragment)
        return when(isCmdEdit) {
            false -> setVariableForSettingHolder
            else -> setVariableTypeListSrc.let {
                if (
                    it.isEmpty()
                ) return@let setVariableForSettingHolder
                setVariableForSettingHolder + it
            }
        }.joinToString(setVariabletypeTempConcatStr).let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                editFragment.setReplaceVariableMap,
                currentAppDirPath,
                currentFannelName
            )
        }.split(setVariabletypeTempConcatStr)
    }
}