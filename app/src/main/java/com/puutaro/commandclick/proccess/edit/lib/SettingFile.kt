package com.puutaro.commandclick.proccess.edit.lib

import com.puutaro.commandclick.common.variable.variables.SettingFileVariables
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.util.file.ReadText
import java.io.File

object SettingFile {

    private const val importPreWord = SettingFileVariables.importPreWord

    fun read(
        settingFilePath: String,
        fannelPath: String,
        setReplaceVariableCompleteMap: Map<String, String>?,
        onImport: Boolean = true
    ): String {
        val fannelPathObj = File(fannelPath)
        if (!fannelPathObj.isFile) return String()
        val scriptFileName = fannelPathObj.name
        val firstSettingCon = ReadText(
            settingFilePath
        ).textToList()
        return settingConFormatter(
            firstSettingCon
        ).map {
            if(
                !onImport
            ) return@map it
            importSetting(
                it,
                scriptFileName,
                setReplaceVariableCompleteMap
            )
        }.let {
            formSettingContents(it)
        }.let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                setReplaceVariableCompleteMap,
//                recentAppDirPath,
                scriptFileName
            )
        }
    }

    fun readLayout(
        settingFilePath: String,
        fannelPath: String,
        setReplaceVariableCompleteMap: Map<String, String>?,
        onImport: Boolean = true
    ): String {
        val fannelPathObj = File(fannelPath)
        if (!fannelPathObj.isFile) return String()
        val fannelName = fannelPathObj.name
        val firstSettingCon = ReadText(
            settingFilePath
        ).textToList()
        return readLayoutFromList(
            firstSettingCon,
            fannelName,
            setReplaceVariableCompleteMap,
            onImport
        )
    }

    fun readLayoutFromList(
        firstSettingConList: List<String>,
        fannelName: String,
        setReplaceVariableCompleteMap: Map<String, String>?,
        onImport: Boolean = true
    ): String {
        return settingConFormatter(
            firstSettingConList
        ).map {
            if(
                !onImport
            ) return@map it
            importLayoutSetting(
                it,
                fannelName,
                setReplaceVariableCompleteMap
            )
        }.let {
            formSettingContents(it)
        }.let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                setReplaceVariableCompleteMap,
                fannelName
            )
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
        return settingConList.map {
            importSetting(
                it,
                scriptFileName,
                setReplaceVariableCompleteMap
            )
        }.let {
            formSettingContents(it)
        }.let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                setReplaceVariableCompleteMap,
//                recentAppDirPath,
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

    private fun importLayoutSetting(
        row: String,
        scriptFileName: String,
        setReplaceVariableCompleteMap: Map<String, String>?
    ): String {
        if (
            !row.contains(importPreWord)
        ) return row
        val trimRowSrc = row
            .trim()
            .trim(';')
            .trim(',')
        val prefixLayoutSeparator =  Regex("(^[-]+)").find(
            trimRowSrc
        )?.value ?: String()

        val suffixLayoutSeparator =  Regex("([-]+)$").find(
            trimRowSrc
        )?.value ?: String()
        val trimRow = trimRowSrc
            .removePrefix(prefixLayoutSeparator)
            .removeSuffix(suffixLayoutSeparator)
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "lImport00.txt").absolutePath,
//            listOf(
//                "row: ${row}",
//                "trimRowSrc: ${trimRowSrc}",
//                "prefixLayoutSeparator: ${prefixLayoutSeparator}",
//                "suffixLayoutSeparator: ${suffixLayoutSeparator}",
//                "trimRow: ${trimRow}"
//            ).joinToString("\n")
//        )
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "lImport.txt").absolutePath,
//            listOf(
//                "row: ${row}",
//                "trimRowSrc: ${trimRowSrc}",
//                "trimRow: ${trimRow}"
//            ).joinToString("\n")
//        )
        return trimRow
            .replace("${importPreWord}=", "")
            .trim()
            .let {
                SetReplaceVariabler.execReplaceByReplaceVariables(
                    it,
                    setReplaceVariableCompleteMap,
//                    recentAppDirPath,
                    scriptFileName
                )
            }.let {
                QuoteTool.trimBothEdgeQuote(it)
            }.let {
                prefixLayoutSeparator + catImportContents(
                    it
                ) + suffixLayoutSeparator
            }
    }

    private fun importSetting(
        row: String,
        scriptFileName: String,
        setReplaceVariableCompleteMap: Map<String, String>?
    ): String {
        if (
            !row.contains(importPreWord)
        ) return row
        val trimRow = row
            .trim()
            .trim(';')
            .trim(',')
        return trimRow
            .replace("${importPreWord}=", "")
            .trim()
            .let {
                SetReplaceVariabler.execReplaceByReplaceVariables(
                    it,
                    setReplaceVariableCompleteMap,
//                    recentAppDirPath,
                    scriptFileName
                )
            }.let {
                QuoteTool.trimBothEdgeQuote(it)
            }.let {
                catImportContents(
                    it
                )
            }
    }

    private fun catImportContents(
        importPath: String,
    ): String {
        val readPathObj = File(importPath)
        if (
            !readPathObj.isFile
        ) return String()
        return formSettingContents(
            ReadText(
                readPathObj.absolutePath
            ).textToList()
        )
    }
}