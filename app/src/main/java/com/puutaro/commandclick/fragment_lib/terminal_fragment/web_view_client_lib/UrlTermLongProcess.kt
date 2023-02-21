package com.puutaro.commandclick.fragment_lib.terminal_fragment.web_view_client_lib

import android.webkit.WebView
import com.puutaro.commandclick.common.variable.ReadLines
import com.puutaro.commandclick.common.variable.WebUrlVariables
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.EnableUrlPrefix
import com.puutaro.commandclick.proccess.IndexOrEditFragment
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

class UrlTermLongProcess {
    companion object {
        fun torigger(
            terminalFragment: TerminalFragment,
            terminalViewModel: TerminalViewModel,
            webView: WebView?,
            url: String?
        ){
            val urlCheckResult = EnableUrlPrefix.check(url)
            terminalViewModel.onExecInternetButtonShell = urlCheckResult
            if(!urlCheckResult) return
            val context = terminalFragment.context
            val title = webView?.title ?: return
            if(title.isBlank() || title.isEmpty() ) return
            if (title.trim(' ').contains(WebUrlVariables.escapeStr)) return
            val changeTargetFragment =
                IndexOrEditFragment(terminalFragment).select()
            val listener =
                context as? TerminalFragment.OnTermLongChangeListenerForTerminalFragment
            listener?.onTermLongChangeForTerminalFragment(
                changeTargetFragment
            )
            return
        }
    }
}