package com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess

import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.common.variable.WebUrlVariables
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.TargetFragmentInstance

object ScrollPosition {

    val takePosiLines = 100

    fun getYPosi(
        terminalFragment: TerminalFragment,
        currentUrl: String
    ): Int {
        return readYPosi(
            terminalFragment,
            currentUrl
        ).toInt()
    }

    fun save(
        terminalFragment: TerminalFragment,
        oldPositionY: Float,
        rawY: Float,
    ){
        val activity = terminalFragment.activity
        val oldCurrYDff = oldPositionY - rawY
        if(
            -20 < oldCurrYDff
            && oldCurrYDff < 20
        ) return
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
        val scrollY = webView.scrollY
        saveYPosi(
            terminalFragment,
            url,
            scrollY.toString(),
        )
    }

    private fun saveYPosi(
        terminalFragment: TerminalFragment,
        currentUrl: String,
        scrollPosi: String
    ){
        val currentAppDirPath = terminalFragment.currentAppDirPath
        val cmdclickSiteScrollPosiDirPath = "${currentAppDirPath}/${UsePath.cmdclickScrollSystemDirRelativePath}"
        val cmdclickSiteScrollPosiFileName = UsePath.cmdclickSiteScrollPosiFileName
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
            .take(takePosiLines)
        val sitePosiLineListFiltered = sitePosiLineList.filter {
            !it.startsWith(currentUrl)
        }
        val updateScrollPosList = listOf(insertLine) + sitePosiLineListFiltered
        val updateScrollPosCon = updateScrollPosList.joinToString("\n")
        FileSystems.writeFile(
            cmdclickSiteScrollPosiDirPath,
            cmdclickSiteScrollPosiFileName,
            updateScrollPosCon
        )
    }

    private fun readYPosi(
        terminalFragment: TerminalFragment,
        currentUrl: String
    ): Float {
        if(
            currentUrl.isEmpty()
        ) return 0f
        val currentAppDirPath = terminalFragment.currentAppDirPath
        val cmdclickSiteScrollPosiDirPath =
            "${currentAppDirPath}/${UsePath.cmdclickScrollSystemDirRelativePath}"
        val cmdclickSiteScrollPosiFileName = UsePath.cmdclickSiteScrollPosiFileName
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