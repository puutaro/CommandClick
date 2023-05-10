package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.EnableTerminalWebView
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object SelecteJsExecutor {
    fun exec(
        currentFragment: Fragment,
        jsFilePath: String
    ) {
        if(
            !File(jsFilePath).isFile
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
            withContext(Dispatchers.Main) {
                val listenerForWebLaunch =
                    currentFragment.context as? EditFragment.OnLaunchUrlByWebViewForEditListener
                listenerForWebLaunch?.onLaunchUrlByWebViewForEdit(
                    JavaScriptLoadUrl.make(
                        context,
                        jsFilePath,
                    ).toString()
                )
            }
        }
    }
}