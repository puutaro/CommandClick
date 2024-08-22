package com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.libs.long_press

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.util.SettingVariableReader

object LongPressPathDecider {
    fun decide(
//        currentAppDirPath: String,
        currentFannelNameSrc: String,
        settingVariableList: List<String>?,
        settingValName: String,
        setReplaceVariableMap: Map<String, String>?
    ): String {
        val currentFannelName = currentFannelNameSrc.let {
            val isEmptyFanneName =
                it.isEmpty()
                        || it == CommandClickScriptVariable.EMPTY_STRING
            when(isEmptyFanneName){
                true -> UsePath.cmdclickPreferenceJsName
                else -> it
            }
        }
        val defaultPath = decideFixLongPressFilePath(settingValName)
        return SettingVariableReader.getStrValue(
            settingVariableList,
            settingValName,
            defaultPath,
        ).let {
            val repPath = when(it.isEmpty()){
                true -> defaultPath
                else -> it
            }
            SetReplaceVariabler.execReplaceByReplaceVariables(
                repPath,
                setReplaceVariableMap,
//                currentAppDirPath,
                currentFannelName,
            )
        }
    }
    private fun decideFixLongPressFilePath(
        variableName: String,
    ): String {
        return when (variableName) {
            CommandClickScriptVariable.IMAGE_LONG_PRESS_MENU_FILE_PATH,
            -> UsePath.imageLongPressMenuFilePath
            CommandClickScriptVariable.SRC_ANCHOR_LONG_PRESS_MENU_FILE_PATH
            -> UsePath.srcAnchorLongPressMenuFilePath
            CommandClickScriptVariable.SRC_IMAGE_ANCHOR_LONG_PRESS_MENU_FILE_PATH
            -> UsePath.srcImageAnchorLongPressMenuFilePath
            else -> String()
        }
    }
}