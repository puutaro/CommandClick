package com.puutaro.commandclick.fragment_lib.terminal_fragment.web_view_client_lib

import android.webkit.WebView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.variables.WebUrlVariables
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.EnableUrlPrefix
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.FragmentTagManager
import com.puutaro.commandclick.util.TargetFragmentInstance
import java.net.URLDecoder


object SearchViewAndAutoCompUpdater {

    fun update(
        terminalFragment: TerminalFragment,
        webView: WebView?,
        url: String?,
    ) {
        val activity = terminalFragment.activity
        val context = terminalFragment.context
        val cmdIndexFragmentTag = context?.getString(R.string.command_index_fragment)
        val cmdVariableEditFragmentTag = FragmentTagManager.makeTag(
            FragmentTagManager.Prefix.cmdEditPrefix.str,
            terminalFragment.currentAppDirPath,
            terminalFragment.currentScriptName,
            FragmentTagManager.Suffix.ON.str
        )
        val commandIndexFragment =
            TargetFragmentInstance().getFromFragment<CommandIndexFragment>(
                activity,
                cmdIndexFragmentTag
            )
        val cmdVariableEditFragment =
            TargetFragmentInstance().getFromFragment<EditFragment>(
                activity,
                cmdVariableEditFragmentTag
            )
        if(
            commandIndexFragment?.isVisible != true
            && cmdVariableEditFragment?.isVisible != true
        ) return

        val currentAppDirPath = terminalFragment.currentAppDirPath
        val ulrTitle = QuoteTool.trimBothEdgeQuote(webView?.title)
        val escapeStr = WebUrlVariables.escapeStr
        if (ulrTitle.endsWith("\t${escapeStr}")) return

        val urlCheckResult = EnableUrlPrefix.isHttpOrFilePrefix(url)
        if(!urlCheckResult) return
        val searchViewTextSource = if(
            url?.startsWith(WebUrlVariables.queryUrl) == true
        ) {
            queryUrlToText(url)
        } else url ?:return
        val searchViewText = if(
            searchViewTextSource.startsWith(escapeStr)
        ) {
            String()
        } else searchViewTextSource
        updateSearchViewString(
            terminalFragment,
            commandIndexFragment,
            searchViewText,
        )
        if(
            searchViewText.isEmpty()
        ) return

        autoCompUpdater(
            terminalFragment,
            commandIndexFragment,
            currentAppDirPath,
        )
    }

    private fun autoCompUpdater(
        terminalFragment: TerminalFragment,
        commandIndexFragment: CommandIndexFragment?,
        currentAppDirPath: String,
    ){
        if(
            commandIndexFragment == null
        ) return
        if(
            !commandIndexFragment.isVisible
        ) return
        val autoCompUpdateListner = terminalFragment.context as? TerminalFragment.OnAutoCompUpdateListener
        autoCompUpdateListner?.onAutoCompUpdate(
            currentAppDirPath,
        )
    }

    private fun updateSearchViewString(
        terminalFragment: TerminalFragment,
        commandIndexFragment: CommandIndexFragment?,
        searchViewText: String,
    ) {
        if(
            commandIndexFragment == null
        ) return
        if(
            !commandIndexFragment.isVisible
        ) return
        val listener = terminalFragment.context as? TerminalFragment.OnSearchTextChangeListener
        listener?.onSearchTextChange(
            searchViewText,
        )
    }
}


internal fun queryUrlToText(
    url: String
): String {
    val removedUrl = url.removePrefix(
        WebUrlVariables.queryUrl
    )
    val decordedUrl = URLDecoder.decode(removedUrl, "utf-8")
    val decordedUrlLength = decordedUrl.length

    val anpasadPosi = makeStrPosi(
        decordedUrl,
        decordedUrlLength,
        '&'
    )
    val sharpPosi = makeStrPosi(
        decordedUrl,
        decordedUrlLength,
        '#'
    )
    val subStrEndPosi = if(
        anpasadPosi > sharpPosi
    ){
        sharpPosi
    } else {
        anpasadPosi
    }
    return decordedUrl.substring(0, subStrEndPosi)
}


internal fun makeStrPosi(
    targetUrl: String,
    decordedUrlLength: Int,
    posiTargetChar: Char
): Int {
    val escapeStrNum = decordedUrlLength
    val sharpPosiSource = targetUrl.indexOf(posiTargetChar)
    return if (
        sharpPosiSource == -1
    ) {
        escapeStrNum
    } else {
        sharpPosiSource
    }
}
