package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.text

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.common.variable.settings.EditSettings
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.util.tsv.TsvTool
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.io.File

class JsToMap(
    terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
    private val keyValueSeparator = '\t'
    private val extraMapSeparator = '|'
    private val shellArgsSeparator = '?'
    private val busyboxExecutor = terminalFragment.busyboxExecutor

    @JavascriptInterface
    fun getKey(line: String): String {
        return line
            .split(keyValueSeparator)
            .firstOrNull()
            ?: String()
    }

    @JavascriptInterface
    fun getValue(line: String): String {
        return line
            .split(keyValueSeparator)
            .getOrNull(1)
            ?: String()
    }


    @JavascriptInterface
    fun map(
        tsvCon: String,
        lineSeparator: String,
        fannelPath: String,
        extraMapCon: String,
    ): String {
        val twoColumnNum = 2
        val tsvKeyConList = TsvTool.filterByColumnNum(
            tsvCon.split(lineSeparator),
            twoColumnNum
        ).ifEmpty {
            return String()
        }
        val replaceVariableMap =
            SetReplaceVariabler.makeSetReplaceVariableMapFromSubFannel(
                context,
                fannelPath
            ) ?: emptyMap()
        val currentAppDirPath = CcPathTool.getMainAppDirPath(
                fannelPath
            )
        val fannelName =
            CcPathTool.getMainFannelDirPath(fannelPath).let {
                File(it).name
            }
        val extraMap = SetReplaceVariabler.execReplaceByReplaceVariables(
            extraMapCon,
            replaceVariableMap,
            currentAppDirPath,
            fannelName
        ).let {
            CmdClickMap.createMap(
                extraMapCon,
                extraMapSeparator
            ).toMap()
        }
        val shellCon =
            extraMap.get(ExtraMapKey.SHELL_PATH.key)?.let {
                ReadText(it).readText()
            }?.let {
                SetReplaceVariabler.execReplaceByReplaceVariables(
                    it,
                    replaceVariableMap,
                    currentAppDirPath,
                    fannelName
                )
            }
        if(
            shellCon.isNullOrEmpty()
        ) return String()
        val shellArgsMapSrc =
            extraMap.get(ExtraMapKey.SHELL_ARGS.key).let {
                CmdClickMap.createMap(
                    it,
                    shellArgsSeparator
                ).toMap()
            }
        val shellOutput =
            extraMap.get(ExtraMapKey.SHELL_OUTPUT.key)

        val concurrentLimit = 50
        val semaphore: Semaphore = Semaphore(concurrentLimit)
        val listSize = tsvKeyConList.size
        val channel = Channel<Pair<Int, String>>(listSize)
        val receiveTsvLineList = mutableListOf<Pair<Int, String>>()
        runBlocking {
            launch {
                tsvKeyConList.forEachIndexed {
                        index, line ->
                    semaphore.withPermit {
                        val result = getResultByShell(
                            line,
                            shellArgsMapSrc,
                            shellCon,
                            shellOutput
                        )
                        // Channelに文字列を送信
                        channel.send(
                            Pair(index, result)
                        )
                    }
                }
                channel.close()
            }

            launch {
               for (received in channel){
                    // Channelから受信
                    receiveTsvLineList.add(received)
                }
            }
        }
        receiveTsvLineList.sortBy { it.first }
        return receiveTsvLineList.map {
            it.second
        }.joinToString(lineSeparator)
    }

    private fun getResultByShell(
        line: String,
        shellArgsMapSrc: Map<String, String>,
        shellCon: String,
        shellOutputSrc: String?
    ): String {
        val tsvKey = ShellPreservedArgs.KEY.key
        val tsvValueKey = ShellPreservedArgs.VALUE.key
        val tsvLineKey = ShellPreservedArgs.LINE.key
        val key = getKey(line)
        val value = getValue(line)
        val shellPreservedArgsMap = mapOf(
            tsvKey to key,
            tsvValueKey to value,
            tsvLineKey to line,
        )
        val shellArgsMap =
            shellPreservedArgsMap + shellArgsMapSrc
        val shellOutput = shellOutputSrc?.let {
            CmdClickMap.replace(
                it,
                shellArgsMap,
            )
        }
        val shellMacro = ShellMacro.values().firstOrNull {
            it.name == shellCon
        } ?: let {
            val resultSrc = busyboxExecutor?.getCmdOutput(
                shellCon,
                shellArgsMap
            ) ?: String()
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "jsMap_${LocalDateTime.now()}.txt").absolutePath,
//                listOf(
//                    "shellPreservedArgsMap: ${shellPreservedArgsMap}",
//                    "shellCon: ${shellCon}",
//                    "resultSrc: ${resultSrc}"
//                ).joinToString("\n\n")
//            )
            if(
                resultSrc.isEmpty()
            ) return String()
            return when(shellOutput.isNullOrEmpty()){
                true -> resultSrc
                else -> shellOutput
            }
        }
        if(
            shellOutput.isNullOrEmpty()
        ) return String()
        val isOutput = when(shellMacro){
            ShellMacro.EQUAL_TWO_VALUE -> {
                EqualTwoValue.isOutput(
                    shellArgsMap,
                )
            }
        }
        return when(isOutput){
            true -> shellOutput
            else -> String()
        }
    }

    private enum class ShellPreservedArgs(
        val key: String
    ){
        KEY("key"),
        VALUE("value"),
        LINE("line"),
    }

    private enum class ExtraMapKey(
        val key: String
    ) {
        SHELL_PATH("shellPath"),
        SHELL_ARGS("shellArgs"),
        SHELL_OUTPUT("shellOutput"),
    }

    private enum class ShellMacro {
        EQUAL_TWO_VALUE,
    }
}

private object EqualTwoValue {
    private val filePrefix = EditSettings.filePrefix
    private enum class EqualTwoValueArgs(
        val arg: String
    ) {
        SRC("src"),
        DEST("dest"),
        COMPARE_TYPE("compareType"),
    }

    private enum class CompareType(
        val type: String
    ) {
        DENY("deny"),
        NORMAL("normal"),
    }

    fun isOutput(
        shellArgsMap: Map<String, String>,
    ): Boolean {
        val src = getArgsMapValue(
            shellArgsMap,
            EqualTwoValueArgs.SRC.arg
        ) ?: return false
        val dest = getArgsMapValue(
            shellArgsMap,
            EqualTwoValueArgs.DEST.arg
        ) ?: return false
        val compareTypeStr = shellArgsMap.get(
            EqualTwoValueArgs.COMPARE_TYPE.arg
        )
        val compareType = CompareType.values().firstOrNull {
            it.type == compareTypeStr
        } ?: CompareType.NORMAL
        return when(compareType){
            CompareType.NORMAL ->
                src == dest
            CompareType.DENY ->
                src != dest
        }
    }

    private fun getArgsMapValue(
        shellArgsMap: Map<String, String>,
        compareValueArg: String,
    ): String? {
        val valueSrc = shellArgsMap.get(
            compareValueArg
        ) ?: return null
        val value = QuoteTool.trimBothEdgeQuote(
            valueSrc
        ).let {
            CmdClickMap.replace(
                it,
                shellArgsMap,
            )
        }
        return when(
            value.startsWith(filePrefix)
        ){
            true -> value.removePrefix(filePrefix).let {
                ReadText(it).readText()
            }
            else -> value
        }

    }
}