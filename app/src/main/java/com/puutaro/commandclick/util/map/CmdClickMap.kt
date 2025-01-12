package com.puutaro.commandclick.util.map

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionKeyManager
import com.puutaro.commandclick.util.CcScript
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.str.BackslashTool
import com.puutaro.commandclick.util.str.QuoteTool
import java.io.File

object CmdClickMap {

    private val backslashToEscapeStr = Pair("\\", "CMDCLICL_BACKKSLASH_ESCAPE_STRING")

    object MapReplacer {
        private const val separator = '廳'

        fun replaceToPairList(
            mainSubKeyMapSrc: Map<String, String>,
            replaceMap: Map<String, String?>?,
        ): Map<String, String> {
            return replace(
                joinToStr(mainSubKeyMapSrc),
                replaceMap
            ).let {
                strToPairList(
                    it
                ).toMap()
            }
        }
        private fun joinToStr(mapArg: Map<String, String>): String {
            return mapArg.entries
                .joinToString(
                    separator.toString()
                )
        }

        private fun strToPairList(joinedMapCon: String): List<Pair<String, String>> {
            return createMap(
                joinedMapCon,
                separator
            )
        }
    }

     fun createMap(
        mapEntryStr: String?,
        separator: Char
    ):List<Pair<String, String>> {
         if(
             mapEntryStr.isNullOrEmpty()
         ) return emptyList()
         val normalSplitSeparatorList = listOf('\n', '\t')
        val mapEntryStrList = when(
            normalSplitSeparatorList.contains(separator)
        ) {
            true -> mapEntryStr.split(separator)
            else -> QuoteTool.splitBySurroundedIgnore(
                mapEntryStr,
                separator,
            )
        }
//         FileSystems.updateFile(
//             File(UsePath.cmdclickDefaultAppDirPath, "limport_createMap.txt").absolutePath,
//             listOf(
////                                    "keyToSubKeyCon: ${keyToSubKeyCon}",
////                                    "keyToSubKeyConList: ${keyToSubKeyConList}",
////                                    "subKeyCon: ${subKeyCon}",
//                 "mapEntryStrList: ${mapEntryStrList}",
//                 "separator: ${separator}",
//                 "makeKeyValuePairFromSeparatedString: ${mapEntryStrList.map {
//                     CcScript.makeKeyValuePairFromSeparatedString(
//                         it,
//                         "="
//                     )}}",
//             ).joinToString("\n") + "\n\n==========\n\n"
//         )
         return mapEntryStrList.map {
             CcScript.makeKeyValuePairFromSeparatedString(
                 it,
                 "="
             )
         }
    }

    fun recreateMapWithoutQuoteInKey(
        srcMap: Map<String, String>,
    ): Map<String, String> {

        val checkQuote = listOf(
            "`",
            "\"",
            "'"
        )
        val mapKeyList = srcMap.keys
        val irregularQuoteList = checkQuote.filter {
            quote ->
            mapKeyList.any {
                it.endsWith(quote)
            }
        }
        return srcMap.map {
            val cleanKey =  removeIrregularQuote(
                irregularQuoteList,
                it.key
            )
            val cleanValue = QuoteTool.compOneSideQuote(
                it.value
            ) ?: String()
            cleanKey to cleanValue
        }.toMap()
    }

    private fun removeIrregularQuote(
        irregularQuoteList: List<String>,
        targetCon: String,
    ): String {
        var updateCon = targetCon
        irregularQuoteList.forEach {
            updateCon = updateCon.replace(
                it,
                String()
            )
        }
        return updateCon

    }

    fun createMapFromTsv(
        mapEntryStr: String?,
        separator: Char
    ):List<Pair<String, String>> {
        if(
            mapEntryStr.isNullOrEmpty()
        ) return emptyList()
        val normalSplitSeparatorList = listOf('\n', '\t')
        val mapEntryStrList = when(
            normalSplitSeparatorList.contains(separator)
        ) {
            true -> mapEntryStr.split(separator)
            else -> QuoteTool.splitBySurroundedIgnore(
                mapEntryStr,
                separator,
            )
        }
        return mapEntryStrList.map {
            CcScript.makeKeyValuePairFromSeparatedString(
                it,
                "\t"
            )
        }
    }

    fun getFirst(
        pairList: List<Pair<String, String>>,
        keyName: String,
    ): String? {
        return pairList.firstOrNull {
            val mainKeyName = it.first
            mainKeyName == keyName
        }?.second
    }

    fun replace(
        targetCon: String,
        repMap: Map<String, String?>?
    ): String {
        if(
            repMap.isNullOrEmpty()
        ) return targetCon
        var repCon = targetCon
        repMap.forEach {
            val replaceStr =
                it.value ?: String()
            val tempRepCon = try {
                repCon.replace(
                    Regex("""([^\\])[$][{]${it.key}[}]"""),
                    "$1${replaceStr}"
                ).replace(
                    Regex("""^[$][{]${it.key}[}]"""),
                    replaceStr
                )
            } catch (e: Exception){
                repCon
            }
            repCon = tempRepCon
        }
        return repCon
    }

    fun replaceByBackslashToNormal(
        targetCon: String,
        repMapSrc: Map<String, String?>?
    ): String {
        val repMap = BackSlashSaveMap.make(
            repMapSrc,
        )
        return replace(
            targetCon,
            repMap
        ).let {
            val toNormalCon = BackslashTool.toNormal(it).replace(
                backslashToEscapeStr.second,
                backslashToEscapeStr.first,
            )
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "lreplace_boTonormao_after.txt").absolutePath,
//                listOf(
//                    "targetCon: ${targetCon}",
//                    "replace: ${it}",
//                    "tonormal: ${tonormal}",
//
//                ).joinToString("\n\n") + "\n=========\n\n"
//            )
            toNormalCon
        }
    }

    fun replaceByBackslashToNormalByEscape(
        targetCon: String,
        repMapSrc: Map<String, String?>?
    ): String {
        val repMap = BackSlashSaveMap.make(
            repMapSrc,
        )
        return replace(
            targetCon,
            repMap
        ).let {
            val toNormalCon =
                BackslashTool.toNormalByEscape(it)
                    .replace(
                    backslashToEscapeStr.second,
                    backslashToEscapeStr.first,
                )
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "lreplace_boTonormao_after_escape.txt").absolutePath,
//                listOf(
//                    "targetCon: ${targetCon}",
//                    "replace: ${it}",
//                    "tonormal: ${toNormalCon}",
//                ).joinToString("\n\n") + "\n=========\n\n"
//            )
            toNormalCon
        }
    }

    private object BackSlashSaveMap {
        fun make(
            repMapSrc: Map<String, String?>?,
        ): Map<String, String?>? {
            return repMapSrc?.map {
                it.key to it.value?.replace(
                    backslashToEscapeStr.first,
                    backslashToEscapeStr.second
                )
            }?.toMap()
        }


        private fun restore(
            targetCon: String,
            repMap: Map<String, String?>?,
        ): String {
            return replace(
                targetCon,
                repMap
            ).let {
                val toNormalCon = BackslashTool.toNormalByEscape(it).replace(
                    backslashToEscapeStr.second,
                    backslashToEscapeStr.first,
                )
                toNormalCon
            }
        }
    }
    fun replaceByAtVar(
        targetCon: String,
        repMap: Map<String, String>?
    ): String {
        if(
            repMap.isNullOrEmpty()
        ) return targetCon
        var repCon = targetCon
        repMap.forEach {
            repCon = repCon.replace(
                "@{${it.key}}",
                it.value
            )
        }
        return repCon
    }

    fun replaceHolderForJsAction(
        targetCon: String,
        repMap: Map<String, String>?
    ): String {
        val holderNameToValueMap =
            updateRepValMapByHolderRegex(
                targetCon,
                repMap
        )
        var repCon = targetCon
        val noQuotePrefix = JsActionKeyManager.noQuotePrefix
        holderNameToValueMap.forEach {
            val holderMark = it.key
            val repValueSrc = it.value
            val isNoQuote = repValueSrc.startsWith(noQuotePrefix)
            val repValue = repValueSrc.removePrefix(noQuotePrefix)
            when(isNoQuote) {
                true -> {
                    repCon = repCon.replace(
                        "`${holderMark}`",
                        repValue
                    )
//                    FileSystems.updateFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "repHolder.txt").absolutePath,
//                        listOf(
//                            "repCon: ${repCon}",
//                            "holderMark: ${holderMark}",
//                            "repValueSrc: ${repValueSrc}",
//                            "isNoQuote: ${isNoQuote}",
//                            "repValue: ${repValue}",
//                        ).joinToString("\n\n") + "\n---\n"
//                    )
                }
                else -> repCon = repCon.replace(
                    holderMark,
                    repValue
                )
            }
        }
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "rep.txt").absolutePath,
//            listOf(
//                "targetCon: ${targetCon}",
//                "repMap: ${repMap}",
//                "holderNameToValueMap: ${holderNameToValueMap}",
//                "repCon: ${repCon}",
//            ).joinToString("\n\n")
//        )
        return repCon
    }

    fun concatRepValMap(
        jsRepValMapBeforeConcat: Map<String, String>?,
        extraRepValMap: Map<String, String>?,
    ): Map<String, String>? {
        return when(true){
            (jsRepValMapBeforeConcat.isNullOrEmpty()
                    && extraRepValMap.isNullOrEmpty())
            -> null
            (!jsRepValMapBeforeConcat.isNullOrEmpty()
                    && extraRepValMap.isNullOrEmpty())
            -> jsRepValMapBeforeConcat
            (jsRepValMapBeforeConcat.isNullOrEmpty()
                    && !extraRepValMap.isNullOrEmpty())
            -> extraRepValMap
            (!jsRepValMapBeforeConcat.isNullOrEmpty()
                    && !extraRepValMap.isNullOrEmpty())
            -> jsRepValMapBeforeConcat + extraRepValMap
            else -> null
        }
    }

    private fun updateRepValMapByHolderRegex(
        targetCon: String,
        repMap: Map<String, String>?
    ): Map<String, String> {
        val valNameDefaValueRegex =
            "\\{\\{ [A-Z0-9_-]{1,}[:]{0,1}.*? \\}\\}"
        val valNameDefaValueResult =
            Regex(valNameDefaValueRegex)
                .findAll(targetCon)
        return valNameDefaValueResult.map {
            val hitHolderMark = it.value
            val holderNameAndDefaultValueList =
                hitHolderMark
                    .trim()
                    .removePrefix("{{")
                    .removeSuffix("}}")
                    .trim()
                    .split(":")
            val valName =
                holderNameAndDefaultValueList.firstOrNull()
                    ?: String()
            val defaultValue =
                holderNameAndDefaultValueList.filterIndexed {
                        index, s -> index > 0
                }.joinToString(":")
                    .trim()
            val repValue = repMap?.get(valName)
                ?: defaultValue
            hitHolderMark to repValue
        }.toMap().filterKeys { it.isNotEmpty() }
    }
}