package com.puutaro.commandclick.util.map

import com.puutaro.commandclick.util.CcScript
import com.puutaro.commandclick.util.QuoteTool

object CmdClickMap {
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
         return mapEntryStrList.map {
             CcScript.makeKeyValuePairFromSeparatedString(
                 it,
                 "="
             )
         }
    }

    fun replace(
        targetCon: String,
        repMap: Map<String, String>?
    ): String {
        if(
            repMap.isNullOrEmpty()
        ) return targetCon
        var repCon = targetCon
        repMap.forEach {
            repCon = repCon.replace(
                "\${${it.key}}",
                it.value
            )
        }
        return repCon
    }

    fun replaceHolder(
        targetCon: String,
        repMap: Map<String, String>?
    ): String {
        val holderNameToValueMap =
            updateRepValMapByHolderRegex(
                targetCon,
                repMap
        )
        var repCon = targetCon
        holderNameToValueMap.forEach {
            val holderMark = it.key
            val repValue = it.value
            repCon = repCon.replace(
                holderMark,
                repValue
            )
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
            "\\{\\{ [a-zA-Z0-9_-]{1,}:*.*? \\}\\}"
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