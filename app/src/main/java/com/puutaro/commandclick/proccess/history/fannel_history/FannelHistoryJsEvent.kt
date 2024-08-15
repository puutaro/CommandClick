package com.puutaro.commandclick.proccess.history.fannel_history

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.intent.lib.JavascriptExecuter
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.file.ReadText
import java.io.File

object FannelHistoryJsEvent {

    private val appHistoryClickJsName = UsePath.appHistoryClickJsName

    fun run(
        fragment: Fragment,
        selectedHistoryFile: String,
    ): Boolean {

        FileSystems.updateLastModified(
            File(
                UsePath.cmdclickAppHistoryDirAdminPath,
                selectedHistoryFile
            ).absolutePath
        )
        val selectedAppDirName = FannelHistoryManager.getAppDirNameFromAppHistoryFileName(
            selectedHistoryFile
        )
        val scriptFileName = FannelHistoryManager.getFannelNameFromAppHistoryFileName(
            selectedHistoryFile
        )
        val fannelDirName = CcPathTool.makeFannelDirName(scriptFileName)
        val selectedAppDirPath = "${UsePath.cmdclickAppDirPath}/${selectedAppDirName}"

        val appHistoryJsDirPath =
            "${selectedAppDirPath}/$fannelDirName/${UsePath.systemExecJsDirName}"
        val appHistoryClickJsPath =
            "$appHistoryJsDirPath/$appHistoryClickJsName"
        if(
            !File(appHistoryClickJsPath).isFile
        ) return false
        val appHistoryClickJsConList =
            ReadText(appHistoryClickJsPath).textToList()
        val jsContents = JavaScriptLoadUrl.make(
            fragment.context,
            appHistoryClickJsPath,
            appHistoryClickJsConList,
        ) ?: return false
        JavascriptExecuter.jsUrlLaunchHandler(
            fragment,
            jsContents
        )
        return true
    }
}