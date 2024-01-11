import android.content.Context
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.util.CcScript
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.ReadText
import java.io.File

object TsvImportManager {

    private const val importPreWord = "tsvimport"

    fun concatRepValWithTsvImport(
        context: Context?,
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
        val tsvImportRegex = "\n${importPreWord}([^\n]*)".toRegex()
        val result = tsvImportRegex.find(jsConForTsv)
            ?: return setReplaceVariableCompleteMap

        val tsvKeyValueMap = makeTsvKeyValueMap(context, result)
//        Toast.makeText(
//            context,
//            result.groups.map {it?.value}.joinToString("\n")+ tsvKeyValueMap.toString(),
//            Toast.LENGTH_SHORT
//        ).show()
        return when(setReplaceVariableCompleteMap.isNullOrEmpty()){
            true -> tsvKeyValueMap
            else ->  setReplaceVariableCompleteMap + tsvKeyValueMap
        }
    }

    private fun makeTsvKeyValueMap(
        context: Context?,
        result: MatchResult,
    ): Map<String, String> {
        val tsvKeyValueList =
            result.groups.map {
                if (
                    it == null
                ) return@map String()
                val tsvImportPath = QuoteTool.trimBothEdgeQuote(
                    it.value.trim().trim(';')
                )
                val tsvImportPathObj = File(tsvImportPath)
                if(!tsvImportPathObj.isFile) {
                    LogSystems.stdWarn(
                        "not found: ${tsvImportPath}"
                    )
                    return@map String()
                }
                val parentDirPath = tsvImportPathObj.parent
                    ?: return@map String()
                val tsvName = tsvImportPathObj.name
                ReadText(
                    parentDirPath,
                    tsvName
                ).readText()
            }.distinct().joinToString("\n").split("\n")
//        Toast.makeText(
//            context,
//            result.groups.map {it?.value}.joinToString("\n") + "\n" + tsvKeyValueList.joinToString("\n") + "\n" + tsvKeyValueList.map {
//                CcScript.makeKeyValuePairFromSeparatedString(
//                    it,
//                    "\t"
//                )
//            }.toMap().toString(),
//            Toast.LENGTH_SHORT
//        ).show()
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
        val tsvImportRegex = "\n${importPreWord}[^\n]*".toRegex()
        return trimJsConForTsv(jsList)
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