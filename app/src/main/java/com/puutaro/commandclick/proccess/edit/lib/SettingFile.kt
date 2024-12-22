package com.puutaro.commandclick.proccess.edit.lib

import android.content.Context
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.SettingFileVariables
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
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
//            File(UsePath.cmdclickDefaultAppDirPath, "layoutContents00.txt").absolutePath,
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
        private const val importRegexStr =
            "\n[ \t]*[${settingSeparators}]*${importPreWord}=.+?\\${importEndSeparator}"
        private val importRegex = importRegexStr.toRegex(RegexOption.DOT_MATCHES_ALL)

        fun import(
            context: Context?,
            settingSrcConList: List<String>,
            fannelName: String,
            setReplaceVariableCompleteMap: Map<String, String>? = null
        ): String {
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
                val importMap = ImportTool.makeImportMap(
                    importSrcCon
                )
                val importPath = ImportTool.getImportPath(
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
                val repValMap = ImportTool.getRepMap(
                    importMap
                )
                val importCon = ReadText(importPath).readText().let {
                    CmdClickMap.replaceHolderForJsAction(
                        it,
                        repValMap
                    )
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
            }
            return settingCon
        }


        private fun trimImportSrcCon(jsList: List<String>): String {
            return "\n" + jsList.joinToString("\n")
                .replace("\n[ 　\t]*".toRegex(), "\n")
                .replace("\n//[^\n]+".toRegex(), "\n")
        }

        private object ImportTool {

            private const val importPreWord = SettingFileVariables.importPreWord

            private enum class ImportKey(val key: String) {
                IMPORT_PATH("importPath"),
                REPLACE("replace"),
            }

            private const val replaceSeparator = '&'


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
}