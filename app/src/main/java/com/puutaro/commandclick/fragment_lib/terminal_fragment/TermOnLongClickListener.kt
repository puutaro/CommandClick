package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.os.Handler
import android.os.Looper
import android.webkit.WebView
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.variables.WebUrlVariables
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.LongPressForImage
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.LongPressForSrcAnchor
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.LongPressForSrcImageAnchor
import com.puutaro.commandclick.util.TargetFragmentInstance


object TermOnLongClickListener {

    fun set(
        terminalFragment: TerminalFragment
    ) {
        val context = terminalFragment.context
        val activity = terminalFragment.activity
        val binding = terminalFragment.binding
        val terminalWebView = binding.terminalWebView
        val longPressForSrcImageAnchor = LongPressForSrcImageAnchor(
            terminalFragment,
            context,
            terminalFragment.srcImageAnchorLongPressMenuFilePath
        )
        val longPressForSrcAnchor = LongPressForSrcAnchor(
            terminalFragment,
            context,
            terminalFragment.srcAnchorLongPressMenuFilePath
        )
        val longPressForImage = LongPressForImage(
            terminalFragment,
            context,
            terminalFragment.imageLongPressMenuFilePath
        )
        val listener =
            context as? TerminalFragment.OnToolBarVisibleChangeListener

        activity?.registerForContextMenu(terminalWebView)
        terminalWebView.setOnLongClickListener { view ->
            val hitTestResult = terminalWebView.hitTestResult
            val currentPageUrl = terminalWebView.url
            val httpsStartStr = WebUrlVariables.httpsPrefix
            val httpStartStr = WebUrlVariables.httpPrefix
            val currentUrl = terminalFragment.currentUrl
                ?: return@setOnLongClickListener false
            when (hitTestResult.type) {
                WebView.HitTestResult.UNKNOWN_TYPE -> {
                    if(
                        terminalFragment.disableShowToolbarWhenHighlight
                        == SettingVariableSelects.DisableShowToolbarWhenHighlightSelects.ON.name
                    ) return@setOnLongClickListener false
                    val targetFragmentInstance = TargetFragmentInstance()
                    val cmdEditFragmentTag = targetFragmentInstance.getCmdEditFragmentTag(activity)
                    val bottomFragment = targetFragmentInstance.getCurrentBottomFragmentInFrag(
                        activity,
                        cmdEditFragmentTag,
                        onNoHeightZeroCheckForEdit = true
                    )
                    listener?.onToolBarVisibleChange(
                        true,
                        bottomFragment
                    )
                    false
                }
                WebView.HitTestResult.IMAGE_TYPE -> {
                    if (
                        currentPageUrl?.startsWith(httpsStartStr) == true
                        || currentPageUrl?.startsWith(httpStartStr) == true
                    ) {
                        val longPressImageUrl = hitTestResult.extra
                            ?: return@setOnLongClickListener false
                        longPressForImage.launch(
                            terminalWebView.title,
                            longPressImageUrl,
                            currentUrl
                        )
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
                    val longPressLinkUrl = hitTestResult.extra
                        ?: return@setOnLongClickListener false
                    if (
                        currentPageUrl?.startsWith(httpsStartStr) == true
                        || currentPageUrl?.startsWith(httpStartStr) == true
                    ) {
                        longPressForSrcAnchor.launch(
                            terminalWebView.title,
                            longPressLinkUrl,
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