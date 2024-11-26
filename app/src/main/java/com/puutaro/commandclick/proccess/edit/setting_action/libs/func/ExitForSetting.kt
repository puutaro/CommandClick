package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager

object ExitForSetting {
    fun handle(
        methodNameStr: String,
    ): String? {
        MethodNameClass.entries.firstOrNull {
            it.str == methodNameStr
        } ?: return null
        return SettingActionKeyManager.CommandMacro.EXIT_SIGNAL.name
    }

    private enum class MethodNameClass(
        val str: String,
    ){
        EXIT("exit"),
    }

}