package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.res.CmdClickColorStr
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.util.image_tools.ColorTool
import kotlin.enums.EnumEntries

object ColorForSetting {
    suspend fun handle(
        funcName: String,
        methodNameStr: String,
        argsPairList: List<Pair<String, String>>,
    ): Pair<
            Pair<
                    String?,
                    SettingActionKeyManager.BreakSignal?
                    >?,
            FuncCheckerForSetting.FuncCheckErr?
            >? {
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
        val args = methodNameClass.args
        if(args != ColorMethodArgClass.RndArgs){
            return null
        }
        return when(args){
            is ColorMethodArgClass.RndArgs -> {
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
                val rndMacroStr = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.rndMacroKeyToIndex,
                    where
                ).let { (rndMacroStr, funcErr) ->
                    funcErr
                        ?: return@let rndMacroStr
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                Pair(
                    ColorTool.parseColorMacro(rndMacroStr)
                        ?: CmdClickColorStr.entries.random().str,
                    null
                ) to null
            }
        }
    }

    private enum class MethodNameClass(
        val str: String,
        val args: ColorMethodArgClass
    ) {
        RND("rnd", ColorMethodArgClass.RndArgs),
    }

    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }

    private sealed class ColorMethodArgClass {
        data object RndArgs : ColorMethodArgClass(), ArgType {
            override val entries = RndEnumArgs.entries
            val rndMacroKeyToIndex = Pair(
                RndEnumArgs.RND_MACRO.key,
                RndEnumArgs.RND_MACRO.index
            )

            enum class RndEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                RND_MACRO("rndMacro", 0, FuncCheckerForSetting.ArgType.STRING),
            }
        }
    }

}
