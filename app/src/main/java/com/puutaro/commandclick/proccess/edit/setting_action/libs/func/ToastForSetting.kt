package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import android.content.Context
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.broadcast.SettingActionFuncExtra
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeTerm
import com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver.SettingActionFuncBroadcastManager
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender

object ToastForSetting {

    fun handle(
        context: Context?,
        methodNameStr: String,
        argsPairList: List<Pair<String, String>>
    ) {
        val methodNameClass = MethodNameClass.entries.firstOrNull {
            it.str == methodNameStr
        } ?: return
        val isErr = ArgsChecker.checkArgs(
            methodNameClass.readArgsNameList,
            argsPairList
        )
        if(isErr) return
        val argsList = argsPairList.map {
            it.second
        }
        BroadcastSender.normalSend(
            context,
            BroadCastIntentSchemeTerm.SETING_ACTION_FUNC.action,
            listOf(
                SettingActionFuncExtra.FUNC_NAME.schema to SettingActionFuncBroadcastManager.FuncClass.TOAST.str,
                SettingActionFuncExtra.METHOD_NAME.schema to methodNameClass.str,
                SettingActionFuncExtra.ARGS.schema to SettingActionFuncBroadcastManager.makeArgsListCon(
                    argsList
                ),
            )
        )
//        when(methodNameClass){
//            MethodNameClass.SHORT -> {
//                val firstArg = argsList.get(0)
//                BroadcastSender.normalSend(
//                    context,
//                    BroadCastIntentSchemeTerm.SETING_ACTION_FUNC.action,
//                    listOf(
//                        SettingActionFuncExtra.FUNC_NAME.schema to SettingActionFuncBroadcastManager.FuncClass.TOAST.str,
//                        SettingActionFuncExtra.METHOD_NAME.schema to  MethodNameClass.SHORT.str,
//                        SettingActionFuncExtra.ARGS.schema to SettingActionFuncBroadcastManager.makeArgsListCon(
//                            argsList
//                        ),
//                    )
//                )
//                ToastUtils.showShort(
//                    firstArg
//                )
//
//            }
//            MethodNameClass.LONG -> {
//                val firstArg = argsList.get(0)
//                ToastUtils.showLong(
//                    firstArg
//                )
//            }
//        }
    }

    enum class MethodNameClass(
        val str: String,
        val readArgsNameList: List<String>,
    ){
        SHORT("short", shortArgsNameList),
        LONG("long", longArgsNameList),
    }

    private val shortArgsNameList = listOf(
        "message"
    )


    private val longArgsNameList = listOf(
        "message"
    )
}