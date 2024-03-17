package com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra

import android.content.Context
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.shell_macro.ShellMacroHandler
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.map.CmdClickMap

object AlterConfig {
    const val alterKeyName = "alter"
    enum class IfKey(
        val key: String,
    ) {
        SHELL_IF_PATH("shellIfPath"),
        SHELL_IF_CON("shellIfCon"),
        IF_ARGS("ifArgs"),
    }


    fun getShellIfOutput(
        context: Context,
        alterMap: Map<String, String>,
        replaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor,
        ifArgsSeparator: Char,
    ): String {
        val shellIfCon = makeShellIfCon(
            context,
            alterMap,
            replaceVariableMap,
            ifArgsSeparator,
        )
//            val repValHashMap = replaceVariableMap?.let {
//                HashMap(it)
//            }
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "setValMap_shellIf.txt").absolutePath,
//            listOf(
//                "alterMap: ${alterMap}",
//                "shellIfCon: ${shellIfCon}",
//                "alterMap: ${alterMap}",
//                "shellIfCon: ${shellIfCon}",
//                "val: ${busyboxExecutor.getCmdOutput(
//                    shellIfCon,
//                )}"
//            ).joinToString("\n\n\n")
//        )
        return busyboxExecutor.getCmdOutput(
            shellIfCon,
        ).trim()
    }

    private fun makeShellIfCon(
        context: Context,
        alterMap: Map<String, String>?,
        replaceVariableMap: Map<String, String>?,
        ifArgsSeparator: Char,
    ): String {
        if(
            alterMap.isNullOrEmpty()
        ) return String()
        val shellIfCon = alterMap.get(
            IfKey.SHELL_IF_CON.key
        )
        if(
            !shellIfCon.isNullOrEmpty()
        ) return SetReplaceVariabler.execReplaceByReplaceVariables(
            shellIfCon,
            replaceVariableMap,
            String(),
            String(),
        )
        val shellPath = alterMap.get(
            IfKey.SHELL_IF_PATH.key
        ) ?: return String()
        val extraRepValMap = CmdClickMap.createMap(
            alterMap.get(
                IfKey.IF_ARGS.key
            ),
            ifArgsSeparator
        ).toMap()
        return ShellMacroHandler.makeShellCon(
            context,
            shellPath,
            replaceVariableMap,
            extraRepValMap,
        )
    }

}