package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.collections

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.collections.libs.FilterAndMapModule
import com.puutaro.commandclick.util.map.CmdClickMap

class JsToListMap(
    terminalFragment: TerminalFragment
) {

    @JavascriptInterface
    fun map(
        con: String,
        separator: String,
        extraMapCon: String,
    ): String {
        val extraMap = CmdClickMap.createMap(
            extraMapCon,
            FilterAndMapModule.extraMapSeparator,
        ).toMap().toSortedMap()
        val removeRegexToReplaceKeyList =
            FilterAndMapModule.makeRemoveRegexToReplaceKeyPairList(
                extraMap,
                FilterAndMapModule.ExtraMapBaseKey.REMOVE_REGEX.key
            )
        val compPrefixList = FilterAndMapModule.makeTargetValueList(
            extraMap,
            FilterAndMapModule.ExtraMapBaseKey.COMP_PREFIX.key
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
        val compSuffixList = FilterAndMapModule.makeTargetValueList(
            extraMap,
            FilterAndMapModule.ExtraMapBaseKey.COMP_SUFFIX.key
        )
        val mapLiesList = con.split(separator).map {
                srcLine ->
            val lineWithRemove = FilterAndMapModule.applyRemoveRegex(
                srcLine,
                removeRegexToReplaceKeyList,
                extraMap,
            )
            val lineWithCompPrefix = FilterAndMapModule.applyCompPrefix(
                lineWithRemove,
                compPrefixList
            )
            val lineWithCompSuffix = FilterAndMapModule.applyCompSuffix(
                lineWithCompPrefix,
                compSuffixList
            )
            lineWithCompSuffix
        }
        return mapLiesList.joinToString(separator)
    }

}