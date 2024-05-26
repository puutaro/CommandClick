package com.puutaro.commandclick.service.lib.textToSpeech

import android.content.Context
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.shell_macro.ShellMacroHandler
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap

private enum class RepValKeyForTts(
    val key: String
){
    PLAY_PATH("playPath"),
    CURRENT_POSI("currentPosi"),
    TOTAL_POSI("totalPosi"),
    CURRENT_BLOCK_NUM("currentBlockNum"),
    TOTAL_BLOCK_NUM("totalBlockNum"),
}

object ExecShellForTts {
    fun exec(
        context: Context,
        shellPath: String,
        shellArgs: String,
        playPath: String,
        currentOrder: Int,
        loopTimes: String,
        currentBlockNum: Int,
        totalTimes: Int,
    ) {
        val shellRepValMap = CmdClickMap.createMap(
            shellArgs,
            '?'
        ).toMap()
        val repValMapForTts = mapOf(
            RepValKeyForTts.PLAY_PATH.key to playPath,
            RepValKeyForTts.CURRENT_POSI.key to currentOrder.toString(),
            RepValKeyForTts.TOTAL_POSI.key to loopTimes,
            RepValKeyForTts.CURRENT_BLOCK_NUM.key to currentBlockNum.toString(),
            RepValKeyForTts.TOTAL_BLOCK_NUM.key to totalTimes.toString(),
        ) + shellRepValMap
        val setReplaceVariableMap =
            ShellMacroHandler.makeSetReplaceVariableMapFromSubFannel(
                context,
                shellPath
            )
        val busyboxExecutor = BusyboxExecutor(
            context,
            UbuntuFiles(context),
        )
        ShellMacroHandler.handle(
            context,
            busyboxExecutor,
            shellPath,
            setReplaceVariableMap,
            repValMapForTts
        )
    }
}