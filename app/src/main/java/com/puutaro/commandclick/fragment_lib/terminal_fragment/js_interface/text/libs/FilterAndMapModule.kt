package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.text.libs

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.str.RegexTool
import java.io.File
import java.time.LocalDateTime

object FilterAndMapModule {

    const val extraMapSeparator = '|'

    val removeAndReplacePair = Pair("removeRegex", "replaceStr")

    enum class ExtraMapBaseKey(
        val key: String
    ) {
        REMOVE_REGEX(removeAndReplacePair.first),
        COMP_PREFIX("compPrefix"),
        COMP_SUFFIX("compSuffix"),
        MATCH_REGEX("matchRegex"),
        MATCH_REGEX_MATCH_TYPE("matchRegexMatchType"),
        MATCH_CONDITION("matchCondition"),
        LINES_MATCH_TYPE("linesMatchType"),
        SHELL_PATH("shellPath"),
        SHELL_ARGS("shellArgs"),
        SHELL_OUTPUT("shellOutput"),
        SHELL_FANNEL_PATH("shellFannelPath"),
    }

    enum class MatchConditionType(
        val type: String,
    ) {
        AND("and"),
        OR("or"),
    }

    enum class LinesMatchType(
        val type: String,
    ) {
        NORMAL("normal"),
        DENY("deny"),
    }

    enum class MatchRegexMatchType(
        val type: String,
    ) {
        NORMAL("normal"),
        DENY("deny"),
    }

    fun applyRemoveRegex(
        srcLine: String,
        removeRegexToReplaceKeyList: List<Pair<Regex, String>>,
        extraMap: Map<String, String>
    ): String {
        var line = srcLine
        removeRegexToReplaceKeyList.forEach {
            val removeRegex = it.first
            val replaceKey = it.second
            val replaceStr = extraMap.get(replaceKey)
                ?: String()
            line = line.replace(removeRegex, replaceStr)
        }
        return line
    }


    fun applyCompPrefix(
        srcLine: String,
        compPrefixList: List<String>,
    ): String {
        var line = srcLine
        compPrefixList.forEach {
            line = when (line.startsWith(it)) {
                true -> line
                else -> "$it${line.replaceFirstChar { it.uppercase() }}"
            }
        }
        return line
    }

    fun applyCompSuffix(
        srcLine: String,
        compSuffixList: List<String>,
    ): String {
        var line = srcLine
        compSuffixList.forEach {
            suffix ->
            line = when (line.endsWith(suffix)) {
                true -> line
                else -> "$line${suffix.replaceFirstChar { it.uppercase() }}"
            }
        }
        return line
    }

    fun makeTargetValueList(
        extraMap: Map<String, String>,
        removeRegexBaseKey: String,
    ): List<String> {
        return extraMap.filter {
                keyToValue ->
            val key = keyToValue.key
            val okPrefix = key.startsWith(removeRegexBaseKey)
            val okNumSuffix = try {
                key.removePrefix(removeRegexBaseKey).toInt()
                true
            } catch(e: Exception){
                false
            }
            okPrefix && okNumSuffix
        }.map {
                keyToValue ->
            keyToValue.value
        }
    }

    fun makeRemoveRegexToReplaceKeyPairList(
        extraMap: Map<String, String>,
        removeRegexBaseKey: String,
    ): List<Pair<Regex, String>> {
        return extraMap.filter {
                keyToValue ->
            val key = keyToValue.key
            val okPrefix = key.startsWith(removeRegexBaseKey)
            val okNumSuffix = try {
                key.removePrefix(removeRegexBaseKey).toInt()
                true
            } catch(e: Exception){
                false
            }
            okPrefix && okNumSuffix
        }.map {
                keyToValue ->
            val repSrcRegex = RegexTool.convert(
                "^${removeAndReplacePair.first}"
            )
            val replaceKey =
                keyToValue.key.replace(
                    repSrcRegex,
                    removeAndReplacePair.second
                )
            val removeRegexStr = keyToValue.value
            val removeRegex = RegexTool.convert(removeRegexStr)
            removeRegex to replaceKey
        }
    }

    object ShellResultForToList {

        private enum class ShellPreservedArgs(
            val key: String
        ){
            KEY("key"),
            VALUE("value"),
            LINE("line"),
        }

        private const val keyValueSeparator = '\t'

        fun getResultByShell(
            busyboxExecutor: BusyboxExecutor?,
            line: String,
            shellArgsMapSrc: Map<String, String>,
            shellCon: String?,
            shellOutputSrc: String?
        ): String {
            if(
                busyboxExecutor == null
            ) return line
            if (
                shellCon.isNullOrEmpty()
            ) return line
            val tsvKey = ShellPreservedArgs.KEY.key
            val tsvValueKey = ShellPreservedArgs.VALUE.key
            val tsvLineKey = ShellPreservedArgs.LINE.key
            val keyAndValue = line.split(keyValueSeparator)
            val key = keyAndValue.firstOrNull() ?: String()
            val value = keyAndValue.getOrNull(1) ?: String()
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
            val resultSrc = busyboxExecutor.getCmdOutput(
                shellCon,
                shellArgsMap
            )
            FileSystems.writeFileToDirByTimeStamp(
                File(UsePath.cmdclickDefaultAppDirPath, "jsMap").absolutePath,
                listOf(
                    "shellPreservedArgsMap: ${shellPreservedArgsMap}",
                    "shellCon: ${shellCon}",
                    "resultSrc: ${resultSrc}"
                ).joinToString("\n\n")
            )
            if (
                resultSrc.isEmpty()
            ) return String()
            return when (shellOutput.isNullOrEmpty()) {
                true -> resultSrc
                else -> shellOutput
            }
        }
    }
}