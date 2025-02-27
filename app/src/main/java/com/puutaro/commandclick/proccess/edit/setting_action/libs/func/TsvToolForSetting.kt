package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.util.tsv.TsvTool
import kotlin.enums.EnumEntries

object TsvToolForSetting {
    fun handle(
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
        }  ?: let {
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
            is TsvToolMethodArgClass.GetKeyValueFromFileArgs -> {
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
                val tsvPath = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.filePathKeyToIndex,
                    where
                ).let { tsvPathToErr ->
                    val funcErr = tsvPathToErr.second
                        ?: return@let tsvPathToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val key = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.keyToIndex,
                    where
                ).let { keyToErr ->
                    val funcErr = keyToErr.second
                        ?: return@let keyToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                Pair(
                    TsvTool.getKeyValueFromFile(
                        tsvPath,
                        key,
                    ),
                    null,
                ) to null
            }
            is TsvToolMethodArgClass.GetKeyValueArgs -> {
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
                val tsvCon = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.tsvConKeyToIndex,
                    where
                ).let { tsvConToErr ->
                    val funcErr = tsvConToErr.second
                        ?: return@let tsvConToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val key = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.keyToIndex,
                    where
                ).let { keyToErr ->
                    val funcErr = keyToErr.second
                        ?: return@let keyToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                Pair(
                    TsvTool.getKeyValue(
                        tsvCon,
                        key,
                    ),
                    null,
                    ) to null
            }
        }
    }

    private enum class MethodNameClass(
        val str: String,
        val args: TsvToolMethodArgClass,
    ){
        GET_KEY_VALUE_FROM_FILE("getKeyValueFromFile", TsvToolMethodArgClass.GetKeyValueFromFileArgs),
        GET_KEY_VALUE("getKeyValue", TsvToolMethodArgClass.GetKeyValueArgs),
    }


    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }

    private sealed class TsvToolMethodArgClass {
        data object GetKeyValueFromFileArgs : TsvToolMethodArgClass(), ArgType {
            override val entries = GetKeyValueFromFileEnumArgs.entries
            val filePathKeyToIndex = Pair(
                GetKeyValueFromFileEnumArgs.FILE_PATH.key,
                GetKeyValueFromFileEnumArgs.FILE_PATH.index
            )
            val keyToIndex = Pair(
                GetKeyValueFromFileEnumArgs.KEY.key,
                GetKeyValueFromFileEnumArgs.KEY.index
            )

            enum class GetKeyValueFromFileEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                FILE_PATH("filePath", 0, FuncCheckerForSetting.ArgType.STRING),
                KEY("key", 1, FuncCheckerForSetting.ArgType.STRING),
            }
        }
        data object GetKeyValueArgs : TsvToolMethodArgClass(), ArgType {
            override val entries = GetKeyValueEnumArgs.entries
            val tsvConKeyToIndex = Pair(
                GetKeyValueEnumArgs.TSV_CON.key,
                GetKeyValueEnumArgs.TSV_CON.index
            )
            val keyToIndex = Pair(
                GetKeyValueEnumArgs.KEY.key,
                GetKeyValueEnumArgs.KEY.index
            )
            enum class GetKeyValueEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                TSV_CON("tsvCon", 0, FuncCheckerForSetting.ArgType.STRING),
                KEY("key", 1, FuncCheckerForSetting.ArgType.STRING),
            }
        }
    }

}