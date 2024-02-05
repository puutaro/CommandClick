package com.puutaro.commandclick.proccess.history

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.util.AppHistoryManager
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import java.io.File

object AppHistoryJsEvent {

    private val appHistoryClickJsName = UsePath.appHistoryClickJsName

    fun run(
        fragment: Fragment,
        selectedScriptFileName: String,
    ): Boolean {

        FileSystems.updateLastModified(
            File(
                UsePath.cmdclickAppHistoryDirAdminPath,
                selectedScriptFileName
            ).absolutePath
        )
        val selectedAppDirName = AppHistoryManager.getAppDirNameFromAppHistoryFileName(
            selectedScriptFileName
        )
        val scriptFileName = AppHistoryManager.getScriptFileNameFromAppHistoryFileName(
            selectedScriptFileName
        )
        val fannelDirName = CcPathTool.makeFannelDirName(scriptFileName)
        val selectedAppDirPath = "${UsePath.cmdclickAppDirPath}/${selectedAppDirName}"

        val appHistoryJsDirPath =
            "${selectedAppDirPath}/$fannelDirName/${UsePath.systemExecJsDirName}"
        val appHistoryClickJsPath =
            "$appHistoryJsDirPath/$appHistoryClickJsName"
        if(!File(appHistoryClickJsPath).isFile) return false
        val jsContents = JavaScriptLoadUrl.make(
            fragment.context,
            appHistoryClickJsPath,
        ) ?: return false
        ExecJsLoad.jsUrlLaunchHandler(
            fragment,
            jsContents
        )
        return true
    }
}