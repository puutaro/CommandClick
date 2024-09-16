package com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra

import android.content.Context
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.shell_macro.ShellMacroHandler
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.map.CmdClickMap

object JsAcAlterIfTool {
    const val alterKeyName = "alter"
    enum class IfShellKey(
        val key: String,
    ) {
        SHELL_IF_PATH_SHELL("shellIfPath"),
        SHELL_IF_CON_SHELL("shellIfCon"),
        IF_ARGS("ifArgs"),
    }


    private val shellIfConKey = IfShellKey.SHELL_IF_CON_SHELL.key
    private val shellIfPathKey = IfShellKey.SHELL_IF_PATH_SHELL.key
    private val ifArgsKey = IfShellKey.IF_ARGS.key
    private val ifKeyList = IfShellKey.values().map { it.key }.filter {
        val isNotShellIfArgsKey =
            it != ifArgsKey
        isNotShellIfArgsKey
    }


    fun getIfOutput(
        context: Context?,
        busyboxExecutor: BusyboxExecutor,
        alterKeyValuePairList: List<Pair<String, String>>?,
        replaceVariableMap: Map<String, String>?,
        ifArgsSeparator: Char,
    ): String {
        if(
            alterKeyValuePairList.isNullOrEmpty()
        ) return String()

        run loop@{
            alterKeyValuePairList.forEachIndexed {
                    currentIndex, alterPair ->
                val alterKey = alterPair.first
                val shellIfConOutput = getFromIfShellCon(
                    busyboxExecutor,
                    alterKey,
                    alterPair,
                    replaceVariableMap
                )
                if (
                    shellIfConOutput.isNotEmpty()
                ) return shellIfConOutput
                val shellIfPathOutput = getFromIfShellPath(
                    context,
                    busyboxExecutor,
                    alterKeyValuePairList,
                    currentIndex,
                    alterKey,
                    alterPair,
                    replaceVariableMap,
                    ifArgsSeparator,
                )
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "setValMap_shellIf.txt").absolutePath,
//                    listOf(
//                        "alterKeyValuePairList: ${alterKeyValuePairList}",
//                        "shellIfPathOutput: ${shellIfPathOutput}",
//                    ).joinToString("\n\n\n")
//                )
                if (
                    shellIfPathOutput.isNotEmpty()
                ) return shellIfPathOutput
            }
        }
        return String()
    }


    fun execAlter(
        currentConfigValueList: List<String>,
        alterKeyValuePairList: List<Pair<String, String>>,
        shellIfOutput: String,
        separator: Char,
    ): String {
        val alterMapSrc = alterKeyValuePairList.toMap()
        val alterMap =
            alterMapSrc +
                    CmdClickMap.createMap(
                        shellIfOutput,
                        separator
                    )
        val alterMapKeyList = (alterMap.keys + setOf(alterKeyName)).filter {
            it.isNotEmpty()
        }
        val currentConfigValueListWithAlterKeyRemove =
            currentConfigValueList.map {
                    configValue ->
                val alterKey = alterMapKeyList.firstOrNull {
                    configValue.startsWith("${it}=")
                }
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "setValMap_exec_alter.txt").absolutePath,
//                    listOf(
//                        "configValue: ${configValue}",
//                        "alterMapKeyList: ${alterMapKeyList}",
//                        "alterKey: ${alterKey}",
//                    ).joinToString("\n\n-------\n") + "^^^^^^"
//                )
                if(
                    alterKey.isNullOrEmpty()
                ) return@map configValue
                String()
            }.filter{
                it.trim().isNotEmpty()
            }.joinToString(separator.toString())
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "setValMap_currentConfigValueListWithAlterKeyRemove.txt").absolutePath,
//            listOf(
//                "currentConfigValueListWithAlterKeyRemove: ${currentConfigValueListWithAlterKeyRemove}",
//            ).joinToString("\n\n-------\n")+ "^^^^^^"
//        )
        return listOf(
            currentConfigValueListWithAlterKeyRemove,
        ).joinToString(separator.toString()) + shellIfOutput
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
//            String(),
            String(),
        )
       return busyboxExecutor.getCmdOutput(
            shellCon,
        ).trim()
    }

    private fun getFromIfShellPath(
        context: Context?,
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
        val extraRepValMap = CmdClickMap.createMap(
            currentAlterIfMap.get(
                ifArgsKey
            ),
            ifArgsSeparator
        ).toMap()
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "jsAccurrentAlterIfMap.txt").absolutePath,
//            listOf(
//                "currentAlterIfMap: ${currentAlterIfMap}",
//                "extraRepValMap: ${extraRepValMap}",
//            ).joinToString("\n\n")
//        )
        return ShellMacroHandler.handle(
            context,
            busyboxExecutor,
            shellIfPath,
            replaceVariableMap,
            extraRepValMap,
        )
//        val shellCon = ShellMacroHandler.makeShellCon(
//            context,
//            shellIfPath,
//            replaceVariableMap,
//            extraRepValMap,
//        )
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
//        return busyboxExecutor.getCmdOutput(
//            shellCon,
//        ).trim()
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