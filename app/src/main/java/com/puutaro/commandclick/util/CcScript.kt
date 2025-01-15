package com.puutaro.commandclick.util

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.str.QuoteTool
import java.io.File

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
        val parametarKey = keyValueList.first().trim()
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
        return keyValueList.filterIndexed{
            index, _ -> index >= 1
        }.joinToString(separator).let {
            QuoteTool.trimBothEdgeQuote(it)
        }
    }
}