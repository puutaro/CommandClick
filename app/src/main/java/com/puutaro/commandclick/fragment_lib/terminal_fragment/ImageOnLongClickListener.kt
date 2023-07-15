package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.HtmlCompat
import com.bachors.img2ascii.Img2Ascii
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.common.variable.WebUrlVariables
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.LongPressForImage
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.LongPressForSrcAnchor
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.LongPressForSrcImageAnchor
import com.puutaro.commandclick.fragment_lib.terminal_fragment.temp_download.ImageTempDownloader
import com.puutaro.commandclick.util.BitmapTool
import com.puutaro.commandclick.util.ScreenSizeCalculator
import com.puutaro.commandclick.util.FileSystems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


object ImageOnLongClickListener {

    fun set(
        terminalFragment: TerminalFragment
    ) {
        val context = terminalFragment.context
        val activity = terminalFragment.activity
        val binding = terminalFragment.binding
        val terminalWebView = binding.terminalWebView
        val longPressForSrcImageAnchor = LongPressForSrcImageAnchor(terminalFragment)
        val longPressForSrcAnchor = LongPressForSrcAnchor(terminalFragment)
        val longPressForImage = LongPressForImage(terminalFragment)

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
//                    ImageTempDownloader.download(
//                        terminalFragment,
//                        longPressImageUrl
//                    )
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