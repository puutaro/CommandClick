package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.os.Handler
import android.os.Looper
import android.webkit.WebView
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.LongPressForImage
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.LongPressForSrcAnchor
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.LongPressForSrcImageAnchor
import com.puutaro.commandclick.proccess.broadcast.BroadCastIntent
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.util.url.WebUrlVariables
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference


object TermOnLongClickListener {

    fun set(
        terminalFragment: TerminalFragment
    ) {
        val context = terminalFragment.context
        val activity = terminalFragment.activity
        val binding = terminalFragment.binding
        val terminalWebView = binding.terminalWebView
        val longPressForSrcImageAnchor = LongPressForSrcImageAnchor(
            WeakReference(terminalFragment),
            context,
            terminalFragment.srcImageAnchorLongPressMenuFilePath
        )
        val longPressForSrcAnchor = LongPressForSrcAnchor(
            WeakReference(terminalFragment),
            context,
            terminalFragment.srcAnchorLongPressMenuFilePath
        )
        val longPressForImage = LongPressForImage(
            WeakReference(terminalFragment),
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
                    CoroutineScope(Dispatchers.Main).launch {
                        val cmdEditFragmentTag = withContext(Dispatchers.IO) {
                            TargetFragmentInstance.getCmdEditFragmentTag(
                                activity
                            )
                        }
                        val bottomFragment = withContext(Dispatchers.IO) {
                            TargetFragmentInstance.getCurrentBottomFragmentInFrag(
                                activity,
                                cmdEditFragmentTag,
                            )
                        }
                        withContext(Dispatchers.Main) {
                            listener?.onToolBarVisibleChange(
                                true,
                                bottomFragment
                            )
                        }
                        val cmdIndexFragment = withContext(Dispatchers.IO) {
                            TargetFragmentInstance.getCmdIndexFragmentFromFrag(
                                terminalFragment.activity
                            )
                        } ?: return@launch
                        if(
                            cmdIndexFragment.binding.pageSearch.cmdclickPageSearchToolBar.isVisible
                        ) return@launch
                        withContext(Dispatchers.Main){
                            val listenerForSelectionBar =
                                context as TerminalFragment.OnSelectionSearchBarSwitchListenerForTerm
                            listenerForSelectionBar.onSelectionSearchBarSwitchForTerm(true)
                        }
                        withContext(Dispatchers.IO) {
                            val jsContents = AssetsFileManager.readFromAssets(
                                context,
                                AssetsFileManager.textSelectionStartJs
                            ).split("\n")
                            val jsScriptUrl = JavaScriptLoadUrl.makeFromContents(
                                context,
                                jsContents
                            ) ?: return@withContext
                            BroadCastIntent.sendUrlCon(
                                context,
                                jsScriptUrl
                            )
                        }
                    }
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
