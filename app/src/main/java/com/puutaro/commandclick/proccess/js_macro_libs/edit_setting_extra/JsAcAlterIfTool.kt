package com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra

import android.content.Context
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.shell_macro.ShellMacroHandler
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.map.CmdClickMap

object AlterIfShellTool {
    const val alterKeyName = "alter"
    enum class IfShellKey(
        val key: String,
    ) {
        SHELL_IF_PATH_SHELL("shellIfPath"),
        SHELL_IF_CON_SHELL("shellIfCon"),
        MACRO_IF("macroIf"),
//        IF_GET("ifGet"),
        IF_SHELL_ARGS("ifArgs"),
//        SAVE_RESULT("saveResult")
    }


    private val shellIfConKey = IfShellKey.SHELL_IF_CON_SHELL.key
    private val shellIfPathKey = IfShellKey.SHELL_IF_PATH_SHELL.key
    private val macroIfKey = IfShellKey.MACRO_IF.key
    private val shellIfArgsKey = IfShellKey.IF_SHELL_ARGS.key
    private val ifKeyList = IfShellKey.values().map { it.key }.filter {
        val isNotShellIfArgsKey =
            it != shellIfArgsKey
        isNotShellIfArgsKey
    }


    fun getShellIfOutput(
        context: Context,
        busyboxExecutor: BusyboxExecutor,
        alterKeyValuePairList: List<Pair<String, String>>?,
        replaceVariableMap: Map<String, String>?,
        ifArgsSeparator: Char,
    ): String {
        if(
            alterKeyValuePairList.isNullOrEmpty()
        ) return String()

        var shellIfOutput = String()
        run loop@{
            alterKeyValuePairList.forEachIndexed {
                    currentIndex, alterPair ->
                val alterKey = alterPair.first
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "setVal_loop.txt").absolutePath,
//                    listOf(
//                        "alterKeyValuePairList: ${alterKeyValuePairList}",
//                        "alterKey: $alterKey",
//                        "currentIndex ${currentIndex}",
//                        "alterPair: ${alterPair}",
//                    ).joinToString("\n\n\n")
//                )
                shellIfOutput = getFromIfShellCon(
                    busyboxExecutor,
                    alterKey,
                    alterPair,
                    replaceVariableMap
                )
                if (shellIfOutput.isNotEmpty()) {
                    return shellIfOutput
                }
                shellIfOutput = getFromIfShellPath(
                    context,
                    busyboxExecutor,
                    alterKeyValuePairList,
                    currentIndex,
                    alterKey,
                    alterPair,
                    replaceVariableMap,
                    ifArgsSeparator,
                )
                if (shellIfOutput.isNotEmpty()) {
                    return shellIfOutput
                }
            }
        }
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "setValMap_shellIf.txt").absolutePath,
//            listOf(
//                "alterKeyValuePairList: ${alterKeyValuePairList}",
//                "shellIfOutput: ${shellIfOutput}",
//                "val: ${shellIfOutput}"
//            ).joinToString("\n\n\n")
//        )
        return String()
    }

    private fun getFromIfShellCon(
        busyboxExecutor: BusyboxExecutor,
        alterKey: String,
        alterPair: Pair<String, String>,
        replaceVariableMap: Map<String, String>?
    ): String {
        if (
            alterKey != shellIfConKey
        ) return String()
        val shellIfCon = alterPair.second
        val shellCon = SetReplaceVariabler.execReplaceByReplaceVariables(
            shellIfCon,
            replaceVariableMap,
            String(),
            String(),
        )
       return busyboxExecutor.getCmdOutput(
            shellCon,
        ).trim()
    }

    private fun getFromIfMacro(
        context: Context,
        alterKeyValuePairList: List<Pair<String, String>>,
        currentIndex: Int,
        alterKey: String,
        alterPair: Pair<String, String>,
        replaceVariableMap: Map<String, String>?,
        ifArgsSeparator: Char,
    ): String {
        if (
            alterKey != macroIfKey
        ) return String()
        val shellIfPath = alterPair.second
        val currentAlterIfMap = makeCurrentShellIfMap(
            alterKeyValuePairList,
            currentIndex,
        )
        val extraRepValMap =CmdClickMap.createMap(
            currentAlterIfMap.get(
                shellIfArgsKey
            ),
            ifArgsSeparator
        ).toMap()
        val shellCon = ShellMacroHandler.makeShellCon(
            context,
            shellIfPath,
            replaceVariableMap,
            extraRepValMap,
        )
        return
    }

    private fun getFromIfShellPath(
        context: Context,
        busyboxExecutor: BusyboxExecutor,
        alterKeyValuePairList: List<Pair<String, String>>,
        currentIndex: Int,
        alterKey: String,
        alterPair: Pair<String, String>,
        replaceVariableMap: Map<String, String>?,
        ifArgsSeparator: Char,
    ): String {
        if (
            alterKey != shellIfPathKey
        ) return String()
        val shellIfPath = alterPair.second
        val currentAlterIfMap = makeCurrentShellIfMap(
            alterKeyValuePairList,
            currentIndex,
        )
        val extraRepValMap =CmdClickMap.createMap(
            currentAlterIfMap.get(
                shellIfArgsKey
            ),
            ifArgsSeparator
        ).toMap()
        val shellCon = ShellMacroHandler.makeShellCon(
            context,
            shellIfPath,
            replaceVariableMap,
            extraRepValMap,
        )
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "setValMap_getFromIfShellPath.txt").absolutePath,
//            listOf(
//                "alterKeyValuePairList: ${alterKeyValuePairList}",
//                "currentIndex: ${currentIndex}",
//                "alterKey: ${alterKey}",
//                "shellIfPath: ${shellIfPath}",
//                "currentAlterIfMap: ${currentAlterIfMap}",
//                "extraRepValMap: ${extraRepValMap}",
//                "shellCon: ${shellCon}",
//                "ouput: ${busyboxExecutor.getCmdOutput(
//                    shellCon,
//                ).trim()}",
//            ).joinToString("\n\n\n")
//        )
        return busyboxExecutor.getCmdOutput(
            shellCon,
        ).trim()
    }

    private fun makeCurrentShellIfMap(
        alterKeyValuePairList: List<Pair<String, String>>,
        currentIndex: Int,
    ): Map<String, String> {
        val forwardAlterKeyValuePairList = alterKeyValuePairList.filterIndexed {
                innerIndex, _ ->
            innerIndex > currentIndex
        }
        val nextIfIndex = forwardAlterKeyValuePairList.indexOfFirst {
            val innerAlterKey = it.first
            val isIfKey = ifKeyList.contains(innerAlterKey)
            isIfKey
        }
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "setValMap_makeCurrentShellIfMap.txt").absolutePath,
//            listOf(
//                "alterKeyValuePairList: ${alterKeyValuePairList}",
//                "currentIndex: ${currentIndex}",
//                "forwardAlterKeyValuePairList: ${forwardAlterKeyValuePairList}",
//                "nextIfIndex: ${nextIfIndex}",
//                "currentAlterIfMap: ${forwardAlterKeyValuePairList.filterIndexed {
//                        innerAlterIndex, _ ->
//                    if(
//                        nextIfIndex == -1
//                    ) return@filterIndexed true
//                    innerAlterIndex < nextIfIndex
//                }.toMap()}",
//            ).joinToString("\n\n\n")
//        )
        return forwardAlterKeyValuePairList.filterIndexed {
                innerAlterIndex, _ ->
            if(
                nextIfIndex == -1
            ) return@filterIndexed true
            innerAlterIndex < nextIfIndex
        }.toMap()
    }
}