package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.func.MathCulc
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import kotlin.enums.EnumEntries


object MathCulcForSetting {

    private const val defaultNullMacroStr = FuncCheckerForSetting.defaultNullMacroStr

    suspend fun handle(
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
                val mapArgMapList = FuncCheckerForSetting.MapArg.makeMapArgMapListByName(
                    formalArgIndexToNameToTypeList,
                    argsPairList
                )
                val where = FuncCheckerForSetting.WhereManager.makeWhereFromList(
                    funcName,
                    methodNameStr,
                    argsPairList,
                    formalArgIndexToNameToTypeList
                )
                val formulaStr = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.formulaKeyToDefaultValueStr,
                    where
                ).let { formulaStrToErr ->
                    val funcErr = formulaStrToErr.second
                        ?: return@let formulaStrToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val inputCon = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.inputConKeyToDefaultValueStr,
                    where
                ).let { inputConToErr ->
                    val funcErr = inputConToErr.second
                        ?: return@let inputConToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val elVarName = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.elVarNameKeyToDefaultValueStr,
                    where
                ).let { elVarNameToErr ->
                    val funcErr = elVarNameToErr.second
                        ?: return@let elVarNameToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val indexVarName = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.indexVarNameKeyToDefaultValueStr,
                    where
                ).let { indexVarNameToErr ->
                    val funcErr = indexVarNameToErr.second
                        ?: return@let indexVarNameToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                if(
                    indexVarName == elVarName
                    && indexVarName != defaultNullMacroStr
                    ){
                    val spanIndexVarName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.lightBlue,
                        args.indexVarNameKeyToDefaultValueStr.first
                    )
                    val spanElVarName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.lightBlue,
                        args.elVarNameKeyToDefaultValueStr.first
                    )
                    val spanWhere = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errBrown,
                        where
                    )
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to  FuncCheckerForSetting. FuncCheckErr(
                        "Must be different from ${spanIndexVarName} and ${spanElVarName}: ${spanWhere} "
                    )
                }
                val separator = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.separatorKeyToDefaultValueStr,
                    where
                ).let { separatorToErr ->
                    val funcErr = separatorToErr.second
                        ?: return@let separatorToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val defaultSeparator =
                    args.separatorKeyToDefaultValueStr.second
                val isSeparatorNull =
                    separator == defaultSeparator
                val joinStr = when(isSeparatorNull) {
                    true -> String()
                    else -> FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                        mapArgMapList,
                        args.joinStrKeyToDefaultValueStr,
                        where
                    ).let { joinStrToErr ->
                        val funcErr = joinStrToErr.second
                        if (funcErr != null) {
                            return Pair(
                                null,
                                SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                            ) to funcErr
                        }
                        SettingFuncTool.makeJoinStrBySeparator(
                            joinStrToErr,
                            separator,
                            args.joinStrKeyToDefaultValueStr.second,
                        )
                    }
                }
                val semaphoreInt = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.semaphoreKeyToDefaultValueStr,
                    where
                ).let { semaphoreIntToErr ->
                    val funcErr = semaphoreIntToErr.second
                        ?: return@let semaphoreIntToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                return Culc.culc(
                    funcName,
                    methodNameStr,
                    formulaStr,
                    inputCon,
                    separator,
                    joinStr,
                    semaphoreInt,
                    elVarName,
                    indexVarName,
                    Culc.Type.INT
                )
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
                val mapArgMapList = FuncCheckerForSetting.MapArg.makeMapArgMapListByName(
                    formalArgIndexToNameToTypeList,
                    argsPairList
                )
                val where = FuncCheckerForSetting.WhereManager.makeWhereFromList(
                    funcName,
                    methodNameStr,
                    argsPairList,
                    formalArgIndexToNameToTypeList
                )
                val formulaStr = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.formulaKeyToDefaultValueStr,
                    where
                ).let { formulaStrToErr ->
                    val funcErr = formulaStrToErr.second
                        ?: return@let formulaStrToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val inputCon = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.inputConKeyToDefaultValueStr,
                    where
                ).let { inputConToErr ->
                    val funcErr = inputConToErr.second
                        ?: return@let inputConToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val elVarName = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.elVarNameKeyToDefaultValueStr,
                    where
                ).let { elVarNameToErr ->
                    val funcErr = elVarNameToErr.second
                        ?: return@let elVarNameToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val indexVarName = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.indexVarNameKeyToDefaultValueStr,
                    where
                ).let { indexVarNameToErr ->
                    val funcErr = indexVarNameToErr.second
                        ?: return@let indexVarNameToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                if(
                    indexVarName == elVarName
                    && indexVarName != defaultNullMacroStr
                ){
                    val spanIndexVarName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.lightBlue,
                        args.indexVarNameKeyToDefaultValueStr.first
                    )
                    val spanElVarName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.lightBlue,
                        args.elVarNameKeyToDefaultValueStr.first
                    )
                    val spanWhere = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errBrown,
                        where
                    )
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to  FuncCheckerForSetting. FuncCheckErr(
                        "Must be different from ${spanIndexVarName} and ${spanElVarName}: ${spanWhere} "
                    )
                }
                val separator = FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                    mapArgMapList,
                    args.separatorKeyToDefaultValueStr,
                    where
                ).let { separatorToErr ->
                    val funcErr = separatorToErr.second
                        ?: return@let separatorToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val defaultSeparator =
                    args.separatorKeyToDefaultValueStr.second
                val isSeparatorNull =
                    separator == defaultSeparator
                val joinStr = when(isSeparatorNull) {
                    true -> String()
                    else -> FuncCheckerForSetting.Getter.getStringFromArgMapByName(
                        mapArgMapList,
                        args.joinStrKeyToDefaultValueStr,
                        where
                    ).let { joinStrToErr ->
                        val funcErr = joinStrToErr.second
                        if (funcErr != null) {
                            return Pair(
                                null,
                                SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                            ) to funcErr
                        }
                        SettingFuncTool.makeJoinStrBySeparator(
                            joinStrToErr,
                            separator,
                            args.joinStrKeyToDefaultValueStr.second,
                        )
                    }
                }
                val semaphoreInt = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.semaphoreKeyToDefaultValueStr,
                    where
                ).let { semaphoreIntToErr ->
                    val funcErr = semaphoreIntToErr.second
                        ?: return@let semaphoreIntToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                return Culc.culc(
                    funcName,
                    methodNameStr,
                    formulaStr,
                    inputCon,
                    separator,
                    joinStr,
                    semaphoreInt,
                    elVarName,
                    indexVarName,
                    Culc.Type.FLOAT
                )
            }
        }
    }

    private object Culc {

        enum class Type {
            FLOAT,
            INT,
        }

        suspend fun culc(
            funcName: String,
            methodNameStr: String,
            formulaStrSrc: String,
            inputCon: String,
            separator: String,
            joinStr: String,
            semaphoreInt: Int,
            elVarName: String,
            indexVarName: String,
            type: Type
        ): Pair<
                Pair<
                        String?,
                        SettingActionKeyManager.BreakSignal?
                        >?,
                FuncCheckerForSetting.FuncCheckErr?
                >?  {
            val info = listOf(
                "func.method: ${funcName}.${methodNameStr}",
                "formula: ${formulaStrSrc}",
                "inputCon: ${inputCon}",
                "separator: ${separator}",
                "joinStr: ${joinStr}",
                "semaphore: ${semaphoreInt}",
                "elVarName: ${elVarName}",
                "indexVarName: ${indexVarName}",
                "type: ${type}",
            ).joinToString(", ")
          return try {
              val cuclResult = execCulc(
                  formulaStrSrc,
                  inputCon,
                  separator,
                  joinStr,
                  semaphoreInt,
                  elVarName,
                  indexVarName,
                  type
              )
              Pair(
                  cuclResult,
                  null
              ) to null
          }  catch (e: Exception){
              val err = makeFormulaErr(
                  funcName,
                  methodNameStr,
                  info,
                  e.toString(),
              )
              Pair(
                  null,
                  SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
              ) to err
          }
        }
        suspend fun execCulc(
            formulaStrSrc: String,
            inputCon: String,
            separator: String,
            joinStr: String,
            semaphoreInt: Int,
            elVarName: String,
            indexVarName: String,
            type: Type
        ): String {
           if(
               inputCon == defaultNullMacroStr
               || separator == defaultNullMacroStr
               ) {
               return when(type){
                   Type.FLOAT -> MathCulc.float(formulaStrSrc)
                   Type.INT -> MathCulc.int(formulaStrSrc)
               }.toString()
           }
            val semaphore = when (semaphoreInt > 0) {
                true -> Semaphore(semaphoreInt)
                else -> null
            }
            val culcListCon = withContext(Dispatchers.IO) {
                val indexToCulcStrJob = when (semaphore == null) {
                    false -> inputCon.split(separator).mapIndexed { index, inputLine ->
                        async {
                            semaphore.withPermit {
                                index to execCulc(
                                    index,
                                    indexVarName,
                                    inputLine,
                                    elVarName,
                                    formulaStrSrc,
                                    type
                                )
                            }
                        }
                    }
                    else -> inputCon.split(separator).mapIndexed { index, inputLine ->
                        async {
                            index to execCulc(
                                index,
                                indexVarName,
                                inputLine,
                                elVarName,
                                formulaStrSrc,
                                type
                            )
                        }
                    }
                }
                indexToCulcStrJob.awaitAll().sortedBy { it.first }.map {
                        indexToCulcStr ->
                        indexToCulcStr.second
                }.let {
                    when(joinStr == defaultNullMacroStr) {
                        true -> it.joinToString(String())
                        else -> it.joinToString(joinStr)
                    }
                }
            }
            return culcListCon
        }

        private fun execCulc(
            index: Int,
            indexVarName: String,
            inputLine: String,
            elVarName: String,
            formulaStrSrc: String,
            type: Type
        ): String {
            val formulaStr = makeFormulaStr(
                index,
                indexVarName,
                inputLine,
                elVarName,
                formulaStrSrc,
            )
            return when (type) {
                Type.FLOAT -> MathCulc.float(formulaStr)
                Type.INT -> MathCulc.int(formulaStr)
            }.toString()
        }
    }

    private fun makeFormulaStr(
        index: Int,
        indexVarName: String,
        inputLine: String,
        elVarName: String,
        formulaStrSrc: String,
    ): String {
        val inputLineWithSetElVar = when(
            elVarName != defaultNullMacroStr
            && elVarName.isNotEmpty()
            )  {
            false -> formulaStrSrc
            true -> formulaStrSrc.replace(
                "${'$'}${elVarName}",
                inputLine
            )
        }
        val inputLineWithSetIndex = when (
                    indexVarName != defaultNullMacroStr
                            && indexVarName.isNotEmpty()
                    ) {
            false -> inputLineWithSetElVar
            else -> inputLineWithSetElVar.replace(
                "${'$'}${indexVarName}",
                index.toString()
            )
        }
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "lculc.txt").absolutePath,
//            listOf(
//                "formulaStrSrc: ${formulaStrSrc}",
//                "index: ${index}",
//                "indexVarName: ${indexVarName}",
//                "elVarName: ${elVarName}",
//                "inputLineWithSetElVar: ${inputLineWithSetElVar}",
//                "inputLineWithSetIndex: ${inputLineWithSetIndex}",
//            ).joinToString("\n")
//        )
        return inputLineWithSetIndex
    }

    private fun makeFormulaErr(
        funcName: String,
        methodNameStr: String,
        formulaStr: String,
        errMessage: String
    ): FuncCheckerForSetting.FuncCheckErr {
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
        return FuncCheckerForSetting.FuncCheckErr("Formula err ${errMessage}: ${spanFuncTypeStr}.${spanMethodNameStr}, arg: ${spanFirstArgStr}")
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
            val inputConKeyToDefaultValueStr = Pair(
                IntEnumArgs.INPUT_CON.key,
                IntEnumArgs.INPUT_CON.defaultValueStr
            )
            val formulaKeyToDefaultValueStr = Pair(
                IntEnumArgs.FORMULA.key,
                IntEnumArgs.FORMULA.defaultValueStr
            )
            val elVarNameKeyToDefaultValueStr = Pair(
                IntEnumArgs.EL_VAR_NAME.key,
                IntEnumArgs.EL_VAR_NAME.defaultValueStr
            )
            val indexVarNameKeyToDefaultValueStr = Pair(
                IntEnumArgs.INDEX_VAR_NAME.key,
                IntEnumArgs.INDEX_VAR_NAME.defaultValueStr
            )
            val separatorKeyToDefaultValueStr = Pair(
                IntEnumArgs.SEPARATOR.key,
                IntEnumArgs.SEPARATOR.defaultValueStr
            )
            val joinStrKeyToDefaultValueStr = Pair(
                IntEnumArgs.JOIN_STR.key,
                IntEnumArgs.JOIN_STR.defaultValueStr
            )
            val semaphoreKeyToDefaultValueStr = Pair(
                IntEnumArgs.SEMAPHORE.key,
                IntEnumArgs.SEMAPHORE.defaultValueStr
            )

            enum class IntEnumArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ){
                INPUT_CON("inputCon", defaultNullMacroStr, FuncCheckerForSetting.ArgType.STRING),
                FORMULA("formula", null, FuncCheckerForSetting.ArgType.STRING),
                EL_VAR_NAME("elVarName", defaultNullMacroStr, FuncCheckerForSetting.ArgType.STRING),
                INDEX_VAR_NAME("indexVarName", defaultNullMacroStr, FuncCheckerForSetting.ArgType.STRING),
                SEPARATOR("separator", defaultNullMacroStr, FuncCheckerForSetting.ArgType.STRING),
                JOIN_STR("joinStr", defaultNullMacroStr, FuncCheckerForSetting.ArgType.STRING),
                SEMAPHORE("semaphore", 0.toString(), FuncCheckerForSetting.ArgType.INT)
            }
        }
        data object FloatArgs : MathMethodArgClass(), ArgType {
            override val entries = FloatEnumArgs.entries
            val inputConKeyToDefaultValueStr = Pair(
                FloatEnumArgs.INPUT_CON.key,
                FloatEnumArgs.INPUT_CON.defaultValueStr
            )
            val formulaKeyToDefaultValueStr = Pair(
                FloatEnumArgs.FORMULA.key,
                FloatEnumArgs.FORMULA.defaultValueStr
            )
            val elVarNameKeyToDefaultValueStr = Pair(
                FloatEnumArgs.EL_VAR_NAME.key,
                FloatEnumArgs.EL_VAR_NAME.defaultValueStr
            )
            val indexVarNameKeyToDefaultValueStr = Pair(
                FloatEnumArgs.INDEX_VAR_NAME.key,
                FloatEnumArgs.INDEX_VAR_NAME.defaultValueStr
            )
            val separatorKeyToDefaultValueStr = Pair(
                FloatEnumArgs.SEPARATOR.key,
                FloatEnumArgs.SEPARATOR.defaultValueStr
            )
            val joinStrKeyToDefaultValueStr = Pair(
                FloatEnumArgs.JOIN_STR.key,
                FloatEnumArgs.JOIN_STR.defaultValueStr
            )
            val semaphoreKeyToDefaultValueStr = Pair(
                FloatEnumArgs.SEMAPHORE.key,
                FloatEnumArgs.SEMAPHORE.defaultValueStr
            )

            enum class FloatEnumArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ){
                INPUT_CON("inputCon", defaultNullMacroStr, FuncCheckerForSetting.ArgType.STRING),
                FORMULA("formula", null, FuncCheckerForSetting.ArgType.STRING),
                EL_VAR_NAME("elVarName", defaultNullMacroStr, FuncCheckerForSetting.ArgType.STRING),
                INDEX_VAR_NAME("indexVarName", defaultNullMacroStr, FuncCheckerForSetting.ArgType.STRING),
                SEPARATOR("separator", defaultNullMacroStr, FuncCheckerForSetting.ArgType.STRING),
                JOIN_STR("joinStr", defaultNullMacroStr, FuncCheckerForSetting.ArgType.STRING),
                SEMAPHORE("semaphore", 0.toString(), FuncCheckerForSetting.ArgType.INT)
            }
        }
    }
}
