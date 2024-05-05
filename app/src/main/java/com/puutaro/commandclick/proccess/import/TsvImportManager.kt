
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

    val tsvImportPreWord = "tsvimport"
    val tsvImportAsPhrase = "as"
    private val tsvImportRegexStr =
        "\n[ \t]*${tsvImportPreWord}[^\n]+(\n${tsvImportAsPhrase}[ \t]*\\([^)]*\\))*"
    private val tsvImportRegex = tsvImportRegexStr.toRegex()
    const val asAllow = "=>"

//        "\n${tsvImportPreWord}[^\n]+\nas".toRegex()
//    [ \t]+[^ ]+
//    const val tsvImportAsPreWord = "tsvimportAs"
//    private val tsvImportRegex = "\n${tsvImportPreWord}[^\n]*".toRegex()

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
//        val tsvImportRegex = "\n${tsvImportPreWord}([^\n]+as[ \t]+[^\n])+".toRegex()
        val result = tsvImportRegex.findAll(jsConForTsv)

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
        val result = tsvImportRegex.findAll(jsConForTsv)
        val tsvKeyValueMap = makeTsvKeyValueMap(
            result,
            setReplaceVariableCompleteMap,
            String(),
            String()
        )
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
        }.toMap().filterValues { it.isNotEmpty() }
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
                val asPhaseMap = when(tsvImportSentenceList.size > 1) {
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
                            }.map {
                                it.trim().removePrefix(tsvImportAsPhrase).trim()
                            }.joinToString(String()).trim()
                                .removePrefix("(")
                                .removeSuffix(")")
                                .split(",")
                                .joinToString("\n")
                        CmdClickMap.createMapByStrSepa(
                            asPhaseLineListCon,
                            asAllow,
                            '\n'
                        ).toMap().filterKeys { it.isNotEmpty() }
                    }
                    else -> null
                }
                val tsvImportPath = QuoteTool.trimBothEdgeQuote(
                    tsvImportLine.trim().trim(';').removePrefix(
                        tsvImportPreWord
                    ).trim()
                )
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "tsvImport.txt").absolutePath,
//                    listOf(
//                        "matchResult: ${matchResult}",
//                        "tsvImportLine: ${tsvImportLine}",
//                        "asPhaseMap: ${asPhaseMap}",
//                        "tsvImportPath: ${tsvImportPath}",
//                    ).joinToString("\n") + "\n----\n"
//                )
                val tsvImportPathObj = File(tsvImportPath)
                if(!tsvImportPathObj.isFile) {
                    LogSystems.stdWarn(
                        "not found: ${tsvImportPath}"
                    )
                    return@map String()
                }
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
                    CmdClickMap.replaceForTsv(
                        it,
                        asPhaseMap
                    )
                }
            }.distinct()
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
}