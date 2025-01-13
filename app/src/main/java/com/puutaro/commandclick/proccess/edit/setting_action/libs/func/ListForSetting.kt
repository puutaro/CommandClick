package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.enums.EnumEntries

object ListForSetting {
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
            > {
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
////            varNameToValueStrMap
//        )?.let { argsCheckErr ->
//            return null to argsCheckErr
//        }
//        val argsList = argsPairList.map {
//            it.second
//        }
        val args =
            methodNameClass.args
        return withContext(Dispatchers.Main) {
            when (args) {
                is ListMethodArgClass.RndArgs -> {
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
                    val listCon =
                        FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                            mapArgMapList,
                            args.strsKeyToIndex,
                            where
                        ).let { listConToErr ->
                        val funcErr = listConToErr.second
                            ?: return@let listConToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    val separator =
                        FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                            mapArgMapList,
                            args.separatorToIndex,
                            where
                        ).let { separatorToErr ->
                        val funcErr = separatorToErr.second
                            ?: return@let separatorToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    Pair(
                        listCon.split(separator).filter{
                            it.trim().isNotEmpty()
                        }.random(),
                        null
                    ) to null
                }
                is ListMethodArgClass.ShufArgs -> {
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
                    val listCon = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                        mapArgMapList,
                        args.strsKeyToIndex,
                        where
                    ).let { listConToErr ->
                        val funcErr = listConToErr.second
                            ?: return@let listConToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    val separator =
                        FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                            mapArgMapList,
                            args.separatorToIndex,
                            where
                        ).let { separatorToErr ->
                        val funcErr = separatorToErr.second
                            ?: return@let separatorToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    Pair(
                        listCon.split(separator).filter{
                            it.trim().isNotEmpty()
                        }.shuffled().joinToString(separator),
                        null
                    ) to null
                }
                is ListMethodArgClass.TakeArgs -> {
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
                    val listCon =
                        FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                            mapArgMapList,
                            args.strsKeyToIndex,
                            where
                        ).let { listConToErr ->
                        val funcErr = listConToErr.second
                            ?: return@let listConToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    val separator =
                        FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                            mapArgMapList,
                            args.separatorToIndex,
                            where
                        ).let { separatorToErr ->
                        val funcErr = separatorToErr.second
                            ?: return@let separatorToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    val takeNum =
                        FuncCheckerForSetting.Getter.getIntFromArgMapByIndex(
                            mapArgMapList,
                            args.numToIndex,
                            where
                        ).let { takeNumToErr ->
                        val funcErr = takeNumToErr.second
                            ?: return@let takeNumToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    Pair(
                        listCon.split(separator).filter{
                            it.trim().isNotEmpty()
                        }.take(takeNum).joinToString(separator),
                        null
                    ) to null
                }
                is ListMethodArgClass.TakeLastArgs -> {
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
                    val listCon = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                        mapArgMapList,
                        args.strsKeyToIndex,
                        where
                    ).let { listConToErr ->
                        val funcErr = listConToErr.second
                            ?: return@let listConToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    val separator = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                        mapArgMapList,
                        args.separatorToIndex,
                        where
                    ).let { separatorToErr ->
                        val funcErr = separatorToErr.second
                            ?: return@let separatorToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    val takeLastNum = FuncCheckerForSetting.Getter.getIntFromArgMapByIndex(
                        mapArgMapList,
                        args.numToIndex,
                        where
                    ).let { takeLastNumToErr ->
                        val funcErr = takeLastNumToErr.second
                            ?: return@let takeLastNumToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    Pair(
                        listCon.split(separator).filter{
                            it.trim().isNotEmpty()
                        }.takeLast(takeLastNum).joinToString(separator),
                        null,
                    ) to null
                }
                is ListMethodArgClass.JoinArgs -> {
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
                    val con = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                        mapArgMapList,
                        args.strsKeyToIndex,
                        where
                    ).let { conToErr ->
                        val funcErr = conToErr.second
                            ?: return@let conToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    val separator = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                        mapArgMapList,
                        args.separatorToIndex,
                        where
                    ).let { separatorToErr ->
                        val funcErr = separatorToErr.second
                            ?: return@let separatorToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    val joinStr = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                        mapArgMapList,
                        args.joinStrToIndex,
                        where
                    ).let { joinStrToErr ->
                        val funcErr = joinStrToErr.second
                            ?: return@let joinStrToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    Pair(
                        con.split(separator).joinToString(joinStr),
                        null,
                    ) to null
                }
            }
        }
    }

    private enum class MethodNameClass(
        val str: String,
        val args: ListMethodArgClass,
    ){
        RND("rnd", ListMethodArgClass.RndArgs),
        SHUF("shuf", ListMethodArgClass.ShufArgs),
        TAKE("take", ListMethodArgClass.TakeArgs),
        TAKE_LAST("takeLast", ListMethodArgClass.TakeLastArgs),
        JOIN("join", ListMethodArgClass.JoinArgs)
    }


    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }

    private sealed class ListMethodArgClass {
        data object RndArgs : ListMethodArgClass(), ArgType {
            override val entries = RndEnumArgs.entries
            val strsKeyToIndex = Pair(
                RndEnumArgs.STRS.key,
                RndEnumArgs.STRS.index
            )
            val separatorToIndex = Pair(
                RndEnumArgs.SEPARATOR.key,
                RndEnumArgs.SEPARATOR.index
            )

            enum class RndEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                STRS("strs", 0, FuncCheckerForSetting.ArgType.STRING),
                SEPARATOR("separator", 1, FuncCheckerForSetting.ArgType.STRING),
            }
        }
        data object ShufArgs : ListMethodArgClass(), ArgType {
            override val entries = ShufEnumArgs.entries
            val strsKeyToIndex = Pair(
                ShufEnumArgs.STRS.key,
                ShufEnumArgs.STRS.index
            )
            val separatorToIndex = Pair(
                ShufEnumArgs.SEPARATOR.key,
                ShufEnumArgs.SEPARATOR.index
            )

            enum class ShufEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                STRS("strs", 0, FuncCheckerForSetting.ArgType.STRING),
                SEPARATOR("separator", 1, FuncCheckerForSetting.ArgType.STRING),
            }
        }
        data object TakeArgs : ListMethodArgClass(), ArgType {
            override val entries = TakeEnumArgs.entries
            val strsKeyToIndex = Pair(
                TakeEnumArgs.STRS.key,
                TakeEnumArgs.STRS.index
            )
            val separatorToIndex = Pair(
                TakeEnumArgs.SEPARATOR.key,
                TakeEnumArgs.SEPARATOR.index
            )
            val numToIndex = Pair(
                TakeEnumArgs.NUM.key,
                TakeEnumArgs.NUM.index
            )

            enum class TakeEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                STRS("strs", 0, FuncCheckerForSetting.ArgType.STRING),
                SEPARATOR("separator", 1, FuncCheckerForSetting.ArgType.STRING),
                NUM("num", 2, FuncCheckerForSetting.ArgType.INT),
            }
        }
        data object TakeLastArgs : ListMethodArgClass(), ArgType {
            override val entries = TakeLastEnumArgs.entries
            val strsKeyToIndex = Pair(
                TakeLastEnumArgs.STRS.key,
                TakeLastEnumArgs.STRS.index
            )
            val separatorToIndex = Pair(
                TakeLastEnumArgs.SEPARATOR.key,
                TakeLastEnumArgs.SEPARATOR.index
            )
            val numToIndex = Pair(
                TakeLastEnumArgs.NUM.key,
                TakeLastEnumArgs.NUM.index
            )

            enum class TakeLastEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                STRS("strs", 0, FuncCheckerForSetting.ArgType.STRING),
                SEPARATOR("separator", 1, FuncCheckerForSetting.ArgType.STRING),
                NUM("num", 2, FuncCheckerForSetting.ArgType.INT),
            }
        }
        data object JoinArgs : ListMethodArgClass(), ArgType {
            override val entries = JoinEnumArgs.entries
            val strsKeyToIndex = Pair(
                JoinEnumArgs.STRS.key,
                JoinEnumArgs.STRS.index
            )
            val separatorToIndex = Pair(
                JoinEnumArgs.SEPARATOR.key,
                JoinEnumArgs.SEPARATOR.index
            )
            val joinStrToIndex = Pair(
                JoinEnumArgs.JOIN_STR.key,
                JoinEnumArgs.JOIN_STR.index
            )

            enum class JoinEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                STRS("strs", 0, FuncCheckerForSetting.ArgType.STRING),
                SEPARATOR("separator", 1, FuncCheckerForSetting.ArgType.STRING),
                JOIN_STR("joinStr", 2, FuncCheckerForSetting.ArgType.STRING),
            }
        }

    }
}
