package com.puutaro.commandclick.util.state

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.ScriptPreWordReplacer
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import java.io.File

object FannelStateManager {

    private val fannelStateKey = FannelStateTsvKey.FANNEL_STATE.key

    fun getSate(
        currentAppDirPath: String,
        currentFannelName: String,
    ): String {
        val fannelStateFilePath = ScriptPreWordReplacer.replace(
            UsePath.fannelStateFilePath,
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
        if(
            updateFannelState.trim().isEmpty()
        ) return
        val fannelStateFilePath = ScriptPreWordReplacer.replace(
            UsePath.fannelStateFilePath,
            currentAppDirPath,
            currentFannelName
        )
        FileSystems.writeFile(
            fannelStateFilePath,
            "${fannelStateKey}\t${updateFannelState}"
        )
    }

    fun getStateFromEditFragTag(
        editFragment: EditFragment
    ): String {
        return FragmentTagManager.execGetStateFromEditFragTag(
            editFragment
        )
    }

    enum class FannelStateTsvKey(
        val key: String
    ) {
        FANNEL_STATE("fanenlState"),
    }
}