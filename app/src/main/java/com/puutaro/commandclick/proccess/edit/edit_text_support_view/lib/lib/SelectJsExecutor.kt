package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.EnableTerminalWebView
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.ScriptPreWordReplacer
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
        val terminalViewModel: TerminalViewModel by currentFragment.activityViewModels()
        val context = currentFragment.context
        terminalViewModel.jsExecuteJob?.cancel()
        terminalViewModel.jsExecuteJob = CoroutineScope(Dispatchers.IO).launch {
            val onLaunchUrl = EnableTerminalWebView.check(
                currentFragment,
                currentFragment.context?.getString(
                    R.string.edit_execute_terminal_fragment
                )
            )
            if (!onLaunchUrl) return@launch
            val currentAppDir = jsFilePathObj.parent
                ?: return@launch
            val scriptName = jsFilePathObj.name
            val fannelDirName = scriptName
                .removeSuffix(CommandClickScriptVariable.JS_FILE_SUFFIX)
                .removeSuffix(CommandClickScriptVariable.SHELL_FILE_SUFFIX) +
                    "Dir"
            val jsFileContents = makeSelectJsContents(
                jsFilePath,
                currentAppDir,
                scriptName,
                fannelDirName,
                selectedItem
            )
            withContext(Dispatchers.Main) {
                val listenerForWebLaunch =
                    currentFragment.context as? EditFragment.OnLaunchUrlByWebViewForEditListener
                listenerForWebLaunch?.onLaunchUrlByWebViewForEdit(
                    JavaScriptLoadUrl.make(
                        context,
                        jsFilePath,
                        jsFileContents,
                    ).toString()
                )
            }
        }
    }
}
private fun makeSelectJsContents(
    jsFilePath: String,
    currentAppDir: String,
    scriptName: String,
    fannelDirName: String,
    selectedItem: String,
): List<String> {
    return ReadText(
        currentAppDir,
        scriptName
    ).readText().let {
        ScriptPreWordReplacer.replace(
            it,
            jsFilePath,
            currentAppDir,
            fannelDirName,
            scriptName
        )
    }.replace(
        "CMDCLICL_SELECT_ITEM",
        selectedItem
    ).split("\n")
}