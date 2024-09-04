package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecSetToolbarButtonImage
import com.puutaro.commandclick.activity_lib.manager.AdBlocker
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.WebChromeClientSetter
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog_js_interface.JsWebViewDialogManager
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.JsInterfaceAdder
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.libs.ExecJsInterfaceAdder
import com.puutaro.commandclick.proccess.broadcast.BroadCastIntent
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.image_tools.ScreenSizeCalculator
import com.puutaro.commandclick.util.url.WebUrlVariables
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.lang.ref.WeakReference
import java.time.LocalDateTime

class GgleSchDialog(
    private val terminalFragmentRef: WeakReference<TerminalFragment>,
) {
    private val autoFocusGgleSearchUrl = WebUrlVariables.autoFocusGgleSearchUrl

    private var loadAtLocal = false
    private var webViewDialogInstance: Dialog? = let {
        val terminalFragment = terminalFragmentRef.get()
        val context = terminalFragment?.context
        val webViewDialog = Dialog(context as Context)
        webViewDialog.setContentView(
            R.layout.ggle_sch_webview_layout
        )
        webViewDialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        webViewDialog.setOnCancelListener {
            stopWebView()
        }
        webViewDialog
    }
    private val webviewDialogLayout =
        webViewDialogInstance?.findViewById<ConstraintLayout>(
            R.id.ggle_sch_webview_dialog_layout
        )
    private var progressBar = webViewDialogInstance?.findViewById<ProgressBar>(
        R.id.ggle_sch_dialog_webview_progressBar
    )
    private var ggleSchButton = let {
        val ggleSchButtonSrc =
            webViewDialogInstance?.findViewById<AppCompatImageView>(
                R.id.ggle_sch_bottom_button
            )
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.Main) {
                updateToolbarButton()
            }
            withContext(Dispatchers.Main){
                ggleSchButtonSrc?.setOnClickListener {
                    highLightSearch(
                        terminalFragmentRef.get()?.context
                    )
                }
            }
        }
        ggleSchButtonSrc
    }

    private var ggleSchButtonCaption = webViewDialogInstance?.findViewById<OutlineTextView>(
        R.id.ggle_sch_button_caption
    )

    private var pocketGgleSchWebView = let {
        val pocketWebViewSrc = webViewDialogInstance?.findViewById<WebView>(
            R.id.ggle_sch_dialog_webview
        )
        val terminalFragment = terminalFragmentRef.get()
        toolbarHideShow(
            terminalFragment,
            pocketWebViewSrc
        )
        webViewSetting(
            terminalFragment,
            pocketWebViewSrc
        )
        webViewClientSetter(
            terminalFragment,
            pocketWebViewSrc
        )
            WebChromeClientSetter.set(
                terminalFragment,
                pocketWebViewSrc,
                progressBar as ProgressBar
            )
//        pocketWebViewSrc?.isVisible = false
        pocketWebViewSrc?.loadUrl(autoFocusGgleSearchUrl)
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                delay(500)
            }
            withContext(Dispatchers.Main) {
                pocketWebViewSrc?.onPause()
                pocketWebViewSrc?.isVisible = false
            }
        }
        pocketWebViewSrc
    }

    fun show() {
        loadAtLocal = true
        PocketWebviewGoBack.initPrevBackTime()
        CoroutineScope(Dispatchers.Main).launch{
            withContext(Dispatchers.Main){
                startWebView()
            }
            val terminalFragment = terminalFragmentRef.get()
            CoroutineScope(Dispatchers.IO).launch autoFocus@ {
                withContext(Dispatchers.IO){
                    for(i in 1..5){
                        val isFocus = withContext(Dispatchers.Main) {
                            pocketGgleSchWebView?.requestFocus() == true
                        }
                        delay(100)
                        if(
                            isFocus
                        ) {
                            break
                        }
                    }
                }
                GglePreFocusJs.loadGglePreFocusJs(
                    terminalFragment?.context,
                    pocketGgleSchWebView,
                )
            }
            webViewDialogInstance?.show()
        }
    }

    private object GglePreFocusJs {

        suspend fun loadGglePreFocusJs(
            context: Context?,
            webView: WebView?,
        ){
            val jsScriptUrl = withContext(Dispatchers.IO) {
                val jsContents = AssetsFileManager.readFromAssets(
                    context,
                    AssetsFileManager.ggleSchBoxFocus,
                ).split("\n")
                JavaScriptLoadUrl.makeFromContents(
                    context,
                    jsContents
                )
            } ?: return
            withContext(Dispatchers.Main) {
                webView?.loadUrl(jsScriptUrl)
            }
        }
    }

    private fun startWebView(){
        try {
            pocketGgleSchWebView?.onResume()
        } catch (e: Exception){
            print("pass")
        }
        pocketGgleSchWebView?.isVisible = true
        ggleSchButton?.isVisible = true
        ggleSchButtonCaption?.isVisible = true
    }

    fun stopWebView(){
        val isAutoFocusUrl =
            pocketGgleSchWebView?.url?.contains(WebUrlVariables.autoFocusGgleSearchUrl) == true
        pocketGgleSchWebView?.onPause()
        pocketGgleSchWebView?.isVisible = false
        if(!isAutoFocusUrl) {
            pocketGgleSchWebView?.webChromeClient = null
            pocketGgleSchWebView?.clearHistory()
            pocketGgleSchWebView?.clearCache(true)
            pocketGgleSchWebView?.removeAllViews()
            pocketGgleSchWebView?.destroy()
            ggleSchButton?.isVisible = false
            webviewDialogLayout?.removeAllViews()
        }
        webViewDialogInstance?.dismiss()
        if(!isAutoFocusUrl) {
            webViewDialogInstance = null
            val terminalFragment = terminalFragmentRef.get()
//            noShowKeyBoardForPreloadAutoGgle = true
            terminalFragment?.ggleWebViewManager = GgleSchDialog(
                terminalFragmentRef,
            )
        }
    }

    fun destroyWebView(){
        pocketGgleSchWebView?.onPause()
        pocketGgleSchWebView?.isVisible = false
        pocketGgleSchWebView?.webChromeClient = null
        pocketGgleSchWebView?.clearHistory()
        pocketGgleSchWebView?.clearCache(true)
        pocketGgleSchWebView?.removeAllViews()
        pocketGgleSchWebView?.destroy()
        ggleSchButton?.isVisible = false
        webviewDialogLayout?.removeAllViews()
        webViewDialogInstance?.dismiss()
        webViewDialogInstance = null
    }

    suspend fun updateToolbarButton(){
        ExecSetToolbarButtonImage.setImageButton(
            ggleSchButton,
            CmdClickIcons.GOOGLE,
        )
    }

    private fun webViewSetting(
        terminalFragment: TerminalFragment?,
        pocketWebViewSrc: WebView?
    ){
        val settings = pocketWebViewSrc?.settings
            ?: return
//        settings.blockNetworkLoads = true
//        settings.blockNetworkImage = true
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.allowContentAccess = true
        settings.allowFileAccess = true
        settings.builtInZoomControls = false
        settings.displayZoomControls = false


//        settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
        pocketWebViewSrc.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        settings.cacheMode = WebSettings.LOAD_NO_CACHE



        settings.textZoom = ((terminalFragment?.fontZoomPercent ?: 100) * 95 ) / 100
        pocketWebViewSrc.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        JsInterfaceAdder.add(
            WeakReference(terminalFragment),
            pocketWebViewSrc
        )
        ExecJsInterfaceAdder.add(
            pocketWebViewSrc,
            JsWebViewDialogManager(terminalFragmentRef),
        )
    }

    private fun highLightSearch(
        context: Context?,
    ){
        val jsContents = AssetsFileManager.readFromAssets(
            context,
            AssetsFileManager.assetsHighlightSchForDialogWebViewPath
        ).split("\n")
        val jsScriptUrl = JavaScriptLoadUrl.makeFromContents(
            context,
            jsContents
        ) ?: return
        pocketGgleSchWebView?.loadUrl(jsScriptUrl)
    }


    private fun webViewClientSetter(
        terminalFragment: TerminalFragment?,
        pocketWebView: WebView?
    ){
        if(
            terminalFragment == null
        ) return
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
//                val isLoadLocal = request?.url.toString().contains(autoFocusGgleSearchUrl)
                if(loadAtLocal) {
                    BroadCastIntent.sendUrlCon(
                        terminalFragment.context,
                        requestUrl.toString()
                    )
                    stopWebView()
                    return true
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

//            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
//                super.onPageStarted(view, url, favicon)
//                CoroutineScope(Dispatchers.Main).launch {
//                    if(noShowKeyBoardForPreloadAutoGgle) return@launch
//                    GglePreFocusJs.execPageFinishJs(
//                        context,
//                        view,
//                        url,
//                    )
//                }
//            }

            override fun onPageFinished(
                webview: WebView?,
                url: String?
            ) {
                if(
                    url?.contains(autoFocusGgleSearchUrl) != true
                ) return
//                webview?.onPause()
                super.onPageFinished(webview, url)
            }
        }
    }


    private object PocketWebviewGoBack {

        private var prevBackTime = LocalDateTime.parse("2020-02-15T21:30:50")

        fun initPrevBackTime(){
            prevBackTime = LocalDateTime.parse("2020-02-15T21:30:50")
        }
    }


    private fun toolbarHideShow(
        terminalFragment: TerminalFragment?,
        pocketWebViewSrc: WebView?,
    ){
        if(
            pocketWebViewSrc == null
        ) return
        var oldPositionY = 0f
        val hideShowThreshold = ScreenSizeCalculator.getScreenHeight(terminalFragment?.activity)
        with(pocketWebViewSrc){
            setOnTouchListener {
                    v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        oldPositionY = event.rawY
                        v.performClick()
                    }
                    MotionEvent.ACTION_UP -> {
                        execHideShowForGGleSchWebview(
                            hideShowThreshold,
                            oldPositionY,
                            event.rawY,
//                            ggleSchButton
                        )
                    }
                }
                v.performClick()
                false
            }
        }
    }

    private fun execHideShowForGGleSchWebview(
        hideShowThreshold: Int,
        oldPositionY: Float,
        rawY: Float,
    ) {
        val oldCurrYDff = oldPositionY - rawY
        if(hideShowThreshold < oldCurrYDff && oldCurrYDff < -10){
            ggleSchButton?.isVisible = true
            ggleSchButtonCaption?.isVisible = true
        }
        if(oldCurrYDff > 10) {
            ggleSchButton?.isVisible = false
            ggleSchButtonCaption?.isVisible = false
        }
    }
}
