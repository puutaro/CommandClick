package com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra

import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor

object ShellTool {

    val shellConReplaceMark = "\${TARGET_CON}"
    private val repMarkStrPairList = listOf(
        "\"" to "cmdclickDoubleQuote"
    )


    fun filter(
        filterCon: String,
        busyboxExecutor: BusyboxExecutor?,
        shellCon: String,
        extraArgsMap: Map<String, String>
    ): String {
        return filterCon.let {
            loopReplace(it)
        }.let{
            if(
                busyboxExecutor == null
                || shellCon.isEmpty()
            ) return@let it
            busyboxExecutor.getCmdOutput(
                shellCon.replace(shellConReplaceMark, it),
                HashMap(extraArgsMap)
            )
        }.let {
            loopReplace(
                it,
                true
            )
        }.split("\n").filter {
            it.trim().isNotEmpty()
        }.joinToString("\n")
    }

    private fun loopReplace(
        shellCon: String,
        isReverse: Boolean = false
    ): String {
        var repCon = shellCon
        when(isReverse){
            false -> repMarkStrPairList.forEach {
                repCon = repCon.replace(
                    it.first,
                    it.second
                )
            }
            else -> repMarkStrPairList.forEach {
                repCon = repCon.replace(
                    it.second,
                    it.first
                )
            }
        }
        return repCon
    }
}