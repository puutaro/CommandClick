package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import com.blankj.utilcode.util.ToastUtils

object ToastForSetting {

    fun handle(
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
        when(methodNameClass){
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

    private enum class MethodNameClass(
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