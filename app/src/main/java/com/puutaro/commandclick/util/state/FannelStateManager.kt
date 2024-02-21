package com.puutaro.commandclick.util.state

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
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
    ): String {
        val firstState = when(
            mainFannelSettingConList.isNullOrEmpty()
        ) {
            true -> return execGetState(
                currentAppDirPath,
                currentFannelName,
            )
            else -> CommandClickVariables.substituteCmdClickVariable(
                mainFannelSettingConList,
                CommandClickScriptVariable.FIRST_STATE
            )
        }
        return when(
            firstState.isNullOrEmpty()
        ) {
            false -> firstState
            else -> execGetState(
                currentAppDirPath,
                currentFannelName,
            )
        }
    }

    fun execGetState(
        currentAppDirPath: String,
        currentFannelName: String,
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
        ).textToList().firstOrNull {
            val trimLine = it.trim()
            trimLine.startsWith("${fannelStateKey}\t")
        }?.split("\t")?.lastOrNull()?.let {
            QuoteTool.trimBothEdgeQuote(it)
        } ?: String()
    }

    fun updateState(
        currentAppDirPath: String,
        currentFannelName: String,
        updateFannelState: String,
    ) {
//        if(
//            updateFannelState.trim().isEmpty()
//        ) return
        val fannelStateRootTableFilePath = ScriptPreWordReplacer.replace(
            UsePath.fannelStateRootTableFilePath,
            currentAppDirPath,
            currentFannelName
        )
        if(
            !File(fannelStateRootTableFilePath).isFile
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

    enum class FannelStateTsvKey(
        val key: String
    ) {
        FANNEL_STATE("fanenlState"),
    }
}