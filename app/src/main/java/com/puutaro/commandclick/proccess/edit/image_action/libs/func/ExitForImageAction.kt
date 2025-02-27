package com.puutaro.commandclick.proccess.edit.image_action.libs.func

import android.graphics.Bitmap
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting

object ExitForImageAction {
    fun handle(
        funcName: String,
        methodNameStr: String,
    ): Pair<
            Pair<
                    Bitmap?,
                    ImageActionKeyManager.BreakSignal?
                    >?,
            FuncCheckerForSetting.FuncCheckErr?
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
            return null to FuncCheckerForSetting.FuncCheckErr("Method name not found: ${spanFuncTypeStr}.${spanMethodNameStr}")
        }
        return Pair(
            null,
            ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
        ) to null
    }

    private enum class MethodNameClass(
        val str: String,
    ){
        EXIT("exit"),
    }

}