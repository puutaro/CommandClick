package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog


import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.ProgressBar
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.google.android.material.button.MaterialButton
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity_lib.manager.AdBlocker
import com.puutaro.commandclick.common.variable.broadcast.extra.PocketWebviewExtra
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeTerm
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.res.CmdClickColor
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.util.url.WebUrlVariables
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
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.intent.lib.JavascriptExecuter
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.str.ScriptPreWordReplacer
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.File


class WebViewJsDialog(
    private val terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
    private val activity = terminalFragment.activity
    private val fannelInfoMap = terminalFragment.fannelInfoMap
    private val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
        fannelInfoMap
    )
    private val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
    private val longpressMenuGroupId = 110000
    private val clickMenuGroupId = 120000
    private val positionHashMap = hashMapOf<String, Int>()

    companion object {
        fun loadUrlHandler(
            terminalFragment: TerminalFragment,
            webView: WebView,
            urlCon: String,
        ){
            val textConWevViewDialogUriPrefix = WevViewDialogUriPrefix.TEXT_CON.prefix
            val mdConWevViewDialogUriPrefix = WevViewDialogUriPrefix.MD_CON.prefix
            val trimUrlCon = urlCon.trim()
            when(true){
                trimUrlCon.startsWith(textConWevViewDialogUriPrefix) -> {
                    val textUrl = WevViewDialogUriPrefix.TEXT_CON.virtualUrl
                    val removePrefixCon = trimUrlCon.removePrefix(textConWevViewDialogUriPrefix)
                    webView.loadDataWithBaseURL(
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
                    webView.loadDataWithBaseURL(
                        mdUrl,
                        removePrefixCon,
                        "text/html",
                        "utf-8",
                        mdUrl
                    )
                }
                else -> webView.loadUrl(trimUrlCon)
            }
        }
    }

    private class UrlToFinishJs {

        private var pageFinishJs = String()

        companion object {
            private const val autoFocusGgleSearchUrl = "https://www.google.co.id/search?q=%20"
        }
        private enum class UrlMacro(
            val macro: String,
            val url: String,
            val pageFinishedJsAssetsPath: String,
        ) {
            GGLE_SEARCH(
                "GGLE_SEARCH",
                autoFocusGgleSearchUrl,
                String()
            ),
        }

        fun makePair(
            urlStrSrc: String
        ): Pair<String, String> {
            val macroEnum = UrlMacro.values().firstOrNull {
                it.macro == urlStrSrc
            } ?: return urlStrSrc to String()
            return macroEnum.url to macroEnum.pageFinishedJsAssetsPath
        }

        fun setFinishJs(pageFinishedJsAssetsPath: String){
            pageFinishJs = pageFinishedJsAssetsPath
        }

        fun execPageFinishJs(
            context: Context?,
            webView: WebView,
            url: String?
        ){
            when(true){
                url.isNullOrEmpty() -> {
                    pageFinishJs = String()
                    return
                }
                (url == autoFocusGgleSearchUrl) -> {
                    pageFinishJs = AssetsFileManager.ggleSchBoxFocus
                }
                else -> {}
            }
            if(
                pageFinishJs.isEmpty()
            ) return
            val jsContents = AssetsFileManager.readFromAssets(
                context,
                pageFinishJs
            ).split("\n")
            setFinishJs(String())
            val jsScriptUrl = JavaScriptLoadUrl.makeFromContents(
                context,
                jsContents
            ) ?: return
            webView.loadUrl(jsScriptUrl)
        }
    }


    fun create(
        urlStrSrc: String,
        currentScriptPath: String,
        menuMapStrListStr: String,
        longPressMenuMapListStr: String,
        extraMapCon: String,
    ) {
        positionHashMap.clear()
        val urlToFinishJs = UrlToFinishJs()
        val urlToFinishJsPair = urlToFinishJs.makePair(urlStrSrc)
        urlToFinishJs.setFinishJs(urlToFinishJsPair.second)
        val madeUrl = urlToFinishJsPair.first
        val urlStr = if(
            madeUrl.startsWith(WebUrlVariables.slashPrefix)
            || madeUrl.startsWith(WebUrlVariables.filePrefix)
            || madeUrl.startsWith(WebUrlVariables.httpPrefix)
            || madeUrl.startsWith(WebUrlVariables.httpsPrefix)
            || madeUrl.startsWith(WevViewDialogUriPrefix.TEXT_CON.prefix)
        ) madeUrl
        else "${WebUrlVariables.queryUrl}${madeUrl}"
        CoroutineScope(Dispatchers.Main).launch{
            terminalFragment.webViewDialogInstance?.dismiss()
            terminalFragment.webViewDialogInstance = null
            terminalFragment.webViewDialogInstance = Dialog(
                context as Context
            )
            terminalFragment.webViewDialogInstance?.setContentView(
                R.layout.dialog_webview_layout
            )
            val webView = terminalFragment.webViewDialogInstance?.findViewById<WebView>(
                R.id.webview_dialog_webview
            ) ?: return@launch
            webViewSetting(webView)
            ExecDownLoadManager.set(
                terminalFragment,
                webView
            )
            loadUrlHandler(
                terminalFragment,
                webView,
                urlStr,
            )
            val progressBar = terminalFragment.webViewDialogInstance?.findViewById<ProgressBar>(
                R.id.dialog_webview_progressBar
            )
            terminalFragment.webViewDialogInstance?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            terminalFragment.webViewDialogInstance?.show()

            terminalFragment.webViewDialogInstance?.setOnCancelListener {
                stopWebView(webView)
            }
            val menuMapStrList = QuoteTool.splitBySurroundedIgnore(
                menuMapStrListStr,
                '|'
            )
            val btnWeight = culcBtnWeight(menuMapStrList)
            val firstBottomLinearLayout = terminalFragment.webViewDialogInstance?.findViewById<LinearLayout>(
                R.id.first_bottom_linearlayout
            ) ?: return@launch
            val webViewDialogExtraMapManager = WebViewDialogExtraMapManager(
                menuMapStrList,
                extraMapCon,
                firstBottomLinearLayout,
            )
            menuMapStrList.forEach {
                val btnMenuMap = makeBtnOptionMap(
                    currentScriptPath,
                    it
                )
                val bottomButton = webViewBottomBtnSetter(
                    webView,
                    btnMenuMap,
                    btnWeight,
                    currentScriptPath,
                    webViewDialogExtraMapManager,
                )
                firstBottomLinearLayout.addView(bottomButton)
            }
            webViewClientSetter(
                webView,
                urlToFinishJs
            )
            WebChromeClientSetter.set(
                terminalFragment,
                webView,
                progressBar as ProgressBar
            )
            webViewLongClickListener(
                webView,
                currentScriptPath,
                longPressMenuMapListStr
            )
        }
    }

    fun dismiss(){
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                delay(200)
            }
            withContext(Dispatchers.Main) {
                terminalFragment.webViewDialogInstance?.dismiss()
                terminalFragment.webViewDialogInstance = null
            }
        }
    }

    private fun stopWebView(webView: WebView){
        webView.onPause()
        webView.isVisible = false
        webView.destroy()
        positionHashMap.clear()
        terminalFragment.webViewDialogInstance?.dismiss()
        terminalFragment.webViewDialogInstance = null
    }

    private fun webViewSetting(
        webView: WebView
    ){
        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.allowContentAccess = true
        settings.allowFileAccess = true
        settings.builtInZoomControls = true
        settings.displayZoomControls = false
        settings.textZoom = (terminalFragment.fontZoomPercent * 95 ) / 100
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        JsInterfaceAdder.add(
            terminalFragment,
            webView
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsWebViewDialogManager(terminalFragment),
        )
    }

    private fun webViewBottomBtnSetter(
        webView: WebView,
        targetMenuMap: Map<String, String>?,
        culcBtnWeight: Float,
        currentScriptPath: String,
        webViewDialogExtraMapManager: WebViewDialogExtraMapManager,
    ): MaterialButton {
        val targetBtn = makeBottomButton(
            targetMenuMap,
            culcBtnWeight,
            webViewDialogExtraMapManager
        )
        val clickMenuList = makeMenu(
            targetMenuMap,
            WebViewMenuMapType.clickMenuFilePath.name,
        )
        val longPressMenuList = makeMenu(
            targetMenuMap,
            WebViewMenuMapType.longPressMenuFilePath.name,
        )
        targetBtn.setOnClickListener{
            val btnContext = it.context
            btnActionHandler(
                btnContext,
                targetBtn,
                targetMenuMap,
                clickMenuList,
                webView,
                DismissType.click.name,
                clickMenuGroupId,
                currentScriptPath
            )
            webViewDialogExtraMapManager.trigger(
                context,
                false,
                targetMenuMap?.get(WebViewMenuMapType.tag.name),
            )
        }
        targetBtn.setOnLongClickListener{
            val btnContext = it.context
            btnActionHandler(
                btnContext,
                targetBtn,
                targetMenuMap,
                longPressMenuList,
                webView,
                DismissType.longpress.name,
                longpressMenuGroupId,
                currentScriptPath
            )
            webViewDialogExtraMapManager.trigger(
                context,
                true,
                targetMenuMap?.get(WebViewMenuMapType.tag.name),
            )
            true
        }
        return targetBtn
    }

    private fun btnActionHandler(
        contextSrc: Context?,
        targetBtn: MaterialButton,
        btnOptionMap: Map<String, String>?,
        menuList: List<String>,
        webView: WebView,
        dismissType: String,
        menuGroupId: Int,
        currentScriptPath: String,
    ){
        launchMenu(
            contextSrc,
            targetBtn,
            menuList,
            webView,
            menuGroupId,
            currentScriptPath,
        )
        dismissHandler(
            webView,
            btnOptionMap,
            dismissType,
        )
    }

    private fun highLightSearch(
        context: Context?,
        webView: WebView,
    ){
        val jsContents = AssetsFileManager.readFromAssets(
            context,
            AssetsFileManager.assetsHighlightSchForDialogWebViewPath
        ).split("\n")
        val jsScriptUrl = JavaScriptLoadUrl.makeFromContents(
            context,
            jsContents
        )
            ?: return
        webView.loadUrl(jsScriptUrl)
    }

    private fun launchUrlAtLocal(
        webView: WebView,
    ){
        val currentUrl = webView.url
            ?: return
        ScrollPosition.save(
            terminalFragment,
            currentUrl,
            webView.scrollY,
            100f,
            0f,
        )
        stopWebView(webView)
        terminalFragment.binding.terminalWebView.loadUrl(currentUrl)
    }

    private fun launchMenu(
        contextSrc: Context?,
        webViewSearchBtn: MaterialButton,
        menuList: List<String>,
        webView: WebView,
        menuGroupId: Int,
        currentScriptPath: String,
    ){
        if (
            menuList.isEmpty()
        ) return
        if(menuList.size == 1){
            jsOrMacroHandler(
                context,
                currentScriptPath,
                getJsPathFromMenuSrc(menuList.first()),
                webView,
            )
            return
        }
        val context = contextSrc
            ?: return
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
            popupMenu,
            menuList,
            webView,
            currentScriptPath,
        )
        popupMenu.show()
    }

    private fun popupMenuItemSelected(
        popup: PopupMenu,
        menuList: List<String>,
        webView: WebView,
        currentScriptPath: String,
    ){
        popup.setOnMenuItemClickListener {
                menuItem ->
            val selectedMenuStr = menuItem.title.toString()
            val selectedJsPath = getJsPathFromSelectedMenuStr(
                selectedMenuStr,
                menuList,
            )
            jsOrMacroHandler(
                context,
                currentScriptPath,
                selectedJsPath,
                webView,
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
        webView: WebView,
        urlToFinishJs: UrlToFinishJs?
    ){
        var previousUrl: String? = null
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                if (
                    request?.url?.scheme.equals("intent")
                    || request?.url?.scheme.equals("android-app")
                ) {
                    val intent = Intent.parseUri(request?.url.toString(), Intent.URI_INTENT_SCHEME)
                    val packageManager = activity?.packageManager
                    if (
                        packageManager != null && intent?.resolveActivity(packageManager
                        ) != null)
                    {
                        activity?.startActivity(intent)
                        return true
                    }
                }
                positionHashMap.put(
                    "${webView.url}",
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

            override fun onPageFinished(
                webview: WebView?,
                url: String?
            ) {
                positionHashMap.get(url)?.let {
                    CoroutineScope(Dispatchers.Main).launch {
                        withContext(Dispatchers.IO){
                            delay(300)
                        }
                        webView.scrollY = it
                    }
                }
                WrapWebHistoryUpdater.updateForPocketWebview(
                    terminalFragment,
                    webView,
                    url,
                    previousUrl
                )
                if(
                    previousUrl?.length != url?.length
                ) {
                    previousUrl = url
                }
                urlToFinishJs?.execPageFinishJs(
                    context,
                    webView,
                    url,
                )
                super.onPageFinished(webview, url)
            }
        }
    }


    private fun makeBtnOptionMap(
        currentScriptPath: String,
        centerMenuMapStr: String
    ): Map<String, String>? {
        val currentScriptPathObj = File(currentScriptPath)
        if(
            !currentScriptPathObj.isFile
            && currentScriptPath.isNotEmpty()
        ) return null
        val currentAppDirPath =
            currentScriptPathObj.parent
                ?: String()
        val fannelName =
            currentScriptPathObj.name
                ?: String()

        return ScriptPreWordReplacer.replace(
            centerMenuMapStr,
            currentAppDirPath,
            fannelName
        ).let {
            CmdClickMap.createMap(
                it,
                '?'
            )
        }.toMap()
    }

    private fun webViewLongClickListener(
        webView: WebView,
        currentScriptPath: String,
        longPressMenuMapListStr: String,
    ){
        webView.setOnLongClickListener { view ->
            val lContext = view.context
            val hitTestResult = webView.hitTestResult
            val title = webView.title
            val currentPageUrl = webView.url
            val longPressMenuMap = makeBtnOptionMap(
                currentScriptPath,
                longPressMenuMapListStr
            )
            val httpsStartStr = WebUrlVariables.httpsPrefix
            val httpStartStr = WebUrlVariables.httpPrefix
            when (hitTestResult.type) {
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
                            terminalFragment,
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
                    webView.requestFocusNodeHref(message)

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
                            terminalFragment,
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
                            terminalFragment,
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
        webView: WebView,
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
                    stopWebView(webView)
                }
            }
        }
    }

    private fun makeBottomButton(
        menuBtnMap: Map<String, String>?,
        buttonWeight: Float,
        webViewDialogExtraMapManager: WebViewDialogExtraMapManager,
    ): MaterialButton {
        val btnContext = context as Context
        val button = MaterialButton(btnContext)
        val label = menuBtnMap
            ?.get(
                WebViewMenuMapType.label.name
            ) ?: "exec"
        button.text = label
        button.textSize = 20f
        button.stateListAnimator = null
        button.setTextColor(context.getColor(CmdClickColor.BLUE.id))
        button.backgroundTintList = context.getColorStateList(CmdClickColor.WHITE.id)
        button.background = AppCompatResources.getDrawable(
            context,
            R.drawable.edit_button_layout_no_stroke
        )
        button.insetTop = 0
        button.insetBottom = 0
        val tag = menuBtnMap
            ?.get(
                WebViewMenuMapType.tag.name
            ) ?: "exec"
        button.tag = tag
        webViewDialogExtraMapManager.setDefaultFocus(
            context,
            button,
            tag,
        )
        val linearLayoutForImageButtonParam = LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        linearLayoutForImageButtonParam.weight = buttonWeight
        linearLayoutForImageButtonParam.gravity = Gravity.CENTER
        button.layoutParams = linearLayoutForImageButtonParam
        return button

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
        context: Context?,
        currentScriptPath: String,
        jsPath: String,
        webView: WebView,
    ){
        val macro = JsMacroType.values().firstOrNull {
            it.str == jsPath
        }
        if(macro == null){
            execLoadJs(
                currentScriptPath,
                jsPath,
                webView,
            )
            return
        }
        when(macro){
            JsMacroType.GO_BACK_JS
            -> execGoBack(webView)
            JsMacroType.HIGHLIGHT_SCH_JS -> highLightSearch(
                context,
                webView
            )
            JsMacroType.LAUNCH_LOCAL_JS
            -> launchUrlAtLocal(
                webView,
            )
            JsMacroType.HIGHLIGHT_COPY_JS
            -> assetsCopy(
                webView
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
//            val disableScrollQueryParameter = listOf(
//                TxtHtmlDescriber.TxtHtmlQueryKey.DISABLE_SCROLL.key,
//                TxtHtmlDescriber.DisableScroll.disableScrollMemoryOn
//            ).joinToString("=")
            val launchUrl = TxtHtmlDescriber.makeTxtHtmlUrl(
                debugPath,
//                disableScrollQueryParameter,
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
        currentScriptPath: String,
        jsPath: String,
        webView: WebView,
    ){
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
            currentAppDirPath,
            fannelName
        )
        JavascriptExecuter.jsOrActionHandler(
            terminalFragment,
            execJsPath,
            ReadText(execJsPath).textToList(),
            webView = webView
        )
    }

    private fun execGoBack(
        webView: WebView
    ){
        if(
            !webView.canGoBack()
        ) return
        webView.goBack()
    }

    private fun assetsCopy(
        webView: WebView,
    ){
        val jsContents = AssetsFileManager.readFromAssets(
            context,
            AssetsFileManager.assetsHighlightCopy
        ).split("\n")
        val jsScriptUrl = JavaScriptLoadUrl.makeFromContents(
            context,
            jsContents
        )
            ?: return
        webView.loadUrl(jsScriptUrl)
    }
}

enum class WebViewMenuMapType {
    clickMenuFilePath,
    longPressMenuFilePath,
    dismissType,
    dismissDelayMiliTime,
    label,
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

enum class JsMacroType(val str: String,) {
    HIGHLIGHT_SCH_JS("HIGHLIGHT_SCH.js"),
    GO_BACK_JS("GO_BACK.js"),
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
    private val firstBottomLinearLayout: LinearLayout?
) {
    private var extraMap = emptyMap<String, String>()
    private var focusMap = emptyMap<String, String>()

    companion object {
        private const val keySeparator = '|'
        private var bottomBtnTagList = emptyList<String>()

        private enum class MainKeys(
            val key: String
        ) {
            FOCUS("focus")
        }

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
        button: MaterialButton,
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
            ?.findViewWithTag<MaterialButton>(curTag)
        setBackground(
            context,
            button,
            FocusManager.focusColorId
        )
        bottomBtnTagList.forEach {
            if(it == curTag) return@forEach
            val curButton = firstBottomLinearLayout
                ?.findViewWithTag<MaterialButton>(it)
            setBackground(
                context,
                curButton,
                CmdClickColor.WHITE.id
            )
        }
    }

    private fun setBackground(
        context: Context?,
        button: MaterialButton?,
        colorId: Int,
    ){
        button?.backgroundTintList =
            context?.getColorStateList(colorId)
    }
}