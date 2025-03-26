package com.puutaro.commandclick.util

import com.puutaro.commandclick.util.str.AltRegexTool
import com.puutaro.commandclick.util.str.QuoteTool

object CcScript {

    fun makeKeyValuePairFromSeparatedString(
        keyValueString: String,
        separator: String
    ): Pair<String, String>{
        val keyValueList = keyValueString
            .split(separator)
        val keyValueListSize = keyValueList.size
        if(
            keyValueListSize < 2
        ) return String() to String()
        val parametarKey = AltRegexTool.trim(keyValueList.first())
        val parameterValue = getValueFromSeparatedList(
            keyValueList,
            separator
        )
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "lreplace_makeKeyValuePairFromSeparatedString.txt").absolutePath,
//            listOf(
//                "keyValueString: $keyValueString",
//                "keyValueList: $keyValueList",
//                "parameterValue: $parameterValue",
//            ).joinToString("\n") + "\n\n==========\n\n"
//        )
        return parametarKey to parameterValue
    }

    private fun getValueFromSeparatedList(
        keyValueList: List<String>,
        separator: String,
    ): String {
        if (keyValueList.size <= 1) return String()

        val result = StringBuilder(
            keyValueList.size * (keyValueList[0].length + separator.length)
        )
        for (i in 1 until keyValueList.size) {
            if (i > 1) result.append(separator)
            result.append(keyValueList[i])
        }

        return result.toString().let {
            QuoteTool.trimBothEdgeQuote(it)
        }
    }

    private fun getValueFromSeparatedListBk(
        keyValueList: List<String>,
        separator: String,
    ): String {
        return keyValueList.filterIndexed{
            index, _ -> index >= 1
        }.joinToString(separator).let {
            QuoteTool.trimBothEdgeQuote(it)
        }
    }
}