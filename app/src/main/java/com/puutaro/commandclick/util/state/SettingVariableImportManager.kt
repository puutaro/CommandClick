package com.puutaro.commandclick.util.state

import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.file.ReadText

object SettingVariableImportManager {
    fun import(
        settingVariableList: List<String>?,
        importDisableValList: List<String>,
//        currentAppDirPath: String,
        currentFannelName: String,
        setReplaceVariableMap: Map<String, String>?,
        settingSectionStart: String,
        settingSectionEnd: String,
    ): List<String>? {
        val importedSettingVariables = settingVariableList?.map {
            val settingImportConList =
                CommandClickVariables.substituteCmdClickVariableList(
                    listOf(it),
                    CommandClickScriptVariable.SETTING_IMPORT
                )
            val importDisableSettingVanName =
                CommandClickScriptVariable.IMPORT_DISABLE_VAL_LIST
            when(
                settingImportConList.isNullOrEmpty()
            ) {
                false -> settingImportConList.map {
                    CommandClickVariables.extractValListFromHolder(
                        ReadText(it).textToList(),
                        settingSectionStart,
                        settingSectionEnd
                    )?.filter {
                        importedValNameValueLine ->
                        val isNotImportDisableSettingValName =
                            !importDisableSettingVanName.startsWith("${importDisableSettingVanName}=")
                        val onImport = !importDisableValList.any {
                            importedValNameValueLine.startsWith("${it}=")
                        }
                        onImport && isNotImportDisableSettingValName
                    } ?: emptyList()
                }.flatten().joinToString("\n")
                else -> it
            }
        }?.joinToString("\n")?.split("\n")
        val filterImportedSettingVariables = importedSettingVariables?.filter {
            val trimLine = it.trim()
            trimLine.isNotEmpty()
                    && !it.startsWith("//")
        }?.joinToString("\n")?.let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                setReplaceVariableMap,
//                currentAppDirPath,
                currentFannelName
            )
        }?.split("\n")
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "edits_settinfImport.txt").absolutePath,
//            listOf(
//                "settingVariableListSrc: ${settingVariableList?.joinToString("\n")}",
//                "settingVariableList: ${importedSettingVariables}",
//                "settingVariableListFilter: ${filterImportedSettingVariables}",
//            ).joinToString("\n\n")
//        )
        return filterImportedSettingVariables
    }
}