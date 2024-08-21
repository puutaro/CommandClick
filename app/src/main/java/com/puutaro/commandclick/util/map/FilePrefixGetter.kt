package com.puutaro.commandclick.util.map

import com.puutaro.commandclick.common.variable.settings.EditSettings
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.FannelInfoTool

object FilePrefixGetter {

    fun get(
        editFragment: EditFragment,
        configMap: Map<String, String>?,
        keyName: String,
    ): String? {
        val filePrefix = EditSettings.filePrefix
        val fannelInfoMap = editFragment.fannelInfoMap
//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val setReplaceVariableMap = editFragment.setReplaceVariableMap
        val listDirGetValue = configMap?.get(keyName)
        if(
            listDirGetValue.isNullOrEmpty()
        ) return String()
        val replaceListDirValue = SetReplaceVariabler.execReplaceByReplaceVariables(
            listDirGetValue,
            setReplaceVariableMap,
//            currentAppDirPath,
            currentFannelName,
        )
        val listDirValue = QuoteTool.trimBothEdgeQuote(replaceListDirValue)
        val isFileSpecify = listDirValue.startsWith(filePrefix)
        return when(isFileSpecify){
            false -> replaceListDirValue
            else -> execGetByFilePrefix(
                editFragment,
                replaceListDirValue,
                keyName,
            )
        }
    }

    private fun execGetByFilePrefix(
        editFragment: EditFragment,
        replaceListDirValue: String,
        keyName: String,
    ): String? {
        val fannelInfoMap = editFragment.fannelInfoMap
//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val listDirFilePath = replaceListDirValue.removePrefix(ConfigMapTool.filePrefix)
        return ReadText(listDirFilePath).readText().let {
            val listSettingKeyMapCon = SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                editFragment.setReplaceVariableMap,
//                currentAppDirPath,
                currentFannelName,
            ).replace(
                "\t",
                "=",
            )
            CmdClickMap.createMap(
                listSettingKeyMapCon,
                '\n'
            ).toMap().get(
                keyName
            )
        }
    }
}