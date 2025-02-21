package com.puutaro.commandclick.proccess.edit.image_action.libs.func

import android.content.Context
import android.graphics.Bitmap
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.proccess.edit.image_action.libs.ImageActionData
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.enums.EnumEntries

object LoopResultForImageAction {

    suspend fun handle(
        context: Context?,
        funcName: String,
        methodNameStr: String,
        forVarNameBitmapMap: ImageActionData.ForVarNameBitmapMap?,
    ): Pair<
            Pair<
                    Bitmap?,
                    ImageActionKeyManager.BreakSignal?
                    >?,
            FuncCheckerForSetting.FuncCheckErr?
            > {
        if(context == null) return  Pair(Pair(null, null), null)
        val methodNameClass = MethodNameClass.entries.firstOrNull {
            it.str == methodNameStr
        } ?: let {
            val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errBrown,
                funcName
            )
            val spanMethodNameStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                methodNameStr
            )
            return null to FuncCheckerForSetting.FuncCheckErr("Method name not found: func.method: ${spanFuncTypeStr}.${spanMethodNameStr}")
        }
        val args =
            methodNameClass.args
        return when(args){
            is LoopResultMethodArgClass.GetArgs -> {
                val bitmap =
                    forVarNameBitmapMap?.get(
                        ImageActionKeyManager.returnTopAcVarNameMacro
                    )
                Pair(
                   bitmap,
                   null,
                ) to null
            }
        }
    }

    private enum class MethodNameClass(
        val str: String,
        val args: LoopResultMethodArgClass,
    ){
        GET("get", LoopResultMethodArgClass.GetArgs),
    }

    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }

    private sealed class LoopResultMethodArgClass {
        data object GetArgs : LoopResultMethodArgClass()
    }
}