package com.puutaro.commandclick.activity_lib.event.lib.common

import android.widget.Toast
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.WebUrlVariables
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.ScriptPreWordReplacer
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.util.UrlTool
import java.io.File

object ExecUpdateNoSaveUrlPaths {
    fun update(
        activity: MainActivity?,
        currentAppDirPath: String,
        fannelName: String,
    ){
        val indexTerminalFragment =
            TargetFragmentInstance().getFromFragment<TerminalFragment>(
                activity,
                activity?.getString(R.string.index_terminal_fragment)
            )
        if(
            indexTerminalFragment != null
            && indexTerminalFragment.isVisible
        ){
            execSave(
                indexTerminalFragment,
                currentAppDirPath,
                fannelName
            )
            return
        }
        val editExecuteTerminalFragment =
            TargetFragmentInstance().getFromFragment<TerminalFragment>(
                activity,
                activity?.getString(R.string.edit_terminal_fragment)
            )
        if(
            editExecuteTerminalFragment != null
            && editExecuteTerminalFragment.isVisible
        ){
            execSave(
                editExecuteTerminalFragment,
                currentAppDirPath,
                fannelName,
            )
        }

    }

    private fun execSave(
        terminalFragment: TerminalFragment,
        currentAppDirPath: String,
        fannelName: String,
    ){
        val context = terminalFragment.context
        val domain =
            makeDomain(terminalFragment)
                ?: return

        val currentFannelName = if(
            fannelName.isEmpty()
        ) UsePath.cmdclickStartupJsName
        else fannelName
        val noScrollSaveUrlsFilePath = ScriptPreWordReplacer.replace(
            UsePath.noScrollSaveUrlsFilePath,
            currentAppDirPath,
            currentFannelName
        )
        val noScrollSaveUrlsFilePathObj = File(noScrollSaveUrlsFilePath)
        val settingsDirPath = noScrollSaveUrlsFilePathObj.parent
            ?: return
        FileSystems.createDirs(settingsDirPath)
        val noScrollSaveUrlsFileCon =
            domain + "\n" + ReadText(
                noScrollSaveUrlsFilePath
            ).textToList().filter {
                it != domain
            }.joinToString("\n")
        FileSystems.writeFile(
            noScrollSaveUrlsFilePath,
            noScrollSaveUrlsFileCon
        )
        terminalFragment.noScrollSaveUrls = noScrollSaveUrlsFileCon.split("\n")
        Toast.makeText(
            context,
            "save ok",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun makeDomain(
        terminalFragment: TerminalFragment,
    ): String? {
        val context = terminalFragment.context
        val url = terminalFragment.binding.terminalWebView.url
            ?: return null
        val isUrl = url.startsWith(WebUrlVariables.filePrefix)
                || url.startsWith(WebUrlVariables.httpPrefix)
                || url.startsWith(WebUrlVariables.httpsPrefix)
        if(!isUrl) {
            Toast.makeText(
                context,
                "no url",
                Toast.LENGTH_SHORT
            ).show()
            return null
        }
        if(
            url.startsWith(WebUrlVariables.filePrefix)
        ) return url
        return UrlTool.extractDomain(url)
    }
}