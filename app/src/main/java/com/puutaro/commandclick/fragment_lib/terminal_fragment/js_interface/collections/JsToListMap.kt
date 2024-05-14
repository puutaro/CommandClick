package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.collections

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.map.CmdClickMap
import java.io.File

class JsToListMap(
    terminalFragment: TerminalFragment
) {

    private val extraMapSeparator = '|'

    private enum class ExtraMapBaseKey(
        val key: String
    ) {
        REMOVE_REGEX("removeRegex"),
        COMP_PREFIX("compPrefix"),
        COMP_SUFFIX("compSuffix"),
    }

    @JavascriptInterface
    fun map(
        con: String,
        separator: String,
        extraMapCon: String,
    ): String {
        val extraMap = CmdClickMap.createMap(
            extraMapCon,
            extraMapSeparator,
        ).toMap().toSortedMap()
        val removeRegexList = makeTargetValueList(
            extraMap,
            ExtraMapBaseKey.REMOVE_REGEX.key
        ).map {
            Regex(it)
        }
        val compPrefixList = makeTargetValueList(
            extraMap,
            ExtraMapBaseKey.COMP_PREFIX.key
        )
//       FileSystems.writeFile(
//           File(UsePath.cmdclickDefaultAppDirPath, "jsToListMap.txt").absolutePath,
//           listOf(
//               "removRegexList: ${makeTargetValueList(
//                   extraMap,
//                   ExtraMapBaseKey.REMOVE_REGEX.key
//               )}",
//               "compPrefixList: ${makeTargetValueList(
//                   extraMap,
//                   ExtraMapBaseKey.COMP_PREFIX.key
//               )}"
//           ).joinToString("\n\n\n")
//       )
        val compSuffixList = makeTargetValueList(
            extraMap,
            ExtraMapBaseKey.COMP_SUFFIX.key
        )
        val mapLiesList = con.split(separator).map {
                srcLine ->
            val lineWithRemove = applyRemoveRegex(
                srcLine,
                removeRegexList,
            )
            val lineWithCompPrefix = applyCompPrefix(
                lineWithRemove,
                compPrefixList
            )
            val lineWithCompSuffix = applyCompSuffix(
                lineWithCompPrefix,
                compSuffixList
            )
            lineWithCompSuffix
        }
        return mapLiesList.joinToString(separator)
    }


    private fun applyRemoveRegex(
        srcLine: String,
        removeRegexList: List<Regex>,
    ): String {
        var line = srcLine
        removeRegexList.forEach {
            line = line.replace(it, String())
        }
        return line
    }


    private fun applyCompPrefix(
        srcLine: String,
        compPrefixList: List<String>,
    ): String {
        var line = srcLine
        compPrefixList.forEach {
            line = when (line.startsWith(it)) {
                true -> line
                else -> "$it$line"
            }
        }
        return line
    }

    private fun applyCompSuffix(
        srcLine: String,
        compSuffixList: List<String>,
    ): String {
        var line = srcLine
        compSuffixList.forEach {
            line = when (line.endsWith(it)) {
                true -> line
                else -> "$it$line"
            }
        }
        return line
    }

    private fun makeTargetValueList(
        extraMap: Map<String, String>,
        removeRegexBaseKey: String,
    ): List<String> {
        return extraMap.filter { keyToValue ->
            val key = keyToValue.key
            val okPrefix = key.startsWith(removeRegexBaseKey)
            val okNumSuffix = try {
                key.removePrefix(removeRegexBaseKey)
                true
            } catch (e: Exception) {
                false
            }
            okPrefix && okNumSuffix
        }.map { keyToValue ->
            keyToValue.value
        }
    }
}