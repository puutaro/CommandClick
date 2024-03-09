package com.puutaro.commandclick.util.state

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.proccess.edit.lib.ListSettingVariableListMaker
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.ScriptPreWordReplacer
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import java.io.File

object FannelStateManager {

    private val fannelStateKey = FannelStateTsvKey.FANNEL_STATE.key

    fun getState(
        currentAppDirPath: String,
        currentFannelName: String,
        mainFannelSettingConList: List<String>?,
        setReplaceVariableMap: Map<String, String>?
    ): String {
        val firstState = when(
            mainFannelSettingConList.isNullOrEmpty()
        ) {
            true -> return execGetState(
                currentAppDirPath,
                currentFannelName,
                setReplaceVariableMap,
            )
            else -> execGetFirstState(
                currentAppDirPath,
                currentFannelName,
                mainFannelSettingConList,
                setReplaceVariableMap,
            )
        }
        val state = when(
            firstState.isNullOrEmpty()
        ) {
            false -> firstState
            else -> execGetState(
                currentAppDirPath,
                currentFannelName,
                setReplaceVariableMap,
            )
        }
        return state
    }

    fun updateState(
        updateFannelState: String,
        readSharePrefMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
    ) {
        val currentAppDirPath = SharePrefTool.getCurrentAppDirPath(
            readSharePrefMap
        )
        val currentFannelName = SharePrefTool.getCurrentFannelName(
            readSharePrefMap
        )
        val fannelStateRootTableFilePath = ScriptPreWordReplacer.replace(
            UsePath.fannelStateRootTableFilePath,
            currentAppDirPath,
            currentFannelName
        )
        if(
            !File(fannelStateRootTableFilePath).isFile
        ) return
        val mainFannelSettingConList = CommandClickVariables.extractSettingValListByFannelName(
            ReadText(File(currentAppDirPath, currentFannelName).absolutePath).textToList(),
            currentFannelName,
        )

        val fannelStateConfigMap = ListSettingVariableListMaker.makeConfigMapFromSettingValList(
            CommandClickScriptVariable.FANNEL_STATE_CONFIG,
            mainFannelSettingConList,
            readSharePrefMap,
            setReplaceVariableMap,
            String()
        )
        val noRegisterFannelStateList = QuoteTool.trimBothEdgeQuote(
            fannelStateConfigMap.get(
                FannelStateSettingKey.NO_REGISTER_STATES.key
            )
        ).split(",")
        if(
            noRegisterFannelStateList.contains(updateFannelState)
        ) return
        val fannelStateFilePath = ScriptPreWordReplacer.replace(
            UsePath.fannelStateStockFilePath,
            currentAppDirPath,
            currentFannelName
        )
        FileSystems.writeFile(
            fannelStateFilePath,
            "${fannelStateKey}\t${updateFannelState}"
        )
    }

    private fun execGetFirstState(
        currentAppDirPath: String,
        currentFannelName: String,
        mainFannelSettingConList: List<String>,
        setReplaceVariableMap: Map<String, String>?,
    ): String? {
        val readSharePreferenceMap = SharePrefTool.makeReadSharePrefMapByString(
            currentAppDirPath,
            currentFannelName
        )
        val fannelStateConfigMap = ListSettingVariableListMaker.makeConfigMapFromSettingValList(
            CommandClickScriptVariable.FANNEL_STATE_CONFIG,
            mainFannelSettingConList,
            readSharePreferenceMap,
            setReplaceVariableMap,
            String()
        )
        val firstStateEntry = fannelStateConfigMap.get(
            FannelStateSettingKey.FIRST_STATE.key
        ) ?: return String()
        return execGetValidState(
            firstStateEntry,
            currentAppDirPath,
            currentFannelName,
            setReplaceVariableMap,
        )
    }

    private fun execGetValidState(
        stateEntry: String?,
        currentAppDirPath: String,
        currentFannelName: String,
        setReplaceVariableMap: Map<String, String>?,
    ): String? {
        if(
            stateEntry.isNullOrEmpty()
        ) return null
        val fannelStateRootTableFilePath = ScriptPreWordReplacer.replace(
            UsePath.fannelStateRootTableFilePath,
            currentAppDirPath,
            currentFannelName
        )
        if(
            !File(fannelStateRootTableFilePath).isFile
        ) return String()
        val validFormatRootTable = ReadText(
            fannelStateRootTableFilePath
        ).readText().let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                setReplaceVariableMap,
                currentAppDirPath,
                currentFannelName
            )
        }.split("\n").map {
            val stateNameAndSettingPathList = it.split("\t")
            if(
                stateNameAndSettingPathList.size == 2
            ) return@map stateNameAndSettingPathList
                .first().trim()
            String()
        }
        val isValidState =
            validFormatRootTable.contains(stateEntry)
        return when(isValidState){
            true -> stateEntry
            else -> String()
        }
    }

    private fun execGetState(
        currentAppDirPath: String,
        currentFannelName: String,
        setReplaceVariableMap: Map<String, String>?
    ): String {
        val fannelStateRootTableFilePath = ScriptPreWordReplacer.replace(
            UsePath.fannelStateRootTableFilePath,
            currentAppDirPath,
            currentFannelName
        )
        if(
            !File(fannelStateRootTableFilePath).isFile
        ) return String()
        val fannelStateFilePath = ScriptPreWordReplacer.replace(
            UsePath.fannelStateStockFilePath,
            currentAppDirPath,
            currentFannelName
        )
        val fannelStateFilePathObj = File(fannelStateFilePath)
        if(
            !fannelStateFilePathObj.isFile
        ) return String()
        val fannelStateKey = FannelStateTsvKey.FANNEL_STATE.key
        return ReadText(
            fannelStateFilePath
        ).readText().let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                setReplaceVariableMap,
                currentAppDirPath,
                currentFannelName
            )
        }.split("\n").firstOrNull {
            val trimLine = it.trim()
            trimLine.startsWith("${fannelStateKey}\t")
        }?.split("\t")?.lastOrNull()?.let {
            QuoteTool.trimBothEdgeQuote(it)
        } ?: String()
    }

    enum class FannelStateTsvKey(
        val key: String
    ) {
        FANNEL_STATE("fanenlState"),
    }

    enum class FannelStateSettingKey(
        val key: String
    ){
        FIRST_STATE("firstState"),
        NO_REGISTER_STATES("noRegisterStates"),
    }
}