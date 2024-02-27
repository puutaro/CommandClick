package com.puutaro.commandclick.proccess.edit.lib

import com.puutaro.commandclick.common.variable.variables.SettingFileVariables
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.ScriptPreWordReplacer
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
                fannelPath,
                setReplaceVariableCompleteMap
            )
        }.let {
            formSettingContents(it)
        }
    }

    fun readFromList(
        settingConList: List<String>,
        fannelPath: String,
        setReplaceVariableCompleteMap: Map<String, String>?
    ): String {
        return settingConList.map {
            importSetting(
                it,
                fannelPath,
                setReplaceVariableCompleteMap
            )
        }.let {
            formSettingContents(it)
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
        fannelPath: String,
        setReplaceVariableCompleteMap: Map<String, String>?
    ): String {
        val fannelPathObj = File(fannelPath)
        if (!fannelPathObj.isFile) return String()
        val recentAppDirPath = fannelPathObj.parent
            ?: return row
        val scriptFileName = fannelPathObj.name
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
                    recentAppDirPath,
                    scriptFileName
                )
            }
            .let {
                ScriptPreWordReplacer.replace(
                    it,
                    recentAppDirPath,
                    scriptFileName
                )
            }
            .let {
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