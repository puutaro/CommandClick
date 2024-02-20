package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.lib.EditedTextContents
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.FragmentTagPrefix
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import java.io.File

    class ScriptFileSaver(
    private val editFragment: EditFragment,
) {
    private val readSharePreferenceMap = editFragment.readSharePreferenceMap
    private val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreferenceMap,
        SharePrefferenceSetting.current_app_dir
    )
    private val currentFannelName = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreferenceMap,
        SharePrefferenceSetting.current_fannel_name
    )
    fun save(){
        val editedTextContents = EditedTextContents(
            editFragment,
        )
        val currentFannelConList = ReadText(
            File(
                currentAppDirPath,
                currentFannelName
            ).absolutePath
        ).textToList()
        val editedShellContentsList = if(
            editFragment.passCmdVariableEdit
            == CommandClickScriptVariable.PASS_CMDVARIABLE_EDIT_ON_VALUE
            || editFragment.tag?.startsWith(FragmentTagPrefix.Prefix.SETTING_VAL_EDIT_PREFIX.str) == true
        ){
            editedTextContents.updateBySettingVariables(
                currentFannelConList,
                editFragment.recordNumToMapNameValueInSettingHolder,
            )
        } else {
            editedTextContents.updateByCommandVariables(
                currentFannelConList,
                editFragment.recordNumToMapNameValueInCommandHolder,
            )
        }
        editedTextContents.save(
            editedShellContentsList,
        )

    }
}