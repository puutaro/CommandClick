package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import java.time.LocalDateTime

object LocalDatetimeForSetting {
    fun handle(
        methodNameStr: String,
        argsPairList: List<Pair<String, String>>
    ): String? {
        val methodNameClass = MethodNameClass.entries.firstOrNull {
            it.str == methodNameStr
        } ?: return null
        val isErr = ArgsChecker.checkArgs(
            methodNameClass.argsNameList,
            argsPairList
        )
        if(isErr) return null
        return when(methodNameClass){
            MethodNameClass.NOW -> {
                LocalDateTime.now().toString()
            }

        }
    }

    private enum class MethodNameClass(
        val str: String,
        val argsNameList: List<String>?,
    ){
        NOW("now", null),
    }
}