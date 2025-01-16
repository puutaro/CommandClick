package com.puutaro.commandclick.proccess.edit.lib

import android.content.Context
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.variables.SettingFileVariables
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import org.jsoup.Jsoup
import java.io.File

object SettingFile {

    fun read(
        context: Context?,
        settingFilePath: String,
        fannelPath: String,
        setReplaceVariableCompleteMap: Map<String, String>?,
    ): String {
        val fannelPathObj = File(fannelPath)
        if (!fannelPathObj.isFile) return String()
        val fannelName = fannelPathObj.name
        val firstSettingConList = ReadText(
            settingFilePath
        ).textToList()
        val settingConList = ImportManager.import(
            context,
            firstSettingConList,
            fannelName,
            setReplaceVariableCompleteMap
        ).split("\n")
//        if(settingConList.joinToString("\n").contains("settingAction=")) {
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "settingFile_read.txt").absolutePath,
//                listOf(
//                    "firstSettingConList: ${firstSettingConList.joinToString("~~~")}",
//                    "-----",
//                    "settingConList: ${settingConList.joinToString("~~~")}"
//                ).joinToString("\n\n\n") + "\n\n==============\n\n"
//            )
//        }
        return settingConFormatter(
            settingConList
        ).let {
            formSettingContents(it)
        }.let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                setReplaceVariableCompleteMap,
                fannelName
            )
        }
    }

    fun readLayout(
        context: Context?,
        settingFilePath: String,
        fannelPath: String,
        setReplaceVariableCompleteMap: Map<String, String>?,
    ): String {
        val fannelPathObj = File(fannelPath)
        if (!fannelPathObj.isFile) return String()
        val fannelName = fannelPathObj.name
        val firstSettingConList = ReadText(
            settingFilePath
        ).textToList()
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "settingFile.txt").absolutePath,
//            listOf(
//                "firstSettingCon: ${firstSettingConList.joinToString("\n")}",
//                "readLayoutFromList: ${readLayoutFromList(
//                    context,
//                    firstSettingConList,
//                    fannelName,
//                    setReplaceVariableCompleteMap,
//                )}"
//            ).joinToString("\n\n\n") + "\n\n--------------\n\n"
//        )
        return readLayoutFromList(
            context,
            firstSettingConList,
            fannelName,
            setReplaceVariableCompleteMap,
        )
    }

    fun readLayoutFromList(
        context: Context?,
        firstSettingConList: List<String>,
        fannelName: String,
        setReplaceVariableCompleteMap: Map<String, String>?,
    ): String {
        val settingConList = ImportManager.import(
            context,
            firstSettingConList,
            fannelName,
            setReplaceVariableCompleteMap
        ).split("\n")
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "readLayoutFromList00.txt").absolutePath,
//            listOf(
//                "firstSettingConList: ${firstSettingConList}",
//                "-----",
//                "settingConList: ${settingConList}"
//            ).joinToString("\n\n\n") + "\n\n==============\n\n"
//        )
        return settingConFormatter(
            settingConList
        ).let {
            formSettingContents(it)
        }.let {
            val layoutContents = SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                setReplaceVariableCompleteMap,
                fannelName
            )
            layoutContents
        }
    }

    fun readFromList(
        settingConList: List<String>,
        fannelPath: String,
        setReplaceVariableCompleteMap: Map<String, String>?
    ): String {
        val fannelPathObj = File(fannelPath)
        if (!fannelPathObj.isFile) return String()
        val scriptFileName = fannelPathObj.name
        return formSettingContents(settingConList).let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                setReplaceVariableCompleteMap,
                scriptFileName
            )
        }
    }

    private fun settingConFormatter(
        settingConList: List<String>,
    ): List<String> {
        return settingConList.map {
            it.trim()
        }.filter {
            it.isNotEmpty()
                && !it.startsWith("//")
        }.joinToString("").let {
            QuoteTool.replaceBySurroundedIgnore(
                it,
                ',',
                ",\n"
            ).split("\n")
        }
    }

    fun formSettingContents(
        settingCon: List<String>
    ): String {
        return settingCon.map {
            it.trim()
        }.filter {
            it.isNotEmpty()
                    && !it.startsWith("//")
        }.joinToString("")
    }

    private object ImportManager {

        private const val importPreWord = SettingFileVariables.importPreWord

        private const val importEndSeparator = ".impEND"
        private const val settingSeparators = "|?&"
        private const val replaceSeparator = '&'
        private const val importRegexStr =
            "\n[ \t]*[${settingSeparators}]*${importPreWord}=.+?\\${importEndSeparator}"
        private val importRegex = importRegexStr.toRegex(RegexOption.DOT_MATCHES_ALL)
        private enum class ImportKey(val key: String) {
            IMPORT_PATH("importPath"),
            REPLACE("replace"),
            LOOP_VAR_NAME("loopVarName"),
            TIMES("times"),
            SEPARATOR("separator"),
            PREFIX("prefix"),
            SUFFIX("suffix"),
        }
        private const val startLoopIndex = 1
        private val alreadyUseLoopVarNameList = mutableListOf<String>()

        private fun init(){
            alreadyUseLoopVarNameList.clear()
        }

        private fun addToAlreadyUseLoopVarNameList(loopVarName: String?){
            if(loopVarName.isNullOrEmpty()) return
            alreadyUseLoopVarNameList.add(loopVarName)
        }

        private fun isAlreadyUseLoopVarNameList(loopVarName: String?): Boolean {
            if(loopVarName.isNullOrEmpty()) return false
            return alreadyUseLoopVarNameList.contains(loopVarName)
        }


        fun import(
            context: Context?,
            settingSrcConList: List<String>,
            fannelName: String,
            setReplaceVariableCompleteMap: Map<String, String>? = null
        ): String {
            init()
            val settingConBeforeImport = SetReplaceVariabler.execReplaceByReplaceVariables(
                trimImportSrcCon(settingSrcConList),
                setReplaceVariableCompleteMap,
                fannelName
            )
            var settingCon = settingConBeforeImport
            for(i in 1..5) {
                val result = importRegex.findAll(settingCon)
                if(
                    result.count() == 0
                ){
                    return settingCon
                }
                settingCon = execImport(
                    context,
                    settingCon,
                    result,
                    setReplaceVariableCompleteMap,
                    fannelName,
                ).let {
                    trimImportSrcCon(it.split("\n"))
                }
            }
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "sInImpoetsettingCon.txt").absolutePath,
//                listOf(
//                    "settingCon: ${settingCon}",
//                ).joinToString("\n\n=====================\n\n")
//            )
            return settingCon
        }

        private fun execImport(
            context: Context?,
            settingConBeforeImport: String,
            result: Sequence<MatchResult>,
            setReplaceVariableCompleteMap: Map<String, String>?,
            fannelName: String,
        ): String {
            var settingCon = settingConBeforeImport
            result.forEach {
                val importRawSrcCon = it.value
                val importSrcConWithPrefix = importRawSrcCon
                    .trim('\n')
                    .trim()
                val separatorPrefix =
                    Regex("^[${settingSeparators}]*").find(importSrcConWithPrefix)?.value
                val importSrcCon =
                    when(separatorPrefix == null) {
                        true -> importSrcConWithPrefix
                        else -> importSrcConWithPrefix.removePrefix(separatorPrefix.toString())
                    }.removeSuffix(importEndSeparator)
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "sInImpoet.txt").absolutePath,
//                    listOf(
//                        "importRawSrcCon: ${importRawSrcCon}",
//                        "importSrcConWithPrefix: ${importSrcConWithPrefix}",
//                        "separatorPrefix: ${separatorPrefix}",
//                        "importSrcCon: ${importSrcCon}",
//                    ).joinToString("\n\n=====================\n\n")
//                )
                if (
                    importSrcCon.isEmpty()
                ) return@forEach
                val importMap = makeImportMap(
                    importSrcCon
                )
                val importPath = getImportPath(
                    importMap
                ) ?: return@forEach
                if (
                    importPath.isEmpty()
                    || !File(importPath).isFile
                ) {
                    LogSystems.stdErr(
                        context,
                        "Import path not found: ${importPath}"
                    )
                    settingCon = settingCon.replace(
                        importRawSrcCon,
                        String(),
                    )
                    return@forEach
                }
                val loopTimes = getLoopTimes(
                    importMap
                )
                val loopVarName = getLoopVarName(
                    importMap
                )
                val isLoop = loopTimes > startLoopIndex
                if(
                    isLoop
                    && loopVarName.isNullOrEmpty()
                    ){
                    val spanTimes = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.lightBlue,
                        ImportKey.TIMES.key
                    )
                    val spanLoopVarNameKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        ImportKey.LOOP_VAR_NAME.key
                    )
                    val spanImportRawSrcCon = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errBrown,
                        importRawSrcCon
                    )
                    val errMessage =
                        "[SETTING IMPORT] ${spanLoopVarNameKey} must specify in ${spanTimes} > ${startLoopIndex}: ${spanImportRawSrcCon}"
                    LogSystems.broadErrLog(
                        context,
                        Jsoup.parse(errMessage).text(),
                        errMessage,
                    )
                    return@forEach
                }
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "lloopVarName.txt").absolutePath,
//                    listOf(
//                        "loopVarName: ${loopVarName}",
//                        "alreadyUseLoopVarNameList: ${alreadyUseLoopVarNameList}",
//                        "importRawSrcCon: ${importRawSrcCon}",
//                        "settingCon: ${settingCon}",
//                    ).joinToString("\n\n\n\n") + "\n\n========\n\n"
//                )
                if(
                    isLoop
                    && isAlreadyUseLoopVarNameList(loopVarName)
                ){
                    val spanLoopVarNameKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.lightBlue,
                        ImportKey.LOOP_VAR_NAME.key
                    )
                    val spanLoopVarName = loopVarName?.let {
                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.errRedCode,
                            it
                        )
                    } ?: String()
                    val spanAlreadyUseLoopVarNameListCon = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errBrown,
                        alreadyUseLoopVarNameList.joinToString(",")
                    )
                    val spanImportRawSrcCon = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errBrown,
                        importRawSrcCon
                    )
                    val errMessage =
                        "[SETTING IMPORT] ${spanLoopVarNameKey} duplicated: ${spanLoopVarName}, spanAlreadyUseLoopVarNameListCon: ${spanAlreadyUseLoopVarNameListCon} impotCon: ${spanImportRawSrcCon}"
                    LogSystems.broadErrLog(
                        context,
                        Jsoup.parse(errMessage).text(),
                        errMessage,
                    )
                    return@forEach
                }
                addToAlreadyUseLoopVarNameList(loopVarName)
                val separator = getSeparator(
                    importMap
                )
                val prefix = getPrefix(
                    importMap
                )
                val suffix = getSuffix(
                    importMap
                )
                val repValMap = getRepMap(
                    importMap
                )
//                if(importPath.endsWith("notAwaitAsyncVarErr.js")) {
//                    FileSystems.updateFile(
//                        File(
//                            UsePath.cmdclickDefaultAppDirPath,
//                            "setingImpotCon00.txt"
//                        ).absolutePath,
//                        listOf(
//                            "importMap: ${importMap}",
//                            "importCon: ${importPath}",
//                            "separator: ${separator}",
//                            "suffix: ${suffix}",
//                            "prefix: ${prefix}",
//                            "settingCon: ${settingCon}",
//                        ).joinToString("\n\n") + "\n\n============\n\n"
//                    )
//                }
                val importCon = when(loopTimes < startLoopIndex) {
                    true -> String()
                    else -> CmdClickMap.replaceHolderForJsAction(
                        ReadText(importPath).readText(),
                        repValMap
                    ).let { innerImportCon ->
                        (startLoopIndex..loopTimes).map { loopIndex ->
                            if(loopTimes == startLoopIndex) {
                                return@map innerImportCon
                            }
                            if(
                                loopVarName.isNullOrEmpty()
                            ) return@map innerImportCon
                            if(
                                !innerImportCon.contains("@{${loopVarName}}")
                            ) {
                                val spanLoopVarName =
                                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                                        CheckTool.errRedCode,
                                        loopVarName
                                    )
                                val spanImportRawSrcCon = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                                    CheckTool.errBrown,
                                    importRawSrcCon
                                )
                                val errMessage =
                                    "[SETTING IMPORT] ${ImportKey.LOOP_VAR_NAME.key}'s ${spanLoopVarName} var name is not used: importCon: ${spanImportRawSrcCon}"
                                LogSystems.broadErrLog(
                                    context,
                                    Jsoup.parse(errMessage).text(),
                                    errMessage,
                                )
                                return@map String()
                            }
                            CmdClickMap.replaceByAtVar(
                                innerImportCon,
                                mapOf(
                                    loopVarName to
                                            loopIndex.toString()
                                )
                            )
                        }.joinToString(separator).let {
                            listOf(
                                prefix,
                                it,
                                suffix
                            ).joinToString("\n")
                        }
                    }
                }
                settingCon = settingCon.replace(
                    importRawSrcCon,
                    "${separatorPrefix}${importCon}"
                ).let {
                    SetReplaceVariabler.execReplaceByReplaceVariables(
                        it,
                        setReplaceVariableCompleteMap,
                        fannelName,
                    )
                }
//                if(suffix.isNotEmpty()) {
//                    FileSystems.updateFile(
//                        File(
//                            UsePath.cmdclickDefaultAppDirPath,
//                            "setingImpotCon.txt"
//                        ).absolutePath,
//                        listOf(
//                            "suffix: ${suffix}",
//                            "importCon: ${importCon}",
//                            "settingCon: ${settingCon}",
//                        ).joinToString("\n\n") + "\n\n============\n\n"
//                    )
//                }
            }
            return settingCon
        }


        private fun trimImportSrcCon(jsList: List<String>): String {
            return "\n" + jsList.joinToString("\n")
                .replace("\n[ 　\t]*".toRegex(), "\n")
                .replace("\n//[^\n]+".toRegex(), "\n")
        }

        fun makeImportMap(
            importKeyAndSubKeyCon: String,
        ): Map<String, String> {

            return ImportMapMaker.comp(
                importKeyAndSubKeyCon,
                "${importPreWord}="
            )
        }

        fun getImportPath(
            importMap: Map<String, String>,
        ): String? {
            val importPath = importMap.get(
                ImportKey.IMPORT_PATH.key
            )
            return importPath
        }

        fun getRepMap(
            importMap: Map<String, String>,
        ): Map<String, String> {
            return makeRepValHolderMap(
                importMap.get(
                    ImportKey.REPLACE.key
                )
            )
        }

        fun getLoopTimes(
            importMap: Map<String, String>,
        ): Int {
            return try {
               importMap.get(
                        ImportKey.TIMES.key
                    )?.toInt() ?: startLoopIndex
            } catch (e: Exception){
                startLoopIndex
            }
        }

        fun getLoopVarName(
            importMap: Map<String, String>,
        ): String? {
            return importMap.get(
                ImportKey.LOOP_VAR_NAME.key
            )
        }

        fun getSeparator(
            importMap: Map<String, String>,
        ): String {
            return try {
                importMap.get(
                    ImportKey.SEPARATOR.key
                ) ?: String()
            } catch (e: Exception){
                String()
            }
        }

        fun getPrefix(
            importMap: Map<String, String>,
        ): String {
            return try {
                importMap.get(
                    ImportKey.PREFIX.key
                ) ?: String()
            } catch (e: Exception){
                String()
            }
        }

        fun getSuffix(
            importMap: Map<String, String>,
        ): String {
            return try {
                importMap.get(
                    ImportKey.SUFFIX.key
                ) ?: String()
            } catch (e: Exception){
                String()
            }
        }

        private fun makeRepValHolderMap(
            replaceKeyConWithQuote: String?,
        ): Map<String, String> {
            if(
                replaceKeyConWithQuote.isNullOrEmpty()
            ) return emptyMap()
            val replaceKeyCon = QuoteTool.trimBothEdgeQuote(
                replaceKeyConWithQuote
            )
            return CmdClickMap.createMap(
                replaceKeyCon,
                replaceSeparator
            ).toMap().filterKeys { it.isNotEmpty() }
        }
    }
}