package com.puutaro.commandclick.service.lib.music_player.libs

import android.content.Context
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.shell_macro.ShellMacroHandler
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.util.map.CmdClickMap


private enum class RepValKeyForTts(
    val key: String
){
    PLAY_PATH("playPath"),
    CURRENT_POSI("currentPosi"),
    TOTAL_POSI("totalPosi"),
    CURRENT_SEEK("currentSeek"),
    TOTAL_SEEK("totalSeek"),
}

object ExecShellForMusicPlayer {
    fun exec(
        context: Context,
        shellPath: String,
        shellArgs: String,
        playPath: String,
        currentOrder: Int,
        loopTimes: String,
        currentSeek: String,
        totalSeek: String,
    ) {
        val shellRepValMap = CmdClickMap.createMap(
            shellArgs,
            '!'
        ).toMap()
        val repValMapForTts = mapOf(
            RepValKeyForTts.PLAY_PATH.key to playPath,
            RepValKeyForTts.CURRENT_POSI.key to currentOrder.toString(),
            RepValKeyForTts.TOTAL_POSI.key to loopTimes,
            RepValKeyForTts.CURRENT_SEEK.key to currentSeek,
            RepValKeyForTts.TOTAL_SEEK.key to totalSeek,
        ) + shellRepValMap
        val setReplaceVariableMap =
            SetReplaceVariabler.makeSetReplaceVariableMapFromSubFannel(
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