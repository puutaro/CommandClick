package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.func.MathCulc
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import kotlin.enums.EnumEntries


object MathCulcForSetting {
    fun handle(
        funcName: String,
        methodNameStr: String,
        argsPairList: List<Pair<String, String>>,
//        varNameToValueStrMap: Map<String, String?>,
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
//        FuncCheckerForSetting.checkArgs(
//            funcName,
//            methodNameStr,
//            methodNameClass.argsNameToTypeList,
//            argsPairList,
////            varNameToValueStrMap,
//        )?.let {
//                argsCheckErr ->
//            return null to argsCheckErr
//        }
//        val argsList = argsPairList.map {
//            it.second
//        }
        val funcCheckerForSetting = FuncCheckerForSetting(
            funcName,
            methodNameStr,
        )
        val args =
            methodNameClass.args
        when(args) {
            is MathMethodArgClass.IntArgs -> {
                val formalArgIndexToNameToTypeList =
                    args.entries.mapIndexed { index, formalArgsNameToType ->
                        Triple(
                            index,
                            formalArgsNameToType.key,
                            formalArgsNameToType.type,
                        )
                    }
                val mapArgMapList = FuncCheckerForSetting.Companion.MapArg.makeMapArgMapListByIndex(
                    formalArgIndexToNameToTypeList,
                    argsPairList
                )
                val where = FuncCheckerForSetting.makeWhereFromList(
                    argsPairList,
                    formalArgIndexToNameToTypeList
                )
                val formulaStr = funcCheckerForSetting.getStringFromArgMapByIndex(
                    funcCheckerForSetting,
                    mapArgMapList,
                    args.formulaKeyToIndex,
                    where
                ).let { formulaStrToErr ->
                    val funcErr = formulaStrToErr.second
                        ?: return@let formulaStrToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                return try {
                    Pair(
                        MathCulc.int(formulaStr).toString(),
                        null,
                    ) to null
                } catch (e: Exception) {
                    makeFormulaErr(
                        funcName,
                        methodNameStr,
                        formulaStr,
                    )
                }

            }

            is MathMethodArgClass.FloatArgs -> {
                val formalArgIndexToNameToTypeList =
                    args.entries.mapIndexed { index, formalArgsNameToType ->
                        Triple(
                            index,
                            formalArgsNameToType.key,
                            formalArgsNameToType.type,
                        )
                    }
                val mapArgMapList = FuncCheckerForSetting.Companion.MapArg.makeMapArgMapListByIndex(
                    formalArgIndexToNameToTypeList,
                    argsPairList
                )
                val where = FuncCheckerForSetting.makeWhereFromList(
                    argsPairList,
                    formalArgIndexToNameToTypeList
                )
                val formulaStr = funcCheckerForSetting.getStringFromArgMapByIndex(
                    funcCheckerForSetting,
                    mapArgMapList,
                    args.formulaKeyToIndex,
                    where
                ).let { formulaStrToErr ->
                    val funcErr = formulaStrToErr.second
                        ?: return@let formulaStrToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                return try {
                    Pair(
                        MathCulc.float(formulaStr).toString(),
                        null,
                    ) to null
                } catch (e: Exception) {
                    makeFormulaErr(
                        funcName,
                        methodNameStr,
                        formulaStr,
                    )
                }
            }
        }
    }

    private fun makeFormulaErr(
        funcName: String,
        methodNameStr: String,
        formulaStr: String,
    ): Pair<Nothing?, FuncCheckerForSetting. FuncCheckErr> {
        val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
            CheckTool.errBrown,
            funcName
        )
        val spanMethodNameStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
            CheckTool.errRedCode,
            methodNameStr
        )
        val spanFirstArgStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
            CheckTool.errRedCode,
            formulaStr
        )
        return null to FuncCheckerForSetting.FuncCheckErr("Formula err: ${spanFuncTypeStr}.${spanMethodNameStr}, arg: ${spanFirstArgStr}")
    }

    private enum class MethodNameClass(
        val str: String,
        val args: MathMethodArgClass,
    ){
        INT(
            "int",
            MathMethodArgClass.IntArgs,
        ),
        FLOAT(
            "float",
            MathMethodArgClass.FloatArgs,
        )
    }


    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }

    private sealed class MathMethodArgClass {
        data object IntArgs : MathMethodArgClass(), ArgType {
            override val entries = IntEnumArgs.entries
            val formulaKeyToIndex = Pair(
                IntEnumArgs.FORMULA.key,
                IntEnumArgs.FORMULA.index
            )

            enum class IntEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.Companion.ArgType,
            ){
                FORMULA("formula", 0, FuncCheckerForSetting.Companion.ArgType.STRING),
            }
        }
        data object FloatArgs : MathMethodArgClass(), ArgType {
            override val entries = FloatEnumArgs.entries
            val formulaKeyToIndex = Pair(
                FloatEnumArgs.FORMULA.key,
                FloatEnumArgs.FORMULA.index
            )

            enum class FloatEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.Companion.ArgType,
            ){
                FORMULA("formula", 0, FuncCheckerForSetting.Companion.ArgType.STRING),
            }
        }
    }
}
