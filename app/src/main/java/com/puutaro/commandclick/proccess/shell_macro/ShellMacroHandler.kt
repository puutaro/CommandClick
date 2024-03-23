package com.puutaro.commandclick.proccess.shell_macro

import android.content.Context
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.file.ReadText

object ShellMacroHandler {

    fun makeShellCon(
        context: Context,
        shellPath: String,
        setReplaceVariableMap: Map<String, String>?,
        extraRepValMap: Map<String, String>?,
    ): String {
        val concatRepValMap =
            (setReplaceVariableMap ?: mapOf()) +
                    (extraRepValMap ?: mapOf())
        return execMakeShellCon(
            context,
            shellPath,
            concatRepValMap,
        )
    }

    fun handle(
        context: Context,
        busyboxExecutor: BusyboxExecutor?,
        shellPath: String,
        setReplaceVariableMap: Map<String, String>?,
        extraRepValMap: Map<String, String>?,
    ){
        if(
            busyboxExecutor == null
        ) return
        val shellCon = makeShellCon(
            context,
            shellPath,
            setReplaceVariableMap,
            extraRepValMap,
        )
        busyboxExecutor.getCmdOutput(
            shellCon,
            extraRepValMap
        )
    }

    private fun execMakeShellCon(
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
        SAVE_PLAY_LIST(AssetsFileManager.savePreviousPlayListShellPath),
        JUDGE_TSV_VALUE(AssetsFileManager.judgeTsvValueShellPath),
        JUDGE_LIST_DIR(AssetsFileManager.judgeListDirShellPath),
        MAKE_HEADER_TITLE(AssetsFileManager.makeHeaderTitlePath)
    }
}
