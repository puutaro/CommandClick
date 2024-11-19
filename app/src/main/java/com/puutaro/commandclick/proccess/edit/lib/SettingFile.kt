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
        val firstSettingCon = ReadText(
            settingFilePath
        ).textToList()
        return readLayoutFromList(
            context,
            firstSettingCon,
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
                && !it.startsWith("#")
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
                    && !it.startsWith("#")
                    && !it.startsWith("//")
        }.joinToString("")
    }

//    private fun importLayoutSetting(
//        context: Context?,
//        row: String,
//        scriptFileName: String,
//        setReplaceVariableCompleteMap: Map<String, String>?
//    ): String {
//        if (
//            !row.contains(importPreWord)
//        ) return row
//        val trimRowSrc = row
//            .trim()
//            .trim(';')
//            .trim(',')
//        val prefixLayoutSeparator =  Regex("(^[-]+)").find(
//            trimRowSrc
//        )?.value ?: String()
//
//        val suffixLayoutSeparator =  Regex("([-]+)$").find(
//            trimRowSrc
//        )?.value ?: String()
//        val trimRow = trimRowSrc
//            .removePrefix(prefixLayoutSeparator)
//            .removeSuffix(suffixLayoutSeparator)
////        FileSystems.updateFile(
////            File(UsePath.cmdclickDefaultAppDirPath, "lImport00.txt").absolutePath,
////            listOf(
////                "row: ${row}",
////                "trimRowSrc: ${trimRowSrc}",
////                "prefixLayoutSeparator: ${prefixLayoutSeparator}",
////                "suffixLayoutSeparator: ${suffixLayoutSeparator}",
////                "trimRow: ${trimRow}"
////            ).joinToString("\n")
////        )
////        FileSystems.updateFile(
////            File(UsePath.cmdclickDefaultAppDirPath, "lImport.txt").absolutePath,
////            listOf(
////                "row: ${row}",
////                "trimRowSrc: ${trimRowSrc}",
////                "trimRow: ${trimRow}"
////            ).joinToString("\n")
////        )
//        return trimRow
//            .replace("${importPreWord}=", "")
//            .trim()
//            .let {
//                SetReplaceVariabler.execReplaceByReplaceVariables(
//                    it,
//                    setReplaceVariableCompleteMap,
////                    recentAppDirPath,
//                    scriptFileName
//                )
//            }.let {
//                QuoteTool.trimBothEdgeQuote(it)
//            }.let {
//                importKeyAndSubKeyCon ->
//                val importMap = ImportTool.makeImportMap(
//                    importKeyAndSubKeyCon
//                )
//                val importPath = ImportTool.getImportPath(
//                    importMap
//                )
//                val importContents = importPath?.let {
//                    val importConSrc = catImportContents(it)
//                    val replaceMap = ImportTool.getRepMap(
//                        importMap
//                    )
//                    CmdClickMap.replaceHolderForJsAction(
//                        importConSrc,
//                        replaceMap
//                    )
//                } ?: String()
//                prefixLayoutSeparator + importContents + suffixLayoutSeparator
//            }
//    }

//    private fun importSetting(
//        context: Context?,
//        row: String,
//        scriptFileName: String,
//        setReplaceVariableCompleteMap: Map<String, String>?
//    ): String {
//        if (
//            !row.contains(importPreWord)
//        ) return row
//        val trimRow = row
//            .trim()
//            .trim(';')
//            .trim(',')
//        return trimRow
//            .replace("${importPreWord}=", "")
//            .trim()
//            .let {
//                SetReplaceVariabler.execReplaceByReplaceVariables(
//                    it,
//                    setReplaceVariableCompleteMap,
//                    scriptFileName
//                )
//            }.let {
//                QuoteTool.trimBothEdgeQuote(it)
//            }.let {
//                importKeyAndSubKeyCon ->
//                val importMap = ImportTool.makeImportMap(
//                    importKeyAndSubKeyCon
//                )
//                val importPath = ImportTool.getImportPath(
//                    context,
//                    importMap
//                )
//                val importContents = importPath?.let {
//                    val importConSrc = catImportContents(it)
//                    val replaceMap = ImportTool.getRepMap(
//                        importMap
//                    )
//                    CmdClickMap.replaceHolderForJsAction(
//                        importConSrc,
//                        replaceMap
//                    )
//                } ?: String()
//                importContents
////                catImportContents(
////                    it
////                )
//            }
//    }

//    private fun catImportContents(
//        importPath: String,
//    ): String {
//        val readPathObj = File(importPath)
//        if (
//            !readPathObj.isFile
//        ) return String()
//        return formSettingContents(
//            ReadText(
//                readPathObj.absolutePath
//            ).textToList()
//        )
//    }


    private object ImportManager {

        private const val importPreWord = SettingFileVariables.importPreWord

        private val importRegexStr =
            "\n[ \t]*${importPreWord}=[^,]+"
        val importRegex = importRegexStr.toRegex()

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
            var settingCon =  settingConBeforeImport
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
            return settingCon
        }

        private fun execImport(
            context: Context?,
            settingConBeforeImport: String,
            result: Sequence<MatchResult>,
            setReplaceVariableCompleteMap: Map<String, String>?,
            fanneName: String,
        ): String {
            var settingCon = settingConBeforeImport
            result.forEach {
                val importSrcCon = it.value
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
                    importSrcCon,
                    importCon
                ).let {
                    SetReplaceVariabler.execReplaceByReplaceVariables(
                        it,
                        setReplaceVariableCompleteMap,
                        fanneName,
                    )
                }
            }
            return settingCon
        }


        private fun trimImportSrcCon(jsList: List<String>): String {
            return (listOf("\n") + jsList).joinToString("\n")
                .replace("\n[ 　\t]*".toRegex(), "\n")
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