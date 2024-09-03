package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog


import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.PopupMenu
import android.widget.ProgressBar
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecSetToolbarButtonImage
import com.puutaro.commandclick.activity_lib.manager.AdBlocker
import com.puutaro.commandclick.common.variable.broadcast.extra.PocketWebviewExtra
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeTerm
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.res.CmdClickColor
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.ExecDownLoadManager
import com.puutaro.commandclick.fragment_lib.terminal_fragment.WebChromeClientSetter
import com.puutaro.commandclick.fragment_lib.terminal_fragment.WrapWebHistoryUpdater
import com.puutaro.commandclick.fragment_lib.terminal_fragment.html.TxtHtmlDescriber
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog_js_interface.JsWebViewDialogManager
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.JsInterfaceAdder
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.LongPressForImage
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.LongPressForSrcAnchor
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.LongPressForSrcImageAnchor
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.ScrollPosition
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.libs.ExecJsInterfaceAdder
import com.puutaro.commandclick.proccess.broadcast.BroadCastIntent
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.intent.lib.JavascriptExecuter
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.datetime.LocalDatetimeTool
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.image_tools.ScreenSizeCalculator
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.util.str.ScriptPreWordReplacer
import com.puutaro.commandclick.util.url.WebUrlVariables
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.File
import java.lang.ref.WeakReference
import java.time.LocalDateTime


class WebViewJsDialog(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {
    private val autoFocusGgleSearchUrl = WebUrlVariables.autoFocusGgleSearchUrl
    private val longpressMenuGroupId = 110000
    private val clickMenuGroupId = 120000
    private var webViewDialogExtraMapManager: WebViewDialogExtraMapManager? = null
    private val positionHashMap = hashMapOf<String, Int>()
    private var currentScriptPath = String()
    private var longPressMenuMapListStr = String()
    private var noShowKeyBoardForPreloadAutoGgle = true
    private var webViewDialogInstance: Dialog? = let {
        val terminalFragment = terminalFragmentRef.get()
        val context = terminalFragment?.context
        val webViewDialog = Dialog(context as Context)
        webViewDialog.setContentView(
            R.layout.dialog_webview_layout
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
            R.id.webview_dialog_layout
        )
    private var progressBar = webViewDialogInstance?.findViewById<ProgressBar>(
        R.id.dialog_webview_progressBar
    )
    private var firstBottomLinearLayout = webViewDialogInstance?.findViewById<LinearLayoutCompat>(
        R.id.first_bottom_linearlayout
    )
    var pocketWebView = let {
        val pocketWebViewSrc = webViewDialogInstance?.findViewById<WebView>(
            R.id.webview_dialog_webview
        )
        val terminalFragment = terminalFragmentRef.get()
        ExecDownLoadManager.set(
            terminalFragment,
            pocketWebViewSrc
        )
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
        webViewLongClickListener(
            pocketWebViewSrc,
        )
        pocketWebViewSrc?.loadUrl(autoFocusGgleSearchUrl)
        pocketWebViewSrc?.isVisible = false
        pocketWebViewSrc?.onPause()
        pocketWebViewSrc
    }

    fun show(
        urlStrSrc: String,
        currentScriptPathSrc: String,
        menuMapStrListStr: String,
        longPressMenuMapListStrSrc: String,
        extraMapCon: String,
    ) {
        noShowKeyBoardForPreloadAutoGgle = false
        positionHashMap.clear()
        PocketWebviewGoBack.initPrevBackTime()
        currentScriptPath = currentScriptPathSrc
        longPressMenuMapListStr = longPressMenuMapListStrSrc
        CoroutineScope(Dispatchers.Main).launch{
            withContext(Dispatchers.Main){
                startWebView()
            }
            val terminalFragment = terminalFragmentRef.get()
            when (urlStrSrc == GglePreFocusJs.gglePreFocusMacro) {
                true ->
                    CoroutineScope(Dispatchers.IO).launch autoFocus@ {
                        withContext(Dispatchers.IO){
                            for(i in 1..5){
                                val isFocus = withContext(Dispatchers.Main) {
                                    pocketWebView?.requestFocus() == true
                                }
                                delay(50)
                                if(
                                    isFocus
                                ) break
                            }
                        }
                        GglePreFocusJs.loadGglePreFocusJs(
                            terminalFragment?.context,
                            pocketWebView,
                        )
                    }
                else -> {
                    val urlStr = if(
                        urlStrSrc.startsWith(WebUrlVariables.slashPrefix)
                        || urlStrSrc.startsWith(WebUrlVariables.filePrefix)
                        || urlStrSrc.startsWith(WebUrlVariables.httpPrefix)
                        || urlStrSrc.startsWith(WebUrlVariables.httpsPrefix)
                        || urlStrSrc.startsWith(WevViewDialogUriPrefix.TEXT_CON.prefix)
                    ) urlStrSrc
                    else "${WebUrlVariables.queryUrl}${urlStrSrc}"
                    loadUrlHandler(
                        terminalFragment,
                        urlStr,
                    )
                }

            }
            val menuMapStrList = QuoteTool.splitBySurroundedIgnore(
                menuMapStrListStr,
                '|'
            )
            val btnWeight = withContext(Dispatchers.IO) {
                culcBtnWeight(menuMapStrList)
            }
            withContext(Dispatchers.IO) {
                webViewDialogExtraMapManager = null
                webViewDialogExtraMapManager = WebViewDialogExtraMapManager(
                    menuMapStrList,
                    extraMapCon,
                    firstBottomLinearLayout,
                )
            }
            withContext(Dispatchers.IO) {
                menuMapStrList.forEach {
                    val btnMenuMap = makeBtnOptionMap(
                        it
                    )
                    withContext(Dispatchers.Main) {
                        webViewBottomBtnSetter(
                            terminalFragment,
                            btnMenuMap,
                            btnWeight,
                        )?.let {
                            firstBottomLinearLayout?.addView(it)
                        }
                    }
                }
            }
            webViewDialogInstance?.show()
        }
    }

    private fun loadUrlHandler(
        terminalFragment: TerminalFragment?,
        urlCon: String,
    ){
        if(
            terminalFragment == null
        ) return
        val textConWevViewDialogUriPrefix = WevViewDialogUriPrefix.TEXT_CON.prefix
        val mdConWevViewDialogUriPrefix = WevViewDialogUriPrefix.MD_CON.prefix
        val trimUrlCon = urlCon.trim()
        when(true){
            trimUrlCon.startsWith(textConWevViewDialogUriPrefix) -> {
                val textUrl = WevViewDialogUriPrefix.TEXT_CON.virtualUrl
                val removePrefixCon = trimUrlCon.removePrefix(textConWevViewDialogUriPrefix)
                pocketWebView?.loadDataWithBaseURL(
                    textUrl,
                    TxtHtmlDescriber.make(
                        removePrefixCon,
                        terminalFragment
                    ),
                    "text/html",
                    "utf-8",
                    textUrl
                )
            }
            trimUrlCon.startsWith(mdConWevViewDialogUriPrefix) -> {
                val mdUrl = WevViewDialogUriPrefix.MD_CON.virtualUrl
                val removePrefixCon = trimUrlCon.removePrefix(mdConWevViewDialogUriPrefix)
                pocketWebView?.loadDataWithBaseURL(
                    mdUrl,
                    removePrefixCon,
                    "text/html",
                    "utf-8",
                    mdUrl
                )
            }
            else -> pocketWebView?.loadUrl(
//                    "https://www.google.co.id/search?q=%20"
                trimUrlCon
            )
        }
    }

    private object GglePreFocusJs {

//        private val autoFocusGgleSearchUrl = WebUrlVariables.autoFocusGgleSearchUrl
        val gglePreFocusMacro = "GGLE_SEARCH"

//        suspend fun execPageFinishJs(
//            context: Context?,
//            webView: WebView?,
//            url: String?
//        ){
//            if(
//                url.isNullOrEmpty()
//            ) return
//            if(
//                url != autoFocusGgleSearchUrl
//            ) return
//            withContext(Dispatchers.IO) {
//                delay(200)
//            }
//            loadGglePreFocusJs(
//                context,
//                webView,
//            )
//        }

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
            pocketWebView?.onResume()
        } catch (e: Exception){
            print("pass")
        }
        pocketWebView?.isVisible = true
        firstBottomLinearLayout?.isVisible = true
    }
    fun stopWebView(
    ){
        val isAutoFocusUrl =
            pocketWebView?.url?.contains(WebUrlVariables.autoFocusGgleSearchUrl) == true
        firstBottomLinearLayout?.removeAllViews()
        pocketWebView?.onPause()
        pocketWebView?.isVisible = false
        if(!isAutoFocusUrl) {
            pocketWebView?.webChromeClient = null
            pocketWebView?.clearHistory()
            pocketWebView?.clearCache(true)
            pocketWebView?.removeAllViews()
            pocketWebView?.destroy()
            positionHashMap.clear()
            firstBottomLinearLayout?.isVisible = false
            webviewDialogLayout?.removeAllViews()
        }
        webViewDialogInstance?.dismiss()
        if(!isAutoFocusUrl) {
            webViewDialogInstance = null
            val terminalFragment = terminalFragmentRef.get()
            noShowKeyBoardForPreloadAutoGgle = true
            terminalFragment?.pocketWebViewManager = WebViewJsDialog(terminalFragmentRef)
        }
    }

    fun destroyWebView(){
        pocketWebView?.onPause()
        pocketWebView?.isVisible = false
        pocketWebView?.webChromeClient = null
        pocketWebView?.clearHistory()
        pocketWebView?.clearCache(true)
        pocketWebView?.removeAllViews()
        pocketWebView?.destroy()
        positionHashMap.clear()
        firstBottomLinearLayout?.isVisible = false
        webviewDialogLayout?.removeAllViews()
        webViewDialogInstance?.dismiss()
        webViewDialogInstance = null
    }

    private fun webViewSetting(
        terminalFragment: TerminalFragment?,
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

    private fun webViewBottomBtnSetter(
//        webView: WebView,
        terminalFragment: TerminalFragment?,
        targetMenuMap: Map<String, String>?,
        culcBtnWeight: Float,
//        currentScriptPath: String,
//        webViewDialogExtraMapManager: WebViewDialogExtraMapManager,
    ): LinearLayoutCompat? {
        val targetBtn = makeBottomButton(
            terminalFragment,
            targetMenuMap,
            culcBtnWeight,
//            webViewDialogExtraMapManager
        )
        val clickMenuList = makeMenu(
            targetMenuMap,
            WebViewMenuMapType.clickMenuFilePath.name,
        )
        val longPressMenuList = makeMenu(
            targetMenuMap,
            WebViewMenuMapType.longPressMenuFilePath.name,
        )
        targetBtn?.setOnClickListener{
            btnActionHandler(
                terminalFragment,
                targetBtn,
                targetMenuMap,
                clickMenuList,
                DismissType.click.name,
                clickMenuGroupId,
            )
            webViewDialogExtraMapManager?.trigger(
                terminalFragment?.context,
                false,
                targetMenuMap?.get(WebViewMenuMapType.tag.name),
            )
        }
        targetBtn?.setOnLongClickListener{
            btnActionHandler(
                terminalFragment,
                targetBtn,
                targetMenuMap,
                longPressMenuList,
                DismissType.longpress.name,
                longpressMenuGroupId,
            )
            webViewDialogExtraMapManager?.trigger(
                terminalFragment?.context,
                true,
                targetMenuMap?.get(WebViewMenuMapType.tag.name),
            )
            true
        }
        return targetBtn
    }

    private fun btnActionHandler(
        terminalFragment: TerminalFragment?,
        targetBtn: LinearLayoutCompat,
        btnOptionMap: Map<String, String>?,
        menuList: List<String>,
        dismissType: String,
        menuGroupId: Int,
    ){
        launchMenu(
            terminalFragment,
            targetBtn,
            menuList,
            menuGroupId,
        )
        dismissHandler(
            btnOptionMap,
            dismissType,
        )
    }

    private fun highLightSearch(
        context: Context?,
//        webView: WebView,
    ){
        val jsContents = AssetsFileManager.readFromAssets(
            context,
            AssetsFileManager.assetsHighlightSchForDialogWebViewPath
        ).split("\n")
        val jsScriptUrl = JavaScriptLoadUrl.makeFromContents(
            context,
            jsContents
        ) ?: return
        pocketWebView?.loadUrl(jsScriptUrl)
    }

    private fun launchUrlAtLocal(
        terminalFragment: TerminalFragment?,
    ){
        val currentUrl = pocketWebView?.url
            ?: return
        pocketWebView?.let {
            ScrollPosition.save(
                terminalFragment,
                it,
                currentUrl,
                it.scrollY,
                100f,
                0f,
            )
        }
        stopWebView()
        terminalFragment?.binding?.terminalWebView?.loadUrl(currentUrl)
    }

    private fun launchMenu(
        terminalFragment: TerminalFragment?,
        webViewSearchBtn: LinearLayoutCompat,
        menuList: List<String>,
        menuGroupId: Int,
    ){
        if (
            menuList.isEmpty()
        ) return
        val context = terminalFragment?.context ?: return
        if(menuList.size == 1){
            jsOrMacroHandler(
                terminalFragment,
                getJsPathFromMenuSrc(menuList.first()),
            )
            return
        }
        val popupMenu = PopupMenu(
            context,
            webViewSearchBtn,
        )
        popupMenu.menu.clear()
        val inflater = popupMenu.menuInflater
        inflater.inflate(
            R.menu.history_admin_menu,
            popupMenu.menu
        )
        (menuList.indices).forEach {
            val menuName = getTitleFromMenuSrc(menuList[it])
            val itemId = menuGroupId + it * 100
            popupMenu.menu.add(
                menuGroupId,
                itemId,
                it,
                menuName
            )
        }
        popupMenuItemSelected(
            terminalFragment,
            popupMenu,
            menuList,
        )
        popupMenu.show()
    }

    private fun popupMenuItemSelected(
        terminalFragment: TerminalFragment?,
        popup: PopupMenu,
        menuList: List<String>,
    ){
        popup.setOnMenuItemClickListener {
                menuItem ->
            val selectedMenuStr = menuItem.title.toString()
            val selectedJsPath = getJsPathFromSelectedMenuStr(
                selectedMenuStr,
                menuList,
            )
            jsOrMacroHandler(
                terminalFragment,
                selectedJsPath,
            )
            true
        }
    }

    private fun getJsPathFromSelectedMenuStr(
        selectedMenuStr: String,
        menuList: List<String>,
    ): String {
        val selectedMenuLine = menuList.firstOrNull {
            it.split("\t").firstOrNull() == selectedMenuStr
        } ?: return String()
        return getJsPathFromMenuSrc(selectedMenuLine)
    }


    private fun getJsPathFromMenuSrc(
        menuText: String
    ): String {
        return menuText.split("\t").lastOrNull()?.let {
            QuoteTool.trimBothEdgeQuote(it)
        } ?: String()
    }

    private fun getTitleFromMenuSrc(
        menuText: String
    ): String {
        return menuText.split("\t").firstOrNull()?.let {
            QuoteTool.trimBothEdgeQuote(it)
        } ?: String()
    }

    private fun webViewClientSetter(
        terminalFragment: TerminalFragment?,
        pocketWebView: WebView?
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
                val isLoadLocal = webViewDialogExtraMapManager?.loadAtLocal(
                    context,
                    requestUrl.toString()
                ) == true
                if(isLoadLocal) {
                    stopWebView()
                    return true
                }
                positionHashMap.put(
                    "${view?.url}",
                    view?.scrollY ?: 0
                )
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
                positionHashMap.get(url)?.let {
                    CoroutineScope(Dispatchers.Main).launch {
                        withContext(Dispatchers.IO){
                            delay(300)
                        }
                        webview?.scrollY = it
                    }
                }
                FileSystems.updateFile(
                    File(UsePath.cmdclickDefaultAppDirPath, "history.txt").absolutePath,
                    "${webview?.title}\t${url.toString()}",
                )
                WrapWebHistoryUpdater.updateForPocketWebview(
                    terminalFragment,
                    webview,
                    url,
                    previousUrl
                )
                if(
                    previousUrl?.length != url?.length
                ) {
                    previousUrl = url
                }
//                urlToFinishJs?.execPageFinishJs(
//                    context,
//                    webView,
//                    url,
//                )
                super.onPageFinished(webview, url)
            }
        }
    }


    private fun makeBtnOptionMap(
//        currentScriptPath: String,
        centerMenuMapStr: String
    ): Map<String, String>? {
        val currentScriptPathObj = File(currentScriptPath)
        if(
            !currentScriptPathObj.isFile
            && currentScriptPath.isNotEmpty()
        ) return null
        val fannelName =
            currentScriptPathObj.name
                ?: String()

        return ScriptPreWordReplacer.replace(
            centerMenuMapStr,
            fannelName
        ).let {
            CmdClickMap.createMap(
                it,
                '?'
            )
        }.toMap()
    }

    private fun webViewLongClickListener(
        pocketWebViewSrc: WebView?
    ){
        pocketWebViewSrc?.setOnLongClickListener { view ->
            firstBottomLinearLayout?.isVisible = true
            val lContext = view.context
            val hitTestResult = pocketWebView?.hitTestResult
            val title = pocketWebView?.title
            val currentPageUrl = pocketWebView?.url
            val longPressMenuMap = makeBtnOptionMap(
//                currentScriptPath,
                longPressMenuMapListStr
            )
            val httpsStartStr = WebUrlVariables.httpsPrefix
            val httpStartStr = WebUrlVariables.httpPrefix
            when (hitTestResult?.type) {
                WebView.HitTestResult.IMAGE_TYPE -> {
                    if (
                        currentPageUrl?.startsWith(httpsStartStr) == true
                        || currentPageUrl?.startsWith(httpStartStr) == true
                    ) {
                        val menuFilePath = longPressMenuMap?.get(
                            WebDialogLongPressType.imageMenuFilePath.name
                        ) ?: return@setOnLongClickListener false
                        val longPressImageUrl = hitTestResult.extra
                            ?: return@setOnLongClickListener false
                        LongPressForImage(
                            terminalFragmentRef,
                            lContext,
                            menuFilePath
                        ).launch(
                            title,
                            longPressImageUrl,
                            currentPageUrl
                        )
                    }
                    false
                }
                WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE -> {
                    val message = Handler(Looper.getMainLooper()).obtainMessage()
                    pocketWebView?.requestFocusNodeHref(message)

                    val longPressLinkUrl = message.data.getString("url")
                        ?: return@setOnLongClickListener false
                    val longPressImageUrl = message.data.getString("src")
                        ?: return@setOnLongClickListener  false
                    if (
                        currentPageUrl?.startsWith(httpsStartStr) == true
                        || currentPageUrl?.startsWith(httpStartStr) == true
                    ) {
                        val menuFilePath = longPressMenuMap?.get(
                            WebDialogLongPressType.srcImageAnchorMenuFilePath.name
                        ) ?: return@setOnLongClickListener false
                        LongPressForSrcImageAnchor(
                            terminalFragmentRef,
                            lContext,
                            menuFilePath
                        ).launch(
                            title,
                            longPressLinkUrl,
                            longPressImageUrl,
                            currentPageUrl
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
                        val menuFilePath = longPressMenuMap?.get(
                            WebDialogLongPressType.srcAnchorMenuFilePath.name
                        ) ?: return@setOnLongClickListener false
                        LongPressForSrcAnchor(
                            terminalFragmentRef,
                            lContext,
                            menuFilePath
                        ).launch(
                            title,
                            longPressLinkUrl,
                            currentPageUrl
                        )
                    }
                    false
                }
                else -> false
            }
        }
    }

    private fun dismissHandler(
//        webView: WebView,
        btnOptionMap: Map<String, String>?,
        triggerType: String,
    ){
        val dismissType = getDismissType(
            btnOptionMap
        )
        if(
            dismissType == triggerType
            || dismissType == DismissType.both.name
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.IO) {
                    val delayMiliTime = getDismissDelayMiliTime(
                        btnOptionMap
                    )
                    delay(delayMiliTime)
                }
                withContext(Dispatchers.Main) {
                    stopWebView()
                }
            }
        }
    }

    private fun makeBottomButton(
        terminalFragment: TerminalFragment?,
        menuBtnMap: Map<String, String>?,
        buttonWeight: Float,
    ): LinearLayoutCompat? {
        val context = terminalFragment?.context ?: return null

        val inflater = LayoutInflater.from(context)
        val linearLayoutCompat = inflater.inflate(
            R.layout.icon_caption_layout,
            null
        ) as LinearLayoutCompat
        val tag = menuBtnMap
            ?.get(
                WebViewMenuMapType.tag.name
            ) ?: "exec"
        linearLayoutCompat.tag = tag
        val param = LinearLayoutCompat.LayoutParams(
            0,
            LinearLayoutCompat.LayoutParams.WRAP_CONTENT
        )
        param.weight = buttonWeight
        linearLayoutCompat.layoutParams = param
        webViewDialogExtraMapManager?.setDefaultFocus(
            context,
            linearLayoutCompat,
            tag,
        )
        val imageView =
            linearLayoutCompat.findViewById<AppCompatImageView>(R.id.icon_caption_layout_image)

        val iconName = menuBtnMap
            ?.get(
                WebViewMenuMapType.iconName.name
            ) ?: "exec"
        makeImageView(
            imageView,
            iconName,
        )
        val caption = menuBtnMap?.get(
            WebViewMenuMapType.caption.name
        ) ?: iconName

        val captionTextView =
            linearLayoutCompat.findViewById<OutlineTextView>(R.id.icon_caption_layout_caption)

        makeTextView(
            captionTextView,
            iconName,
            caption,
        )
        return linearLayoutCompat

    }

    private fun makeTextView(
        captionTextView: OutlineTextView,
        iconName: String,
        caption: String,
    ) {
        val icon = CmdClickIcons.values().firstOrNull {
            it.str == iconName
        } ?: CmdClickIcons.OK
        captionTextView.text = caption.ifEmpty { icon.str }
        captionTextView.setStrokeColor(R.color.white)
        captionTextView.setFillColor(R.color.web_icon_color)
    }

    private fun makeImageView(
        imageView: AppCompatImageView,
        iconName: String,
    ) {
        val imageViewContext = imageView.context
        val icon = CmdClickIcons.values().firstOrNull {
            it.str == iconName
        } ?: CmdClickIcons.OK
        imageView.imageTintList = null
        imageView.backgroundTintList = imageViewContext.getColorStateList(R.color.white)
        val requestBuilder: RequestBuilder<Drawable> =
            Glide.with(imageViewContext)
                .asDrawable()
                .sizeMultiplier(0.1f)
        val isImageFile =
            ExecSetToolbarButtonImage.isImageFile(icon.assetsPath)
        when(isImageFile) {
            true -> {
                val imagePath = ExecSetToolbarButtonImage.getImageFile(icon.assetsPath)
                Glide.with(imageViewContext)
                    .load(imagePath)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .thumbnail(requestBuilder)
                    .into(imageView)
            }
            else ->
                Glide.with(imageViewContext)
                    .load(icon.id)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .thumbnail(requestBuilder).into(imageView)
        }
    }

    private fun getDismissType(
        btnOptionMap:  Map<String, String>?,
    ): String {
        return btnOptionMap
            ?.get(
                WebViewMenuMapType.dismissType.name
            ) ?: String()
    }

    private fun getDismissDelayMiliTime(
        btnOptionMap:  Map<String, String>?,
    ): Long {
        return try {
            btnOptionMap
                ?.get(
                    WebViewMenuMapType.dismissDelayMiliTime.name
                )?.toLong() ?: 0L
        } catch (e: Exception){
            0L
        }
    }
    private fun makeMenu(
        btnOptionMap: Map<String, String>?,
        listType: String
    ): List<String> {
        val menuFilePath = btnOptionMap
            ?.get(
                listType
            ) ?: String()
        val commentOutMark = JavaScriptLoadUrl.commentOutMark
        val isJsPath = menuFilePath.endsWith(UsePath.JS_FILE_SUFFIX)
        return when(isJsPath){
            true -> listOf(
                "${menuFilePath}\t${menuFilePath}"
            )
            else -> ReadText(
                menuFilePath
            ).textToList().filter {
                val timeLine = it.trim()
                !timeLine.startsWith(commentOutMark)
                        && !timeLine.startsWith("#")
                        && timeLine.isNotEmpty()
            }
        }
    }

    private fun culcBtnWeight(
        btnMenuMapStrList: List<String>
    ): Float {
        return 1.0f / btnMenuMapStrList.size
    }


    private fun jsOrMacroHandler(
        terminalFragment: TerminalFragment?,
        jsPath: String,
    ){
        val macro = JsMacroType.values().firstOrNull {
            it.str == jsPath
        }
        if(macro == null){
            execLoadJs(
                terminalFragment,
                jsPath,
            )
            return
        }
        if(
            terminalFragment == null
        ) return
        when(macro){
            JsMacroType.GO_BACK_JS
            -> execGoBack()
            JsMacroType.GO_FORWARD_JS
            -> execGoForward()
            JsMacroType.HIGHLIGHT_SCH_JS -> highLightSearch(
                terminalFragment.context,
            )
            JsMacroType.LAUNCH_LOCAL_JS
            -> launchUrlAtLocal(
                terminalFragment
            )
            JsMacroType.HIGHLIGHT_COPY_JS
            -> assetsCopy(
                terminalFragment.context
            )
            JsMacroType.OPEN_SRC_JS_ACTION_REPORT
            -> DebugReport.openSrcJsActionReport(terminalFragment)
            JsMacroType.OPEN_GENERATED_JS_ACTION_REPORT
            -> DebugReport.openGenJsActionReport(terminalFragment)
            JsMacroType.OPEN_JS_REPORT
            -> DebugReport.openJsReport(terminalFragment)

        }
    }


    private object DebugReport {
        fun openSrcJsActionReport(
            terminalFragment: TerminalFragment,
        ) {
            openDebugReport(
                terminalFragment,
                UsePath.jsSrcAcDebugReportPath
            )
        }

        fun openGenJsActionReport(
            terminalFragment: TerminalFragment,
        ) {
            openDebugReport(
                terminalFragment,
                UsePath.jsGenAcDebugReportPath
            )
        }


        fun openJsReport(
            terminalFragment: TerminalFragment
        ) {
            openDebugReport(
                terminalFragment,
                UsePath.jsDebugReportPath
            )
        }

        private fun openDebugReport(
            terminalFragment: TerminalFragment,
            debugPath: String,
        ) {
            val launchUrl = TxtHtmlDescriber.makeTxtHtmlUrl(
                debugPath,
            )
            val extraStrPairList = listOf(
                PocketWebviewExtra.url.schema
                        to launchUrl
            )
            BroadcastSender.normalSend(
                terminalFragment.context,
                BroadCastIntentSchemeTerm.POCKET_WEBVIEW_LOAD_URL.action,
                extraStrPairList,
            )
        }
    }

    private fun execLoadJs(
        terminalFragment: TerminalFragment?,
        jsPath: String,
    ){
        val context = terminalFragment?.context
        val setReplaceVariableMap = SetReplaceVariabler.makeSetReplaceVariableMapFromSubFannel(
            context,
            currentScriptPath,
        )
        val fannelPath = CcPathTool.getMainFannelFilePath(
            currentScriptPath
        )
        val fannelPathObj = File(fannelPath)
        if(!fannelPathObj.isFile) return
        val fannelName = fannelPathObj.name
        val execJsPath = SetReplaceVariabler.execReplaceByReplaceVariables(
            jsPath,
            setReplaceVariableMap,
            fannelName
        )
        if(terminalFragment == null) return
        JavascriptExecuter.jsOrActionHandler(
            terminalFragment,
            execJsPath,
            ReadText(execJsPath).textToList(),
            webView = pocketWebView
        )
    }

    private fun execGoBack(
    ){
        val isDismiss = PocketWebviewGoBack.backOrDismiss(pocketWebView)
        if(isDismiss) stopWebView()
    }

    private object PocketWebviewGoBack {

        private var prevBackTime = LocalDateTime.parse("2020-02-15T21:30:50")

        fun initPrevBackTime(){
            prevBackTime = LocalDateTime.parse("2020-02-15T21:30:50")
        }

        fun backOrDismiss(
            webView: WebView?
        ): Boolean {
            if (
                webView?.canGoBack() == true
            ) {
                webView.goBack()
                initPrevBackTime()
                return false
            }


            val curBackTime = LocalDateTime.now()
            val isNotDismiss = LocalDatetimeTool.getDurationSec(prevBackTime, curBackTime) > 5
            if(isNotDismiss){
                prevBackTime = curBackTime
                ToastUtils.showShort("Dismiss by next tap")
                return false
            }
            return true
        }
    }

    private fun execGoForward(
//        webView: WebView
    ){
        if(
            pocketWebView?.canGoForward() != true
        ) return
        pocketWebView?.goForward()
    }

    private fun assetsCopy(
        context: Context?
    ){
        val jsContents = AssetsFileManager.readFromAssets(
            context,
            AssetsFileManager.assetsHighlightCopy
        ).split("\n")
        val jsScriptUrl = JavaScriptLoadUrl.makeFromContents(
            context,
            jsContents
        ) ?: return
        pocketWebView?.loadUrl(jsScriptUrl)
    }

    private fun toolbarHideShow(
        terminalFragment: TerminalFragment?,
        pocketWebViewSrc: WebView?,
    ){
        if(
            pocketWebViewSrc == null
        ) return
        var oldPositionY = 0f
        val hideShowThreshold = getScreenHeight(terminalFragment?.activity)
        with(pocketWebViewSrc){
            setOnTouchListener {
                    v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        oldPositionY = event.rawY
                        v.performClick()
                    }
                    MotionEvent.ACTION_UP -> {
                        execHideShowForPocketWebview(
                            hideShowThreshold,
                            oldPositionY,
                            event.rawY,
                            firstBottomLinearLayout
                        )
                    }
                }
                v.performClick()
                false
            }
        }
    }
}

private fun execHideShowForPocketWebview(
    hideShowThreshold: Int,
    oldPositionY: Float,
    rawY: Float,
    firstBottomLinearLayout: LinearLayoutCompat?
) {
    val oldCurrYDff = oldPositionY - rawY
    if(hideShowThreshold < oldCurrYDff && oldCurrYDff < -10){
        firstBottomLinearLayout?.isVisible = true
    }
    if(oldCurrYDff > 10) {
        firstBottomLinearLayout?.isVisible = false
    }
}
private fun getScreenHeight(
    activity: FragmentActivity?,
): Int {
    val dpHeight = ScreenSizeCalculator.dpHeight(
        activity
    )
    val hideShowRate =
        if(dpHeight > 670f) 3.0f
        else if(dpHeight > 630) 3.5F
        else 4.0f
    return -(dpHeight / hideShowRate).toInt()
}

enum class WebViewMenuMapType {
    clickMenuFilePath,
    longPressMenuFilePath,
    dismissType,
    dismissDelayMiliTime,
    caption,
    iconName,
    tag,
}

private enum class WebDialogLongPressType {
    srcImageAnchorMenuFilePath,
    srcAnchorMenuFilePath,
    imageMenuFilePath,
}

private enum class DismissType {
    longpress,
    click,
    both,
}

enum class JsMacroType(val str: String) {
    HIGHLIGHT_SCH_JS("HIGHLIGHT_SCH.js"),
    GO_BACK_JS("GO_BACK.js"),
    GO_FORWARD_JS("GO_FORWARD.js"),
    LAUNCH_LOCAL_JS("LAUNCH_LOCAL.js"),
    HIGHLIGHT_COPY_JS("HIGHLIGHT_COPY.js"),
    OPEN_SRC_JS_ACTION_REPORT("OPEN_SRC_JS_ACTION_REPORT.js"),
    OPEN_GENERATED_JS_ACTION_REPORT("OPEN_GENERATED_JS_ACTION_REPORT.js"),
    OPEN_JS_REPORT("OPEN_JS_REPORT.js"),
}

enum class WevViewDialogUriPrefix(
    val prefix: String,
    val virtualUrl: String,
) {
    TEXT_CON("textCon://", "${WebUrlVariables.filePrefix}///textCon.txt"),
    MD_CON("mdCon://", "${WebUrlVariables.filePrefix}///descMd.txt"),
}

private class WebViewDialogExtraMapManager(
    menuMapStrList: List<String>,
    extraMapCon: String,
    private val firstBottomLinearLayout: LinearLayoutCompat?
) {
    private var extraMap = emptyMap<String, String>()
    private var focusMap = emptyMap<String, String>()

    companion object {
        private const val keySeparator = '|'
        private var bottomBtnTagList = emptyList<String>()

        private enum class MainKeys(
            val key: String
        ) {
            FOCUS("focus"),
            LOAD_LOCAL("load_local"),
        }

        private val loadLocalOn = "ON"

        object FocusManager {

            const val focusKeySeparator = '?'
            val focusColorId = CmdClickColor.LIGHT_AO.id

            enum class FocusKey(
                val key: String,
            ){
                TRIGGERS("triggers"),
                DEFAULT_TAG("defaultTag"),
            }

            enum class TriggerType(
                val type: String
            ) {
                LONG_CLICK("longClick"),
                CLICK("click")

            }
        }
    }

    init {
        init(
            extraMapCon,
            menuMapStrList
        )
    }
    private fun init(
        extraMapCon: String,
        menuMapStrList: List<String>
    ){
        setExtraMap(extraMapCon)
        setFocusMap(extraMap)
        makeBottomButtonTagList(menuMapStrList)
    }

    private fun setExtraMap(extraMapCon: String) {
        extraMap = CmdClickMap.createMap(
            extraMapCon,
            keySeparator,
        ).toMap()
    }

    fun loadAtLocal(
        context: Context?,
        url: String?
    ): Boolean {
        val isNotLocalAtLocal = extraMap.get(
            MainKeys.LOAD_LOCAL.key
        ) != loadLocalOn
        if(
            isNotLocalAtLocal
        ) return false
        if(
            url.isNullOrEmpty()
        ) return false
        if(
            WebUrlVariables.isGgleSearchIndexPage(url)
        ) return false
        val query = url.removePrefix(
            WebUrlVariables.queryUrl
        ).trim()
        val isNotSearchUrl =
            query.isEmpty() || query == WebUrlVariables.blankEncodeQuery
        if(isNotSearchUrl) {
            return false
        }
        BroadCastIntent.sendUrlCon(
            context,
            url
        )
        return true

    }

    private fun makeBottomButtonTagList(
        menuMapStrList: List<String>
    ) {
        bottomBtnTagList = menuMapStrList.map {
            val menuMap = CmdClickMap.createMap(
                it,
                '?'
            ).toMap()
            menuMap.get(WebViewMenuMapType.tag.name)
                ?: String()
        }.filter { it.isNotEmpty() }
    }

    fun setFocusMap(extraMap: Map<String, String>) {
        val focusMapCon = extraMap.get(
            MainKeys.FOCUS.key
        )
        focusMap = CmdClickMap.createMap(
            focusMapCon,
            FocusManager.focusKeySeparator
        ).toMap()
    }
    fun setDefaultFocus(
        context: Context?,
        button: LinearLayoutCompat,
        curTag: String,
    ){
        val isNotDefaultTag = focusMap.get(
            FocusManager.FocusKey.DEFAULT_TAG.key
        ) != curTag
        if(
            isNotDefaultTag
        ) return
        setBackground(
            context,
            button,
            FocusManager.focusColorId
        )
    }

    fun trigger(
        context: Context?,
        isLongClick: Boolean,
        curTag: String?,
    ){
        if(
            curTag.isNullOrEmpty()
        ) return
        val isNotTriggerTag =
            !bottomBtnTagList.contains(curTag)
        if(
            isNotTriggerTag
        ) return
        val triggerType = when(isLongClick){
            true -> FocusManager.TriggerType.LONG_CLICK.type
            false -> FocusManager.TriggerType.CLICK.type
        }
        val triggerList =
            focusMap.get(FocusManager.FocusKey.TRIGGERS.key)
                ?.split("&")
                ?: return
        val isNotTrigger = !triggerList.contains(triggerType)
        if(
            isNotTrigger
        ) return
        val button = firstBottomLinearLayout
            ?.findViewWithTag<LinearLayoutCompat>(curTag)
        setBackground(
            context,
            button,
            FocusManager.focusColorId
        )
        bottomBtnTagList.forEach {
            if(it == curTag) return@forEach
            val curButton = firstBottomLinearLayout
                ?.findViewWithTag<LinearLayoutCompat>(it)
            setBackground(
                context,
                curButton,
                CmdClickColor.WHITE.id
            )
        }
    }

    private fun setBackground(
        context: Context?,
        button: LinearLayoutCompat?,
        colorId: Int,
    ){
        when(colorId == CmdClickColor.WHITE.id) {
            true -> {
                button?.background = null
                button?.alpha = 1f
            }
            else -> {
                button?.background =
                    AppCompatResources.getDrawable(context as Context, colorId)
                button?.alpha = 0.6f
            }
        }
    }
}