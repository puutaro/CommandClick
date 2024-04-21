
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.util.CcScript
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.file.ReadText
import java.io.File

object TsvImportManager {

    const val tsvImportPreWord = "tsvimport"
    private val tsvImportRegex = "\n${tsvImportPreWord}[^\n]*".toRegex()

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
        val tsvImportRegex = "\n${tsvImportPreWord}([^\n]*)".toRegex()
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
            result.map {
                val matchResult = it.value
                if (
                    matchResult.isEmpty()
                ) return@map String()
                val tsvImportPath = QuoteTool.trimBothEdgeQuote(
                    matchResult.trim().trim(';').removePrefix(
                        tsvImportPreWord
                    ).trim()
                )
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
                }
            }.distinct()
        val tsvKeyValueList = tsvKeyValueListSrc
            .distinct()
            .joinToString("\n")
            .split("\n")
        return tsvKeyValueList.map {
            CcScript.makeKeyValuePairFromSeparatedString(
                it,
                "\t"
            )
        }.toMap().filterValues { it.isNotEmpty() }
    }

    fun removeTsvImport(
        jsList: List<String>,
    ):List<String> {
        val tsvImportRegex = "\n[ 　\t]*${tsvImportPreWord}[^\n]*".toRegex()
        return jsList.joinToString("\n")
//        trimJsConForTsv(jsList)
            .replace(
                tsvImportRegex,
                ""
            ).split("\n")
    }

    private fun trimJsConForTsv(jsList: List<String>): String {
        return (listOf("\n") + jsList).joinToString("\n")
            .replace("\n[ 　\t]*".toRegex(), "\n")
    }
}