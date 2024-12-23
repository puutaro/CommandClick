package com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess

import android.webkit.ValueCallback
import android.webkit.WebView
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.url.WebUrlVariables
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.tsv.TsvTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

object ScrollPosition {

    val takePosiLines = 100
    private val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath

    fun execScroll(
        terminalFragment: TerminalFragment,
        webView: WebView,
        currentUrl: String,
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
        if(
            currentUrl.startsWith(WebUrlVariables.monitorUrlPath)
        ) return
        execScrollByMemory(
            webView,
            currentUrl,
        )
    }

    private fun execScrollByMemory(
        webView: WebView,
        currentUrl: String,
    ){
        webView.scrollY = readYPosi(
            currentUrl,
        ).toInt()
    }

    fun save(
        terminalFragment: TerminalFragment?,
        webView: WebView,
        url: String?,
        scrollY: Int,
        oldPositionY: Float,
        rawY: Float,
    ){
        if(
            terminalFragment == null
            || !terminalFragment.isVisible
        ) return
        if(
            url.isNullOrEmpty()
        ) return
        if(
            url.startsWith(WebUrlVariables.monitorUrlPath)
        ) return
        val oldCurrYDff = oldPositionY - rawY
        if(
            -20 < oldCurrYDff
            && oldCurrYDff < 20
        ) return
        saveYPosi(
            terminalFragment,
            url,
            scrollY.toString(),
        )
        if(!url.startsWith(WebUrlVariables.monitorUrlPath)) return
        val scrollPosiSaveDirPath = "${cmdclickDefaultAppDirPath}/${UsePath.cmdclickScrollSystemDirRelativePath}/"
        val cmdclickMonitorScrollPosiFileName = UsePath.cmdclickMonitorScrollPosiFileName
        CoroutineScope(Dispatchers.Main).launch {
            webView.evaluateJavascript(
                "(function() {  " +
                        "return (document.scrollingElement || document.body).scrollTop;" +
                        "})()",
                ValueCallback<String?> { scrollY ->
                    if(
                        scrollY.isEmpty()
                    ) return@ValueCallback
                    FileSystems.writeFile(
                        File(
                            scrollPosiSaveDirPath,
                            cmdclickMonitorScrollPosiFileName
                        ).absolutePath,
                        scrollY
                    )
                })
        }
    }


    private fun saveYPosi(
        terminalFragment: TerminalFragment,
        currentUrl: String,
        scrollPosi: String
    ){
        val cmdclickSiteScrollPosiDirPath = "${cmdclickDefaultAppDirPath}/${UsePath.cmdclickScrollSystemDirRelativePath}"
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
        TsvTool.updateByKeyDistinct(
            File(
                cmdclickSiteScrollPosiDirPath,
                cmdclickSiteScrollPosiFileName
            ).absolutePath,
            currentUrl,
            scrollPosi,
        )
//        val insertLine = "${currentUrl}\t${scrollPosi}"
//        val sitePosiLineList = ReadText(
//            File(
//                cmdclickSiteScrollPosiDirPath,
//                cmdclickSiteScrollPosiFileName
//            ).absolutePath
//        )
//            .textToList()
//            .take(takePosiLines)
//        val sitePosiLineListFiltered = sitePosiLineList.filter {
//            !it.startsWith("${currentUrl}\t")
//        }
//        val updateScrollPosList = listOf(insertLine) + sitePosiLineListFiltered
//        val updateScrollPosCon = updateScrollPosList.joinToString("\n")
//        FileSystems.writeFile(
//            File(
//                cmdclickSiteScrollPosiDirPath,
//                cmdclickSiteScrollPosiFileName
//            ).absolutePath,
//            updateScrollPosCon
//        )
    }

    private fun readYPosi(
        currentUrl: String,
    ): Float {
        if(
            currentUrl.isEmpty()
        ) return 0f
        val cmdclickSiteScrollPosiDirPath =
            "${cmdclickDefaultAppDirPath}/${UsePath.cmdclickScrollSystemDirRelativePath}"
        val cmdclickSiteScrollPosiFileName = UsePath.cmdclickSiteScrollPosiFileName
        val isRegisterPrefix = howRegisterPrefix(
            currentUrl
        )
        if(!isRegisterPrefix) return 0f
        val sitePosiLineList = ReadText(
            File(
                cmdclickSiteScrollPosiDirPath,
                cmdclickSiteScrollPosiFileName
            ).absolutePath
        ).textToList()
        val urlPosiLine = sitePosiLineList.filter {
            it.startsWith("${currentUrl}\t")
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
            currentUrl.startsWith("${it}\t")
        }.isNotEmpty()
    }
}
