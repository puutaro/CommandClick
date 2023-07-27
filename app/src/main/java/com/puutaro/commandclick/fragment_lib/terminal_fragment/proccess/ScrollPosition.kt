package com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess

import android.webkit.WebView
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.common.variable.WebUrlVariables
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ReadText

object ScrollPosition {

    val takePosiLines = 50

    fun execScroll(
        terminalFragment: TerminalFragment,
        webView: WebView,
        currentUrl: String
    ) {
        val scrollY = webView.scrollY
        if(
            scrollY > 300
        ) return
        if(
            judgeNoExec(
                terminalFragment,
                currentUrl
            )
        ) return
        webView.scrollY = readYPosi(
            terminalFragment,
            currentUrl
        ).toInt()
    }

    fun save(
        terminalFragment: TerminalFragment,
        url: String?,
        oldPositionY: Float,
        rawY: Float,
    ){
        if(
            !terminalFragment.isVisible
        ) return
        if(
            url.isNullOrEmpty()
        ) return
        val oldCurrYDff = oldPositionY - rawY
        if(
            -20 < oldCurrYDff
            && oldCurrYDff < 20
        ) return
        execSave(
            terminalFragment,
            url
        )
    }

    private fun execSave(
        terminalFragment: TerminalFragment,
        url: String,
    ){
        val webView = terminalFragment.binding.terminalWebView
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
        if(
            judgeNoExec(
                terminalFragment,
                currentUrl
            )
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

    private fun judgeNoExec(
        terminalFragment: TerminalFragment,
        currentUrl: String
    ): Boolean {
        val noScrollSaveUrlList = terminalFragment.noScrollSaveUrls
        if(
            noScrollSaveUrlList.isEmpty()
        ) return false
        return noScrollSaveUrlList.filter {
            if(
                it.isEmpty()
            ) return@filter false
            currentUrl.startsWith(it)
        }.isNotEmpty()
    }
}