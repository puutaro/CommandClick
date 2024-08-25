package com.puutaro.commandclick.fragment_lib.terminal_fragment.web_view_client_lib

import android.webkit.WebView
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.util.url.WebUrlVariables
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.url.EnableUrlPrefix
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.FdialogToolForTerm
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

object UrlTermLongProcess {
    fun trigger(
        terminalFragment: TerminalFragment,
        terminalViewModel: TerminalViewModel,
        webView: WebView?,
        url: String?
    ){
        if(
            FdialogToolForTerm.howExitExecThisProcess(terminalFragment)
        ) return
        val activity = terminalFragment.activity
        val targetFragmentInstance = TargetFragmentInstance()
        val cmdEditFragmentTag = targetFragmentInstance.getCmdEditFragmentTag(activity)
        val bottomFragment = targetFragmentInstance.getCurrentBottomFragmentInFrag(
            activity,
            cmdEditFragmentTag,
        )
        if(
            bottomFragment is CommandIndexFragment
        ) return

        val urlCheckResult = EnableUrlPrefix.isHttpOrFilePrefix(url)
        terminalViewModel.onDisplayUpdate = !urlCheckResult
        terminalViewModel.onExecInternetButtonShell = urlCheckResult
        if(!urlCheckResult) return
        val context = terminalFragment.context
        val title = webView?.title ?: return
        if(title.isBlank() || title.isEmpty() ) return
        if (
            title.trim().contains(WebUrlVariables.escapeStr)
        ) return
        val isTermShortWhenLoad =
            terminalFragment.onTermShortWhenLoad ==
                    SettingVariableSelects.OnTermShortWhenLoadSelects.ON.name
        if(isTermShortWhenLoad) return
        val listener =
            context as? TerminalFragment.OnTermLongChangeListenerForTerminalFragment
        listener?.onTermLongChangeForTerminalFragment(
            bottomFragment
        )
    }
}