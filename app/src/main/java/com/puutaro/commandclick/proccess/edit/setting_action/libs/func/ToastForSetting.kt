package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import android.content.Context
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.CheckTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ToastForSetting {

    suspend fun handle(
        context: Context?,
        funcName: String,
        methodNameStr: String,
        argsPairList: List<Pair<String, String>>
    ): FuncCheckerForSetting.FuncCheckErr? {
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
            return FuncCheckerForSetting.FuncCheckErr("Method name not found: ${spanFuncTypeStr}.${spanMethodNameStr}")
        }
        FuncCheckerForSetting.checkArgs(
            funcName,
            methodNameStr,
            methodNameClass.readArgsNameToTypeList,
            argsPairList
        )?.let {
                argsCheckErr ->
            return argsCheckErr
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
        withContext(Dispatchers.Main) {
            when (methodNameClass) {
                MethodNameClass.SHORT -> {
                    val firstArg = argsList.get(0)
                    ToastUtils.showShort(
                        firstArg
                    )

                }

                MethodNameClass.LONG -> {
                    val firstArg = argsList.get(0)
                    ToastUtils.showLong(
                        firstArg
                    )
                }
            }
        }
        return null
    }

    enum class MethodNameClass(
        val str: String,
        val readArgsNameToTypeList: List<Pair<String, FuncCheckerForSetting.ArgType>>,
    ){
        SHORT("short", shortArgsNameToTypeList),
        LONG("long", longArgsNameToTypeList),
    }

    private val shortArgsNameToTypeList = listOf(
        Pair("message", FuncCheckerForSetting.ArgType.STRING)
    )


    private val longArgsNameToTypeList = listOf(
        Pair("message", FuncCheckerForSetting.ArgType.STRING)
    )
}