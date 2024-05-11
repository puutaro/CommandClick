
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.util.CcScript
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import java.io.File

object TsvImportManager {

    const val tsvImportPreWord = "tsvimport"
    const val tsvImportUsePhrase = "use"
    private val tsvImportRegexStr =
        "\n[ \t]*${tsvImportPreWord}[^\n]+(\n[ \t]*${tsvImportUsePhrase}[ \t]*\\([^)]*\\))*"
    val tsvImportRegex = tsvImportRegexStr.toRegex()
    const val changePhrase = "=>"

    fun concatRepValWithTsvImport(
        scriptPath: String,
        jsList: List<String>,
        setReplaceVariableCompleteMap: Map<String, String>? = null
    ): Map<String, String>? {
        val jsFileObj = File(scriptPath)
        if(!jsFileObj.isFile) return setReplaceVariableCompleteMap
        val recentAppDirPath = jsFileObj.parent
            ?: return setReplaceVariableCompleteMap
        val scriptFileName = jsFileObj.name
        val jsConForTsv = SetReplaceVariabler.execReplaceByReplaceVariables(
            trimJsConForTsv(jsList),
            setReplaceVariableCompleteMap,
            recentAppDirPath,
            scriptFileName
        )
        val result = tsvImportRegex.findAll("\n$jsConForTsv")

        val tsvKeyValueMap = makeTsvKeyValueMap(
            result,
            setReplaceVariableCompleteMap,
            recentAppDirPath,
            scriptFileName
        )
        return when(setReplaceVariableCompleteMap.isNullOrEmpty()){
            true -> tsvKeyValueMap
            else ->  setReplaceVariableCompleteMap + tsvKeyValueMap
        }
    }

    fun concatRepValMapWithTsvImportFromContents(
        jsConForTsv: String,
        setReplaceVariableCompleteMap: Map<String, String>? = null
    ): Map<String, String> {
        val result = tsvImportRegex.findAll("\n$jsConForTsv")
        val tsvKeyValueMap = makeTsvKeyValueMap(
            result,
            setReplaceVariableCompleteMap,
            String(),
            String()
        )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "tsvimportRepVal.txt").absolutePath,
//            listOf(
//                "jsConForTsv: ${jsConForTsv}",
//                "tsvKeyValueMap: ${tsvKeyValueMap}",
//            ).joinToString("\n\n")
//        )
        return when(setReplaceVariableCompleteMap.isNullOrEmpty()){
            true -> tsvKeyValueMap
            else ->  setReplaceVariableCompleteMap + tsvKeyValueMap
        }
    }

    private fun makeTsvKeyValueMap(
        result: Sequence<MatchResult>,
        setReplaceVariableCompleteMap: Map<String, String>?,
        currentAppDirPath: String,
        fanneName: String,
    ): Map<String, String> {
        val tsvKeyValueListSrc =
            makeTsvKeyValueListSrc(
                result,
                setReplaceVariableCompleteMap,
                currentAppDirPath,
                fanneName,
            )
        val tsvKeyValueList = tsvKeyValueListSrc
            .distinct()
            .joinToString("\n")
            .split("\n")
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "tsvImportResult.txt").absolutePath,
//            listOf(
//                "tsvKeyValueListSrc: ${tsvKeyValueListSrc}",
//                "tsvKeyValueList: ${tsvKeyValueList}",
//                "resultMap: ${tsvKeyValueList.map {
//                    CcScript.makeKeyValuePairFromSeparatedString(
//                        it,
//                        "\t"
//                    )
//                }.toMap()}",
//            ).joinToString("\n\n") + "\n----\n"
//        )
        return tsvKeyValueList.map {
            CcScript.makeKeyValuePairFromSeparatedString(
                it,
                "\t"
            )
        }.toMap().filterKeys { it.isNotEmpty() }
    }

    private fun makeTsvKeyValueListSrc(
        result: Sequence<MatchResult>,
        setReplaceVariableCompleteMap: Map<String, String>?,
        currentAppDirPath: String,
        fanneName: String,
    ): Sequence<String> {
        return result.map {
                val matchResult = it.value
                if (
                    matchResult.isEmpty()
                ) return@map String()
                val tsvImportSentenceList =
                    matchResult.split("\n").filter {
                        it.trim().isNotEmpty()
                    }
                val tsvImportLine =
                    tsvImportSentenceList.firstOrNull()
                        ?: String()
                val useKeyMap = when(tsvImportSentenceList.size > 1) {
                    true -> {
                        val asPhaseLineListCon =
                            tsvImportSentenceList.filterIndexed {
                                    index, line ->
                                val trimLine = line.trim()
                                val isNotCommentOut = !trimLine.startsWith("//")
                                val isNotOnlyComma = trimLine != ","
                                index > 0
                                        && isNotCommentOut
                                        && isNotOnlyComma
                            }.joinToString(String())
                                .replace(
                                    Regex("^[ \t]*${tsvImportUsePhrase}[ \t]*\\("),
                                    String()
                                ).trim()
                                .removeSuffix(")")
                                .split(",")
                                .joinToString("\n")
                        createMapByStrSepa(
                            asPhaseLineListCon,
                            changePhrase,
                        ).toMap().filterKeys { it.isNotEmpty() }
                    }
                    else -> null
                }
                val tsvImportPath =
                    extractPathFromImportLine(tsvImportLine)
                val tsvImportPathObj = File(tsvImportPath)
                if(!tsvImportPathObj.isFile) {
                    LogSystems.stdWarn(
                        "not found: ${tsvImportPath}"
                    )
                    return@map String()
                }
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "tsvImport.txt").absolutePath,
//                listOf(
//                    "matchResult: ${matchResult}",
//                    "tsvImportLine: ${tsvImportLine}",
//                    "asPhaseMap: ${useKeyMap}",
//                    "tsvImportPath: ${tsvImportPath}",
//                    "tsvCon: ${ReadText(
//                        tsvImportPathObj.absolutePath
//                    ).readText().let {
//                        SetReplaceVariabler.execReplaceByReplaceVariables(
//                            it,
//                            setReplaceVariableCompleteMap,
//                            currentAppDirPath,
//                            fanneName,
//                        )
//                    }.let {
//                        replaceAndFilterForTsvImportUsePhrase(
//                            it,
//                            useKeyMap
//                        )
//                    }}"
//                ).joinToString("\n") + "\n----\n"
//            )
                ReadText(
                    tsvImportPathObj.absolutePath
                ).readText().let {
                    SetReplaceVariabler.execReplaceByReplaceVariables(
                        it,
                        setReplaceVariableCompleteMap,
                        currentAppDirPath,
                        fanneName,
                    )
                }.let {
                    replaceAndFilterForTsvImportUsePhrase(
                        it,
                        useKeyMap
                    )
                }
            }.distinct()
    }

    fun extractPathFromImportLine(
        tsvImportLine: String
    ): String {
        return QuoteTool.trimBothEdgeQuote(
            tsvImportLine.trim().trim(';').removePrefix(
                tsvImportPreWord
            ).trim()
        )
    }

    private fun replaceAndFilterForTsvImportUsePhrase(
        targetCon: String,
        useKeyMap: Map<String, String>?
    ): String {
        if(
            useKeyMap.isNullOrEmpty()
        ) return String()
        return targetCon.split("\n").map {
            val keyAndValueList = it.split("\t")
            val key = keyAndValueList.firstOrNull()
                ?: return@map String()
            val changedKey = useKeyMap.get(key)
            val isNotExist = changedKey.isNullOrEmpty()
            if(
                isNotExist
            ) return@map String()
            val value = keyAndValueList.getOrNull(1)
                ?: String()
            "${changedKey}\t${value}"
        }.joinToString("\n")
    }

    fun removeTsvImport(
        jsList: List<String>,
    ):List<String> {
        val tsvImportRegexForRemove =
            "${tsvImportRegexStr}[;]*".toRegex()
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "tsvImportRemove.txt").absolutePath,
//            listOf(
//                "src: ${jsList.joinToString("\n")}",
//                "after: ${jsList.joinToString("\n")
//                    .replace(
//                        tsvImportRegexForRemove,
//                        String()
//                    )}"
//            ).joinToString("\n\n")
//        )
        return jsList.joinToString("\n")
            .replace(
                tsvImportRegexForRemove,
                String()
            ).split("\n")
    }

    private fun trimJsConForTsv(jsList: List<String>): String {
        return (listOf("\n") + jsList).joinToString("\n")
            .replace("\n[ 　\t]*".toRegex(), "\n")
    }

    fun createMapByStrSepa(
        mapEntryStr: String?,
        equalStr: String,
    ): List<Pair<String, String>> {
        if(
            mapEntryStr.isNullOrEmpty()
        ) return emptyList()
        val mapStr = mapEntryStr.split("\n").map {
            val trimLine = it.trim()
            val isCommentOut = trimLine.startsWith("//")
            if(
                isCommentOut
            ) return@map String()
            val keyAndAlterKeyList = trimLine.split(equalStr)
            val key = keyAndAlterKeyList
                .getOrNull(0)
                ?.trim()
            if(
                key.isNullOrEmpty()
            ) return@map String()
            val alterKeySrc =
                keyAndAlterKeyList.getOrNull(1)
                    ?.trim()
                    ?: key
            val alterKey = alterKeySrc.ifEmpty {
                key
            }
            "${key}=${alterKey}"
        }.joinToString("\n")
        return CmdClickMap.createMap(
            mapStr,
            '\n'
        )
    }
}