package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.os.Handler
import android.os.Looper
import android.webkit.WebView
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.common.variable.WebUrlVariables
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.util.ReadText

object ImageOnLongClickListener {

    fun set(
        terminalFragment: TerminalFragment
    ) {
        val activity = terminalFragment.activity
        val binding = terminalFragment.binding
        val currentHitSystemDirPath =
            "${terminalFragment.currentAppDirPath}/${UsePath.cmdclickHitSystemDirRelativePath}"

        activity?.registerForContextMenu(binding.terminalWebView)
        binding.terminalWebView.setOnLongClickListener() { view ->
            val hitTestResult = binding.terminalWebView.hitTestResult
            val currentPageUrl = binding.terminalWebView.url
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
                        val jsContentsListSource = ReadText(
                            currentHitSystemDirPath,
                            UsePath.longPressImageAnchorJsName,
                        ).readText()
                            .replace(
                                CommandClickScriptVariable.CMDCLICK_LONG_PRESS_IMAGE_URL,
                                longPressImageUrl
                            )
                            .replace(
                                CommandClickScriptVariable.CMDCLICK_CURRENT_PAGE_URL,
                                currentUrl
                            )
                            .split("\n")
                        ExecJsLoad.execJsLoad(
                            terminalFragment,
                            currentHitSystemDirPath,
                            UsePath.longPressImageAnchorJsName,
                            jsContentsListSource
                        )
                    }
                    false
                }
                WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE -> {
                    val message = Handler(Looper.getMainLooper()).obtainMessage()
                    terminalFragment.binding.terminalWebView.requestFocusNodeHref(message)

                    val longPressLinkUrl = message.data.getString("url")
                        ?: return@setOnLongClickListener false
                    val longPressImageUrl = message.data.getString("src")
                        ?: return@setOnLongClickListener  false
                    if (
                        currentPageUrl?.startsWith(httpsStartStr) == true
                        || currentPageUrl?.startsWith(httpStartStr) == true
                    ) {
                        val jsContentsListSource = ReadText(
                            currentHitSystemDirPath,
                            UsePath.longPressSrcImageAnchorJsName,
                        ).readText()
                            .replace(
                                CommandClickScriptVariable.CMDCLICK_LONG_PRESS_LINK_URL,
                                longPressLinkUrl
                            )
                            .replace(
                                CommandClickScriptVariable.CMDCLICK_LONG_PRESS_IMAGE_URL,
                                longPressImageUrl
                            )
                            .replace(
                                CommandClickScriptVariable.CMDCLICK_CURRENT_PAGE_URL,
                                currentUrl
                            )
                            .split("\n")
                        ExecJsLoad.execJsLoad(
                            terminalFragment,
                            currentHitSystemDirPath,
                            UsePath.longPressSrcImageAnchorJsName,
                            jsContentsListSource
                        )
                    }
                    true
                }
                WebView.HitTestResult.SRC_ANCHOR_TYPE -> {
                    val url = hitTestResult.extra
                        ?: return@setOnLongClickListener false
                    if (
                        currentPageUrl?.startsWith(httpsStartStr) == true
                        || currentPageUrl?.startsWith(httpStartStr) == true
                    ) {
                        val jsContentsListSource = ReadText(
                            currentHitSystemDirPath,
                            UsePath.longPressSrcAnchorJsName,
                        ).readText()
                            .replace(
                                CommandClickScriptVariable.CMDCLICK_LONG_PRESS_LINK_URL,
                                url
                            )
                            .replace(
                                CommandClickScriptVariable.CMDCLICK_CURRENT_PAGE_URL,
                                currentUrl
                            )
                            .split("\n")
                        ExecJsLoad.execJsLoad(
                            terminalFragment,
                            currentHitSystemDirPath,
                            UsePath.longPressSrcAnchorJsName,
                            jsContentsListSource
                        )
                    }
                    false
                }
                else -> false
            }
        }
    }
}