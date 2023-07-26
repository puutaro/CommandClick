package com.puutaro.commandclick.proccess

import androidx.fragment.app.FragmentActivity
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.common.variable.WebUrlVariables
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.TargetFragmentInstance

object ScrollPosition {

    private val takePisLines = 100
    private val cmdclickSiteScrollPosiDirPath = UsePath.cmdclickSiteScrollPosiDirPath
    private val cmdclickSiteScrollPosiFileName = UsePath.cmdclickSiteScrollPosiFileName

    fun getYPosi(
        currentUrl: String
    ): Int {
        return readYPosi(currentUrl).toInt()
    }

    fun save(
        activity: FragmentActivity?
    ){
        val indexTerminalFragment = TargetFragmentInstance().getFromFragment<TerminalFragment>(
            activity,
            activity?.getString(R.string.index_terminal_fragment)
        )
        if(
            indexTerminalFragment != null
            && indexTerminalFragment.isVisible
        ) {
            execSave(
                indexTerminalFragment,
            )
            return
        }
        val editExecuteTerminalFragment = TargetFragmentInstance().getFromFragment<TerminalFragment>(
            activity,
            activity?.getString(R.string.edit_execute_terminal_fragment)
        )
        if(
            editExecuteTerminalFragment != null
            && editExecuteTerminalFragment.isVisible
        ) {
            execSave(
                editExecuteTerminalFragment,
            )
        }
    }

    private fun execSave(
        terminalFragment: TerminalFragment
    ){
        val webView = terminalFragment.binding.terminalWebView
        val url = webView.url ?: return
        val scrollY = webView.scrollY ?: return
        saveYPosi(
            url,
            scrollY.toString(),
        )
    }

    private fun saveYPosi(
        currentUrl: String,
        scrollPosi: String
    ){
        if(
            currentUrl.isEmpty()
        ) return
        if(
            scrollPosi.isEmpty()
        ) return
        val isRegisterPrefix = howRegisterPrefix(
            currentUrl
        )
        if(
            !isRegisterPrefix
        ) return
        FileSystems.createDirs(cmdclickSiteScrollPosiDirPath)
        val insertLine = "${currentUrl}\t${scrollPosi}"
        val sitePosiLineList = ReadText(
            cmdclickSiteScrollPosiDirPath,
            cmdclickSiteScrollPosiFileName
        )
            .textToList()
            .take(takePisLines)
        val sitePosiLineListFiltered = sitePosiLineList.filter {
            !it.startsWith(currentUrl)
        }
        val updateScrollPosList = listOf(insertLine) + sitePosiLineListFiltered
        val updateScrollPosCon = updateScrollPosList.joinToString("\n")
        FileSystems.writeFile(
            cmdclickSiteScrollPosiDirPath,
            cmdclickSiteScrollPosiFileName,
            updateScrollPosCon
        );
    }

    private fun readYPosi(
        currentUrl: String
    ): Float {
        if(
            currentUrl.isEmpty()
        ) return 0f
        val isRegisterPrefix = howRegisterPrefix(
            currentUrl
        )
        if(!isRegisterPrefix) return 0f
        val sitePosiLineList = ReadText(
            cmdclickSiteScrollPosiDirPath,
            cmdclickSiteScrollPosiFileName
        ).textToList()
        val urlPosiLine = sitePosiLineList.filter {
            it.startsWith(currentUrl)
        }.firstOrNull()
            ?: return 0f
        if(
            urlPosiLine.isEmpty()
        ) return 0f
        val posi = urlPosiLine
            .split("\t")
            .getOrNull(1)
            ?: return 0f
        return try {
            posi.toFloat()
        }catch (e: Exception){
            0f
        }
    }


    private fun howRegisterPrefix(
        currentUrl: String
    ): Boolean {
        return currentUrl.startsWith(WebUrlVariables.httpsPrefix)
                || currentUrl.startsWith(WebUrlVariables.httpsPrefix)
                || currentUrl.startsWith(WebUrlVariables.filePrefix)
                || currentUrl.startsWith(WebUrlVariables.slashPrefix)
    }
}