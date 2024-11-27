package com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver.libs

import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.ArgsChecker
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.ToastForSetting
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ToastForSettingForTerm {
    fun handle(
        methodNameStr: String,
        argsPairList: List<Pair<String, String>>
    ) {
        val methodNameClass = ToastForSetting.MethodNameClass.entries.firstOrNull {
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
        val firstArg = argsList.get(0)
        CoroutineScope(Dispatchers.Main).launch {
            when (methodNameClass) {
                ToastForSetting.MethodNameClass.SHORT -> {
                    ToastUtils.showShort(
                        firstArg
                    )

                }

                ToastForSetting.MethodNameClass.LONG -> {
                    ToastUtils.showLong(
                        firstArg
                    )
                }
            }
        }
    }
}