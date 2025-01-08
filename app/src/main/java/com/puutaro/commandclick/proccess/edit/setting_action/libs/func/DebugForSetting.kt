package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import android.content.Context
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
            FuncCheckerForSetting2.FuncCheckErr?
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
            return null to FuncCheckerForSetting2.FuncCheckErr("Method name not found: ${spanFuncTypeStr}.${spanMethodNameStr}")
        }
        FuncCheckerForSetting2.checkArgs(
            funcName,
            methodNameStr,
            methodNameClass.readArgsNameToTypeList,
            argsPairList,
//            varNameToValueStrMap,
        )?.let {
                argsCheckErr ->
            return null to argsCheckErr
        }
        val argsList = argsPairList.map {
            it.second
        }
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
        return withContext(Dispatchers.Main) {
            when (methodNameClass) {
                MethodNameClass.REFLECT -> {
                    val msg = argsList.get(0)
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
                    Pair(msg, null) to null
                }
                MethodNameClass.NULL -> null
            }
        }
    }

    enum class MethodNameClass(
        val str: String,
        val readArgsNameToTypeList: List<Pair<String, FuncCheckerForSetting2.ArgType>>?,
    ){
        REFLECT("reflect", shortArgsNameToTypeList),
        NULL("null", null)
    }

    private val shortArgsNameToTypeList = listOf(
        Pair("message", FuncCheckerForSetting2.ArgType.STRING),
    )


}