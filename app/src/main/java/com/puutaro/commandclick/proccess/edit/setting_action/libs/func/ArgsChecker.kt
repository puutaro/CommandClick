package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

object ArgsChecker {
    fun checkArgs(
        baseArgsNameList: List<String>?,
        argsPairList: List<Pair<String, String>>
    ): Boolean {
        if(
            baseArgsNameList.isNullOrEmpty()
        ) return false
        baseArgsNameList.forEachIndexed {
                index, argName ->
            val argPair = argsPairList.getOrNull(index)
                ?: return true
            if(
                argPair.first.isEmpty()
            ) return true
        }
        return false
    }
}