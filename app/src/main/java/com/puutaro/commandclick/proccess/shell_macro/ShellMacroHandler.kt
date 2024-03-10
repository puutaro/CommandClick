package com.puutaro.commandclick.proccess.shell_macro

import android.content.Context
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.file.ReadText

object ShellMacroHandler {
    fun handle(
        context: Context,
        busyboxExecutor: BusyboxExecutor?,
        shellPath: String,
        setReplaceVariableMap: Map<String, String>?,
        repValMapForTts: Map<String, String>?,
    ){
        if(
            busyboxExecutor == null
        ) return
        val shellCon = makeShellCon(
            context,
            shellPath,
            setReplaceVariableMap,
        )
        busyboxExecutor.getCmdOutput(
            shellCon,
            repValMapForTts
        )
    }

    private fun makeShellCon(
        context: Context,
        shellPath: String,
        setReplaceVariableMap: Map<String, String>?,
    ): String {
        val currentAppDirPath = CcPathTool.getMainAppDirPath(
            shellPath
        )
        val currentFannelName = CcPathTool.getMainFannelFilePath(
            currentAppDirPath
        )
        return makeNoReplaceShellCon(
            context,
            shellPath
        ).let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                setReplaceVariableMap,
                currentAppDirPath,
                currentFannelName
            )
        }

    }

    private fun makeNoReplaceShellCon(
        context: Context,
        shellPath: String
    ): String {
        val shellMacro = ShellMacro.values().firstOrNull {
            it.name == shellPath
        } ?: return ReadText(
            shellPath
        ).readText()
        return AssetsFileManager.readFromAssets(
            context,
            shellMacro.assetsPath
        )
    }

    private enum class ShellMacro(
        val assetsPath: String
    ) {
        SAVE_PLAY_LIST(AssetsFileManager.savePreviousPlayListPath)
    }
}
