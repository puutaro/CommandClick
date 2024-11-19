package com.puutaro.commandclick.proccess.edit.lib

import com.puutaro.commandclick.util.map.CmdClickMap

object ImportMapMaker {
    fun comp(
        subKeyCon: String,
        firstSubKeyWithEqualPrefix: String,
    ): Map<String, String> {
        val subKeySeparator = '?'
        val subKeyConList = subKeyCon.split(subKeySeparator)
        val importPathKeyCon = subKeyConList.firstOrNull()
            ?: String()
        val endsQuote = extractEndsQuote(
            importPathKeyCon.removePrefix(firstSubKeyWithEqualPrefix),
        )
        if (
            endsQuote.isNullOrEmpty()
        ) {
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "jsAc_makeActionImportMap1.txt").absolutePath,
//                listOf(
//                    "subKeyCon: ${subKeyCon}",
//                    "importPathKeyCon: ${importPathKeyCon}",
//                ).joinToString("\n\n")
//            )
            return CmdClickMap.createMap(
                subKeyCon,
                subKeySeparator
            ).toMap()
        }
        val otherKeyCon = subKeyConList.filterIndexed { index, _ ->
            index > 0
        }.joinToString(subKeySeparator.toString())
        val compImportPathKeyCon = listOf(
            firstSubKeyWithEqualPrefix,
            endsQuote,
            importPathKeyCon.removePrefix(firstSubKeyWithEqualPrefix),
        ).joinToString(String())
        val compOtherKeyCon = listOf(
            otherKeyCon,
            endsQuote
        ).joinToString(String())
        val compQuoteSubKeyCon = listOf(
            compImportPathKeyCon,
            subKeySeparator.toString(),
            compOtherKeyCon
        ).joinToString(String())
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "jsAc_makeActionImportMap2.txt").absolutePath,
//            listOf(
//                "subKeyCon: ${subKeyCon}",
//                "importPathKeyCon: ${importPathKeyCon}",
//                "compImportPathKeyCon: ${compImportPathKeyCon}",
//                "importPathKeyPrefix: ${importPathKeyPrefix}",
//                "otherKeyCon: ${otherKeyCon}",
//                "compOtherKeyCon: ${compOtherKeyCon}",
//                "compQuoteSubKeyCon: ${compQuoteSubKeyCon}",
//                "map: ${CmdClickMap.createMap(
//                    compQuoteSubKeyCon,
//                    subKeySeparator
//                ).toMap()}"
//            ).joinToString("\n\n")
//        )
        return CmdClickMap.createMap(
            compQuoteSubKeyCon,
            subKeySeparator
        ).toMap()
    }

    private fun extractEndsQuote(
        importPathKeyCon: String,
    ): String? {
        val quoteList = listOf("`", "\"")
        quoteList.forEach {
            val isOnlyEndQuote = importPathKeyCon.endsWith(it)
                    && !importPathKeyCon.startsWith(it)
            if(
                isOnlyEndQuote
            ) return it
        }
        return null
    }
}