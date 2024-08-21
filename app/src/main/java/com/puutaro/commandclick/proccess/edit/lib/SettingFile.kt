package com.puutaro.commandclick.proccess.edit.lib

import com.puutaro.commandclick.common.variable.variables.SettingFileVariables
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.util.file.ReadText
import java.io.File

object SettingFile {

    private val importPreWord = SettingFileVariables.importPreWord

    fun read(
        settingFilePath: String,
        fannelPath: String,
        setReplaceVariableCompleteMap: Map<String, String>?,
        onImport: Boolean = true
    ): String {
        val fannelPathObj = File(fannelPath)
        if (!fannelPathObj.isFile) return String()
        val recentAppDirPath = fannelPathObj.parent
            ?: String()
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
                recentAppDirPath,
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

    fun readFromList(
        settingConList: List<String>,
        fannelPath: String,
        setReplaceVariableCompleteMap: Map<String, String>?
    ): String {
        val fannelPathObj = File(fannelPath)
        if (!fannelPathObj.isFile) return String()
        val recentAppDirPath = fannelPathObj.parent
            ?: String()
        val scriptFileName = fannelPathObj.name
        return settingConList.map {
            importSetting(
                it,
                recentAppDirPath,
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

    private fun importSetting(
        row: String,
        recentAppDirPath: String,
        scriptFileName: String,
        setReplaceVariableCompleteMap: Map<String, String>?
    ): String {
        val trimRow = row
            .trim()
            .trim(';')
            .trim(',')
        if (
            !trimRow.contains(importPreWord)
        ) return row
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