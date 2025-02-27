package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import android.content.Context
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.enums.EnumEntries

object ToastForSetting {

    suspend fun handle(
        context: Context?,
        funcName: String,
        methodNameStr: String,
        argsPairList: List<Pair<String, String>>,
//        varNameToValueStrMap: Map<String, String?>
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
            return null to FuncCheckerForSetting.FuncCheckErr("Method name not found: ${spanFuncTypeStr}.${spanMethodNameStr}")
        }
        val args =
            methodNameClass.args
        withContext(Dispatchers.Main) {
            when (args) {
                is ToastMethodArgClass.ShortArgs -> {
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
                    val msg = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                        mapArgMapList,
                        args.messageKeyToIndex,
                        where
                    ).let { msgToErr ->
                        val funcErr = msgToErr.second
                            ?: return@let msgToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                        ) to funcErr
                    }
//                    SettingFuncTool.getValueStrFromMapOrIt(
//                        argsList.get(0),
//                        varNameToValueStrMap,
//                    )
//                    val bitmapVarRegex = Regex("^[$][{][a-zA-Z0-9_]+[}]$")
//                    FileSystems.writeFile(
//                        File(UsePath.cmdclickDefaultSDebugAppDirPath, "ltoast.txt").absolutePath,
//                        listOf(
//                            "bitmapVarRegex: ${bitmapVarRegex}",
//                            "matches: ${bitmapVarRegex.matches(argsList.get(0))}",
//                            "matchesAB: ${bitmapVarRegex.matches("${'$'}{AB}")}",
//                            "matches: ${bitmapVarRegex.matches("${'$'}{it}")}",
//                            "SettingActionKeyManager.ValueStrVar.convertStrKey(rawValueStr): ${SettingActionKeyManager.ValueStrVar.convertStrKey(argsList.get(0))}",
//                            "argsList.get(0): ${argsList.get(0)}",
//                            "varNameToValueStrMap: ${varNameToValueStrMap}",
//                            "msg: ${msg}",
//                        ).joinToString("\n")
//                    )
                    ToastUtils.showShort(
                        msg
                    )

                }

                is ToastMethodArgClass.LongArgs -> {
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
                    val msg = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                        mapArgMapList,
                        args.messageKeyToIndex,
                        where
                    ).let { msgToErr ->
                        val funcErr = msgToErr.second
                            ?: return@let msgToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                        ) to funcErr
                    }
                    ToastUtils.showLong(
                        msg
                    )
                }
            }
        }
        return null
    }

    private enum class MethodNameClass(
        val str: String,
        val args: ToastMethodArgClass,
    ){
        SHORT("short", ToastMethodArgClass.ShortArgs),
        LONG("long", ToastMethodArgClass.LongArgs),
    }


    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }

    private sealed class ToastMethodArgClass {
        data object ShortArgs : ToastMethodArgClass(), ArgType {
            override val entries = RangeEnumArgs.entries
            val messageKeyToIndex = Pair(
                RangeEnumArgs.MESSAGE.key,
                RangeEnumArgs.MESSAGE.index
            )

            enum class RangeEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                MESSAGE("message", 0, FuncCheckerForSetting.ArgType.STRING),

            }
        }
        data object LongArgs : ToastMethodArgClass(), ArgType {
            override val entries = LongEnumArgs.entries
            val messageKeyToIndex = Pair(
                LongEnumArgs.MESSAGE.key,
                LongEnumArgs.MESSAGE.index
            )

            enum class LongEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                MESSAGE("message", 0, FuncCheckerForSetting.ArgType.STRING),

            }
        }
    }
}