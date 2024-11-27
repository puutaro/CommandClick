package com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver

import android.content.Intent
import com.puutaro.commandclick.common.variable.broadcast.SettingActionFuncExtra
import com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver.libs.ToastForSettingForTerm
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.ArgsChecker

object SettingActionFuncBroadcastManager {

    private const val argsSeparator = "SETTING_ACTION_ARGS_SEPARATOR"

    enum class FuncClass(
        val str: String,
        val argsList: List<String>
    ){
        TOAST("toast", listOf("message"))
    }

    fun handle(
        intent: Intent,
    ){
        val funcClassStr = intent.getStringExtra(
            SettingActionFuncExtra.FUNC_NAME.schema
        )
        if(
            funcClassStr.isNullOrEmpty()
        ) return
        val funcClass = FuncClass.entries.firstOrNull {
            it.str == funcClassStr
        } ?: return
        val methodName = intent.getStringExtra(
            SettingActionFuncExtra.METHOD_NAME.schema
        )
        if(
            methodName.isNullOrEmpty()
        ) return
        val argsPairList = intent.getStringExtra(
            SettingActionFuncExtra.ARGS.schema
        )?.let {
            makeArgsPairList(it)
        } ?: emptyList()
        val isErr = ArgsChecker.checkArgs(
            funcClass.argsList,
            argsPairList
        )
        if(isErr) return
        when(funcClass){
            FuncClass.TOAST -> {
                ToastForSettingForTerm.handle(
                    methodName,
                    argsPairList
                )
            }
        }

    }

    private fun makeArgsPairList(
        argsCon: String?
    ): List<Pair<String, String>>? {
        val dummyKey = "dummy"
        return argsCon?.split(argsSeparator)?.mapIndexed {
            index, argName ->
            "${dummyKey}${index}" to argName
        }
    }

    fun makeArgsListCon(
        argsList: List<String>?
    ): String {
        if(
            argsList.isNullOrEmpty()
            ) return String()
        return argsList.joinToString(argsSeparator)
    }
}

