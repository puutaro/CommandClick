package com.puutaro.commandclick.util.map

import com.puutaro.commandclick.common.variable.settings.EditSettings
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.FannelInfoTool

object FilePrefixGetter {

    fun get(
        fannelInfoMap: HashMap<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        configMap: Map<String, String>?,
        keyName: String,
    ): String? {
        val filePrefix = EditSettings.filePrefix
//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
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
                fannelInfoMap,
                setReplaceVariableMap,
                replaceListDirValue,
                keyName,
            )
        }
    }

    private fun execGetByFilePrefix(
        fannelInfoMap: HashMap<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        replaceListDirValue: String,
        keyName: String,
    ): String? {
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
                setReplaceVariableMap,
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