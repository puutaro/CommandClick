package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.intent.lib.JavascriptExecuter
import com.puutaro.commandclick.util.EnableTerminalWebView
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.str.ScriptPreWordReplacer
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object SelectJsExecutor {
    fun exec(
        currentFragment: Fragment,
        jsFilePath: String,
        selectedItem: String,
    ) {
        val jsFilePathObj = File(jsFilePath)
        if (
            !jsFilePathObj.isFile
        ) return
        val terminalViewModel:
                TerminalViewModel by currentFragment.activityViewModels()
        terminalViewModel.jsExecuteJob?.cancel()
        terminalViewModel.jsExecuteJob = CoroutineScope(Dispatchers.IO).launch {
            val onLaunchUrl = EnableTerminalWebView.check(
                currentFragment,
                currentFragment.context?.getString(
                    R.string.edit_terminal_fragment
                )
            )
            if (!onLaunchUrl) return@launch
//            val currentAppDir = jsFilePathObj.parent
//                ?: return@launch
            val scriptName = jsFilePathObj.name
            val jsFileContents = withContext(Dispatchers.IO) {
                makeSelectJsContents(
//                    currentAppDir,
                    scriptName,
                )
            }
            withContext(Dispatchers.Main) {
                jsHandler(
                    currentFragment,
                    jsFilePath,
                    jsFileContents,
                    selectedItem,
                )
            }
        }
    }

    private fun jsHandler(
        currentFragment: Fragment,
        jsFilePath: String,
        jsFileContents: List<String>,
        selectedItem: String,
    ){
        val extraRepValMap = mapOf(
            "SELECT_ITEM"
                    to selectedItem
                .replace("\"", "\\\"")
                .replace("'", "\\\'")
                .replace("`", "\\`")
        )
        JavascriptExecuter.jsOrActionHandler(
            currentFragment,
            jsFilePath,
            jsFileContents,
            extraRepValMap
        )
    }
}
private fun makeSelectJsContents(
//    currentAppDir: String,
    scriptName: String,
): List<String> {
    val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
    return ReadText(
        File(
            cmdclickDefaultAppDirPath,
            scriptName
        ).absolutePath
    ).readText().let {
        ScriptPreWordReplacer.replace(
            it,
//            cmdclickDefaultAppDirPath,
            scriptName
        )
    }.split("\n")
}
