package com.puutaro.commandclick.proccess.edit.image_action.libs.func

import android.content.Context
import android.graphics.Bitmap
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.enums.EnumEntries

object DelayForImageAction {

    suspend fun handle(
        context: Context?,
        funcName: String,
        methodNameStr: String,
        argsPairList: List<Pair<String, String>>,
        varNameToBitmapMap: Map<String, Bitmap?>?,
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
            is DelayMethodArgClass.MillArgs -> {
                val formalArgIndexToNameToTypeList = args.entries.mapIndexed {
                        index, formalArgsNameToType ->
                    Triple(
                        index,
                        formalArgsNameToType.key,
                        formalArgsNameToType.type,
                    )
                }
                val mapArgMapList = FuncCheckerForSetting.MapArg.makeMapArgMapListByIndex(
                    formalArgIndexToNameToTypeList,
                    argsPairList
                )
                val where = FuncCheckerForSetting.WhereManager.makeWhereFromList(
                    funcName,
                    methodNameStr,
                    argsPairList,
                    formalArgIndexToNameToTypeList
                )
                val millSec = FuncCheckerForSetting.Getter.getIntFromArgMapByIndex(
                    mapArgMapList,
                    args.millSecKeyToIndex,
                    where
                ).let { millSecToErr ->
                    val funcErr = millSecToErr.second
                        ?: return@let millSecToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val bitmap = varNameToBitmapMap?.get(ImageActionKeyManager.BitmapVar.itPronoun)
                if(millSec < 0){
                    return Pair(
                        bitmap,
                        null,
                    ) to null
                }
                withContext(Dispatchers.IO){
                    delay(millSec.toLong())
                }
                Pair(
                   bitmap,
                   null,
                ) to null
            }
        }
    }

    private enum class MethodNameClass(
        val str: String,
        val args: DelayMethodArgClass,
    ){
        MILL("mill", DelayMethodArgClass.MillArgs),
    }

    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }

    private sealed class DelayMethodArgClass {
        data object MillArgs : DelayMethodArgClass(), ArgType {
            override val entries = MillEnumArgs.entries
            val millSecKeyToIndex = Pair(
                MillEnumArgs.MILL_SEC.key,
                MillEnumArgs.MILL_SEC.index
            )
            enum class MillEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                MILL_SEC("millSec", 0, FuncCheckerForSetting.ArgType.INT),
            }
        }
    }
}