package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.os.Handler
import android.os.Looper
import android.webkit.WebView
import android.widget.Toast
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.common.variable.WebUrlVariables
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.LongPressForImage
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.LongPressForSrcAnchor
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.LongPressForSrcImageAnchor
import com.puutaro.commandclick.fragment_lib.terminal_fragment.temp_download.FileTempDownloader
import com.puutaro.commandclick.fragment_lib.terminal_fragment.temp_download.ImageTempDownloader
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.util.ReadText


object ImageOnLongClickListener {

    fun set(
        terminalFragment: TerminalFragment
    ) {
        val activity = terminalFragment.activity
        val binding = terminalFragment.binding
        val terminalWebView = binding.terminalWebView
        val longPressForSrcImageAnchor = LongPressForSrcImageAnchor(terminalFragment)
        val longPressForSrcAnchor = LongPressForSrcAnchor(terminalFragment)
        val longPressForImage = LongPressForImage(terminalFragment)
        val currentHitSystemDirPath =
            "${terminalFragment.currentAppDirPath}/${UsePath.cmdclickHitSystemDirRelativePath}"

        activity?.registerForContextMenu(terminalWebView)
        terminalWebView.setOnLongClickListener() { view ->
            val hitTestResult = terminalWebView.hitTestResult
            val currentPageUrl = terminalWebView.url
            val httpsStartStr = WebUrlVariables.httpsPrefix
            val httpStartStr = WebUrlVariables.httpPrefix
            val currentUrl = terminalFragment.currentUrl
                ?: return@setOnLongClickListener false
            when (hitTestResult.type) {
                WebView.HitTestResult.IMAGE_TYPE -> {
                    if (
                        currentPageUrl?.startsWith(httpsStartStr) == true
                        || currentPageUrl?.startsWith(httpStartStr) == true
                    ) {
                        val longPressImageUrl = hitTestResult.extra
                            ?: return@setOnLongClickListener false
//                        ImageTempDownloader.download(
//                            terminalFragment,
//                            longPressImageUrl
//                        )
                        longPressForImage.launch(
                            terminalWebView.title,
                            longPressImageUrl,
                            currentUrl

                        )
//                        val jsContentsListSource = ReadText(
//                            currentHitSystemDirPath,
//                            UsePath.longPressImageAnchorJsName,
//                        ).readText()
//                            .replace(
//                                CommandClickScriptVariable.CMDCLICK_CURRENT_PAGE_URL,
//                                currentUrl
//                            )
//                            .split("\n")
//                        ExecJsLoad.execJsLoad(
//                            terminalFragment,
//                            currentHitSystemDirPath,
//                            UsePath.longPressImageAnchorJsName,
//                            jsContentsListSource
//                        )
                    }
                    false
                }
                WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE -> {
                    val message = Handler(Looper.getMainLooper()).obtainMessage()
                    terminalWebView.requestFocusNodeHref(message)

                    val longPressLinkUrl = message.data.getString("url")
                        ?: return@setOnLongClickListener false
                    val longPressImageUrl = message.data.getString("src")
                        ?: return@setOnLongClickListener  false
//                    ImageTempDownloader.download(
//                        terminalFragment,
//                        longPressImageUrl
//                    )
//                    if (
//                        currentPageUrl?.startsWith(httpsStartStr) == true
//                        || currentPageUrl?.startsWith(httpStartStr) == true
//                    ) {
//                        val jsContentsListSource = ReadText(
//                            currentHitSystemDirPath,
//                            UsePath.longPressSrcImageAnchorJsName,
//                        ).readText()
//                            .replace(
//                                CommandClickScriptVariable.CMDCLICK_LONG_PRESS_LINK_URL,
//                                longPressLinkUrl
//                            )
//                            .replace(
//                                CommandClickScriptVariable.CMDCLICK_LONG_PRESS_IMAGE_URL,
//                                longPressImageUrl
//                            )
//                            .replace(
//                                CommandClickScriptVariable.CMDCLICK_CURRENT_PAGE_URL,
//                                currentUrl
//                            )
//                            .split("\n")
                    Toast.makeText(
                        terminalFragment.context,
                        "src_iamge_anchor" + "\n" + currentPageUrl,
                        Toast.LENGTH_LONG
                    ).show()
                    if (
                        currentPageUrl?.startsWith(httpsStartStr) == true
                        || currentPageUrl?.startsWith(httpStartStr) == true
                    ) {
                        longPressForSrcImageAnchor.launch(
                            terminalWebView.title,
                            longPressLinkUrl,
                            longPressImageUrl,
                            currentUrl
                        )
                    }
                    true
                }
                WebView.HitTestResult.SRC_ANCHOR_TYPE -> {
                    val url = hitTestResult.extra
                        ?: return@setOnLongClickListener false
//                    FileTempDownloader.downloadFile(url)
//                    Toast.makeText(
//                        terminalFragment.context,
//                        url,
//                        Toast.LENGTH_SHORT
//                    ).show()
                    Toast.makeText(
                        terminalFragment.context,
                        "src_anchor" + "\n" + currentPageUrl,
                        Toast.LENGTH_LONG
                    ).show()
                    if (
                        currentPageUrl?.startsWith(httpsStartStr) == true
                        || currentPageUrl?.startsWith(httpStartStr) == true
                    ) {
                        longPressForSrcAnchor.launch(
                            terminalWebView.title,
                            url,
                            currentUrl
                        )
                    }
                    false
                }
                else -> false
            }
        }
    }
}