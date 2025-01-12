package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import android.content.Context
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.enums.EnumEntries

object DebugForSetting {

    suspend fun handle(
        context: Context?,
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
//        FuncCheckerForSetting.checkArgs(
//            funcName,
//            methodNameStr,
//            methodNameClass.readArgsNameToTypeList,
//            argsPairList,
////            varNameToValueStrMap,
//        )?.let {
//                argsCheckErr ->
//            return null to argsCheckErr
//        }
//        val argsList = argsPairList.map {
//            it.second
//        }
//        BroadcastSender.normalSend(
//            context,
//            BroadCastIntentSchemeTerm.SETING_ACTION_FUNC.action,
//            listOf(
//                SettingActionFuncExtra.FUNC_NAME.schema to SettingActionFuncBroadcastManager.FuncClass.TOAST.str,
//                SettingActionFuncExtra.METHOD_NAME.schema to methodNameClass.str,
//                SettingActionFuncExtra.ARGS.schema to SettingActionFuncBroadcastManager.makeArgsListCon(
//                    argsList
//                ),
//            )
//        )
        val args =
            methodNameClass.args
        return withContext(Dispatchers.Main) {
            when (args) {
                is DebugMethodArgClass.ReflectArgs -> {
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
                    val message = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                        mapArgMapList,
                        args.messageKeyToIndex,
                        where
                    ).let { msgToErr ->
                        val funcErr = msgToErr.second
                            ?: return@let msgToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
//                    SettingFuncTool.getValueStrFromMapOrIt(
//                        argsList.get(0),
//                        varNameToValueStrMap,
//                    )
//                    FileSystems.writeFile(
//                        File(UsePath.cmdclickDefaultSDebugAppDirPath, "lebug_reflect.txt").absolutePath,
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
                    Pair(message, null) to null
                }
                else -> null
            }
        }
    }

    private enum class MethodNameClass(
        val str: String,
        val args: DebugMethodArgClass?,
    ){
        REFLECT("reflect", DebugMethodArgClass.ReflectArgs),
        NULL("null", null),
    }


    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }

    private sealed class DebugMethodArgClass {
        data object ReflectArgs : DebugMethodArgClass(), ArgType {
            override val entries = ReflectEnumArgs.entries
            val messageKeyToIndex = Pair(
                ReflectEnumArgs.MESSAGE.key,
                ReflectEnumArgs.MESSAGE.index
            )

            enum class ReflectEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                MESSAGE("message", 0, FuncCheckerForSetting.ArgType.STRING),
            }
        }
    }


}