package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity_lib.manager.AdBlocker
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.WebChromeClientSetter
import com.puutaro.commandclick.fragment_lib.terminal_fragment.html.TxtHtmlDescriber
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog_js_interface.JsWebViewDialogManager
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.JsInterfaceAdder
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.libs.ExecJsInterfaceAdder
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.File
import java.lang.ref.WeakReference

object TextJsDialogV3{

    private const val titleOridnalyMaxLines = 1

    private const val keySeparator = '|'
    const val valueSeparator = '?'
    const val switchOn = "ON"
    const val switchOff = "OFF"
    private var titleShowJob: Job? = null


    enum class TextJsDialogKey(
        val key: String
    ){
        ON_FORMAT(TxtHtmlDescriber.TxtHtmlQueryKey.ON_FORMAT.key),
        DISABLE_SCROLL(TxtHtmlDescriber.TxtHtmlQueryKey.DISABLE_SCROLL.key),
        FANNEL_PATH(TxtHtmlDescriber.TxtHtmlQueryKey.FANNEL_PATH.key),
        SAVE_TAG(TxtHtmlDescriber.TxtHtmlQueryKey.SAVE_TAG.key),

    }

    enum class BodyKey(
        val key: String
    ){
        ON_FORMAT("onFormat"),
    }

    private var webViewDialogInstance: Dialog? = null

    fun show(
        terminalFragment: TerminalFragment,
        fannelPath: String,
        title: String,
        con: String,
        configMapConSrc: String,
    ){
        CoroutineScope(Dispatchers.Main).launch {
            execShow(
                terminalFragment,
                fannelPath,
                title,
                con,
                configMapConSrc,
            )
        }
    }

    fun execShow(
        terminalFragment: TerminalFragment,
        fannelPath: String,
        title: String,
        con: String,
        configMapConSrc: String,
    ) {
        val context = terminalFragment.context
            ?: return
        val fannelFile = File(fannelPath)
        if(
            !fannelFile.isFile
        ) {
            LogSystems.stdSErr("invalid fannel path: ${fannelFile.absolutePath}")
            return
        }
        val setReplaceVariablesMap = SetReplaceVariabler.makeSetReplaceVariableMapFromSubFannel(
            context,
            fannelPath
        )
        val configMapCon = SetReplaceVariabler.execReplaceByReplaceVariables(
            configMapConSrc,
            setReplaceVariablesMap,
            fannelFile.name
        )
        val configMap = CmdClickMap.createMap(
            configMapCon,
            keySeparator
        ).toMap()
        webViewDialogInstance = Dialog(
            context,
            R.style.fullScreenRoundCornerDialogTheme,
        )
        webViewDialogInstance?.setContentView(
            R.layout.text_dialog_v3_layout,
        )
        val constraintLayout =
            webViewDialogInstance?.findViewById<ConstraintLayout>(
                R.id.text_dialog_v3_constraint
            )
        val titleTextView =
            webViewDialogInstance?.findViewById<AppCompatTextView>(
                R.id.text_dialog_v3_title
            )?.apply {
                val titleTextViewSrc = this@apply
                setOnClickListener {
                    titleShowJob?.cancel()
                    titleShowJob = CoroutineScope(Dispatchers.Main).launch {
                        titleTextViewSrc.maxLines = Integer.MAX_VALUE
                    }
                }
            } ?: return
        val progressBar =
            webViewDialogInstance?.findViewById<ProgressBar>(
                R.id.text_dialog_v3_progressBar
            ) ?: return
        when(title.isEmpty()){
            true -> {
                titleTextView.isVisible = false
            }
            else -> titleTextView.text = title
        }
        val pocketWebView = let {
            val pocketWebViewSrc = webViewDialogInstance?.findViewById<WebView>(
                R.id.text_dialog_v3_webview
            )
            HideShow.exec(
                titleTextView,
                pocketWebViewSrc,
            )
            webViewSetting(
                terminalFragment,
                pocketWebViewSrc
            )
            webViewClientSetter(
                terminalFragment,
                pocketWebViewSrc,
            )
            WebChromeClientSetter.set(
                terminalFragment,
                pocketWebViewSrc,
                progressBar
            )
            pocketWebViewSrc
        }
        webViewDialogInstance?.setOnCancelListener {
            stopWebView(
                constraintLayout,
                pocketWebView
            )
        }
        val saveTag = configMap.get(
            TextJsDialogKey.SAVE_TAG.key
        )

        val savePath = File(
            UsePath.cmdclickTempDownloadDirPath,
            saveTag ?: "tempSaveTag",
        ).absolutePath
        FileSystems.writeFile(
            savePath,
            con,
        )
        val onFormat = configMap.get(
            TextJsDialogKey.ON_FORMAT.key
        )
        val disableScroll = configMap.get(
            TextJsDialogKey.DISABLE_SCROLL.key
        )
        loadUrlHandler(
            terminalFragment,
            pocketWebView,
            fannelPath,
            savePath,
            saveTag,
            onFormat,
            disableScroll,
        ).let {
            isLaunch ->
            if(!isLaunch) return
        }
        webViewDialogInstance?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        webViewDialogInstance?.show()
    }

    private fun loadUrlHandler(
        terminalFragment: TerminalFragment?,
        pocketWebView: WebView?,
        fannelPath: String,
        savePath: String,
        saveTag: String?,
        onFormat: String?,
        disableScroll: String?,
    ): Boolean {
        if(
            terminalFragment == null
        ) return false
        val textUrl = WevViewDialogUriPrefix.TEXT_CON.virtualUrl
        val queryParameter = listOf(
            onFormat?.let {
                "${TxtHtmlDescriber.TxtHtmlQueryKey.ON_FORMAT.key}=${onFormat}"
            } ?: String(),
            disableScroll?.let {
                "${TxtHtmlDescriber.TxtHtmlQueryKey.DISABLE_SCROLL.key}=${disableScroll}"
            }?: String(),
            "${TxtHtmlDescriber.TxtHtmlQueryKey.FANNEL_PATH.key}=${fannelPath}",
            saveTag?.let {
                "${TxtHtmlDescriber.TxtHtmlQueryKey.SAVE_TAG.key}=${saveTag}"
            } ?: String(),
        ).joinToString(TxtHtmlDescriber.qerySeparator.toString())
        val txtHtmlUrl = listOf(
            savePath,
            TxtHtmlDescriber.searchQuerySuffix,
            queryParameter
        ).joinToString(String())
        pocketWebView?.loadDataWithBaseURL(
            textUrl,
            TxtHtmlDescriber.make(
                terminalFragment,
                txtHtmlUrl,
            ),
            "text/html",
            "utf-8",
            textUrl
        )
        return true

    }


    fun stopWebView(
        constraintLayout: ConstraintLayout?,
        pocketWebView: WebView?,
    ){
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.Main) {
                pocketWebView?.onPause()
                pocketWebView?.isVisible = false
                pocketWebView?.webChromeClient = null
                pocketWebView?.clearHistory()
                pocketWebView?.clearCache(true)
                pocketWebView?.removeAllViews()
                pocketWebView?.destroy()
                constraintLayout?.removeAllViews()
                webViewDialogInstance?.dismiss()
                webViewDialogInstance = null
            }
        }
    }

    private fun webViewSetting(
        terminalFragment: TerminalFragment,
        pocketWebViewSrc: WebView?
    ){
        val settings = pocketWebViewSrc?.settings
            ?: return
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.allowContentAccess = true
        settings.allowFileAccess = true
        settings.builtInZoomControls = true
        settings.displayZoomControls = false


//        settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
        pocketWebViewSrc.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        settings.cacheMode = WebSettings.LOAD_NO_CACHE


        terminalFragment.fontZoomPercent.let {
            settings.textZoom = it
        }
//        settings.textZoom = ((terminalFragment?.fontZoomPercent ?: 100) * 95 ) / 100
        pocketWebViewSrc.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        JsInterfaceAdder.add(
            WeakReference(terminalFragment),
            pocketWebViewSrc
        )
        ExecJsInterfaceAdder.add(
            pocketWebViewSrc,
            JsWebViewDialogManager(WeakReference(terminalFragment)),
        )
    }

    private fun webViewClientSetter(
        terminalFragment: TerminalFragment?,
        pocketWebView: WebView?,
    ){
        var previousUrl: String? = null
        if(
            terminalFragment == null
        ) return
        val context = terminalFragment.context
        val activity = terminalFragment.activity
        pocketWebView?.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val requestUrl = request?.url
                if (
                    requestUrl?.scheme.equals("intent")
                    || requestUrl?.scheme.equals("android-app")
                ) {
                    val intent = Intent.parseUri(request?.url.toString(), Intent.URI_INTENT_SCHEME)
                    val packageManager = activity?.packageManager
                    if (
                        packageManager != null && intent?.resolveActivity(packageManager
                        ) != null)
                    {
                        activity.startActivity(intent)
                        return true
                    }
                }
                return false
            }

            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                if(
                    terminalFragment.onAdBlock != SettingVariableSelects.OnAdblockSelects.ON.name
                ) return super.shouldInterceptRequest(view, request)
                val empty3 = ByteArrayInputStream("".toByteArray())
                val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
                val blockListCon = terminalViewModel.blockListCon
                val isBlock = AdBlocker.judgeBlock(
                    request?.url?.host,
                    blockListCon
                )
                if (isBlock) {
                    return WebResourceResponse("text/plain", "utf-8", empty3)
                }
                return super.shouldInterceptRequest(view, request)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(
                webview: WebView?,
                url: String?
            ) {
                val prevUrl = previousUrl
                if(
                    previousUrl?.length != url?.length
                ) {
                    previousUrl = url
                }
                super.onPageFinished(webview, url)
            }
        }
    }

    private object HideShow {
        fun exec(
            titleTextView: AppCompatTextView?,
            pocketWebViewSrc: WebView?,
        ) {
            if (
                pocketWebViewSrc == null
            ) return
            var oldPositionY = 0f
            with(pocketWebViewSrc) {
                setOnTouchListener { v, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            if (titleTextView?.maxLines != titleOridnalyMaxLines) {
                                titleTextView?.maxLines = titleOridnalyMaxLines
                                titleTextView?.ellipsize = TextUtils.TruncateAt.END
                            }
                            oldPositionY = event.rawY
                            v.performClick()
                        }

                        MotionEvent.ACTION_UP -> {
                            execHideShowForPocketWebview(
                                oldPositionY,
                                event.rawY,
                                titleTextView,
                            )
                        }
                    }
                    v.performClick()
                    false
                }
            }
        }

        private fun execHideShowForPocketWebview(
            oldPositionY: Float,
            rawY: Float,
            titleTextView: AppCompatTextView?,
        ) {
            val oldCurrYDff =  rawY - oldPositionY
            when(true) {
                (oldCurrYDff < -10) -> {
                    titleTextView?.isVisible = true
                }
                (oldCurrYDff > 100) -> {
                    titleTextView?.isVisible = false
                }
                else -> {}
            }
        }
    }
}





