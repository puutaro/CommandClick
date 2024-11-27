package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import com.puutaro.commandclick.util.file.FileSystems
import java.io.File

object ExitForSetting {
    fun handle(
        methodNameStr: String,
    ): String? {
        FileSystems.updateFile(
            File(UsePath.cmdclickDefaultAppDirPath, "sExitExit.txt").absolutePath,
            listOf(
                "methodNameStr: ${methodNameStr}",
                "exitSignal: ${MethodNameClass.entries.firstOrNull {
                    it.str == methodNameStr
                }}",
            ).joinToString("\n") + "\n\n==========\n\n"
        )
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