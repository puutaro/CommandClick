package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting2

object ExitForSetting {
    fun handle(
        funcName: String,
        methodNameStr: String,
    ): Pair<
            Pair<
                    String?,
                    SettingActionKeyManager.ExitSignal?
                    >?,
            FuncCheckerForSetting2.FuncCheckErr?
            >? {
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "sExitExit.txt").absolutePath,
//            listOf(
//                "methodNameStr: ${methodNameStr}",
//                "exitSignal: ${MethodNameClass.entries.firstOrNull {
//                    it.str == methodNameStr
//                }}",
//            ).joinToString("\n") + "\n\n==========\n\n"
//        )
        MethodNameClass.entries.firstOrNull {
            it.str == methodNameStr
        }  ?: let {
            val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errBrown,
                funcName
            )
            val spanMethodNameStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                methodNameStr
            )
            return null to FuncCheckerForSetting2.FuncCheckErr("Method name not found: ${spanFuncTypeStr}.${spanMethodNameStr}")
        }
        return Pair(
            null,
            SettingActionKeyManager.ExitSignal.EXIT_SIGNAL
        ) to null
    }

    private enum class MethodNameClass(
        val str: String,
    ){
        EXIT("exit"),
    }

}