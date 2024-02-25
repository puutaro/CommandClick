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
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.ProgressBar
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity_lib.manager.AdBlocker
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.variables.WebUrlVariables
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.ExecDownLoadManager
import com.puutaro.commandclick.fragment_lib.terminal_fragment.WebChromeClientSetter
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog_js_interface.JsWebViewDialogManager
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.JsInterfaceAdder
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.LongPressForImage
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.LongPressForSrcAnchor
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.LongPressForSrcImageAnchor
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.ScrollPosition
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.libs.ExecJsInterfaceAdder
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.intent.lib.JavascriptExecuter
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionTool
import com.puutaro.commandclick.proccess.tool_bar_button.JsActionHandler
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.ScriptPreWordReplacer
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import com.puutaro.commandclick.util.state.TargetFragmentInstance
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
    private val readSharePreferenceMap = terminalFragment.readSharePreferenceMap
    private val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreferenceMap,
        SharePrefferenceSetting.current_app_dir
    )
    private val currentFannelName = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreferenceMap,
        SharePrefferenceSetting.current_fannel_name
    )
    private val currentState = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreferenceMap,
        SharePrefferenceSetting.current_fannel_state
    )
    private val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
    private val longpressMenuGroupId = 110000
    private val clickMenuGroupId = 120000
    private val positionHashMap = hashMapOf<String, Int>()

    fun create(
        urlStrSrc: String,
        currentScriptPath: String,
        menuMapStrListStr: String,
        longPressMenuMapListStr: String,
    ) {
        positionHashMap.clear()
        val urlStr = if(
            urlStrSrc.startsWith(WebUrlVariables.slashPrefix)
            || urlStrSrc.startsWith(WebUrlVariables.filePrefix)
            || urlStrSrc.startsWith(WebUrlVariables.httpPrefix)
            || urlStrSrc.startsWith(WebUrlVariables.httpsPrefix)
            || urlStrSrc.startsWith(UriPrefix.TEXT_CON.prefix)
        ) urlStrSrc
        else "${WebUrlVariables.queryUrl}${urlStrSrc}"
        CoroutineScope(Dispatchers.Main).launch{
            terminalFragment.dialogInstance?.dismiss()
            terminalFragment.dialogInstance = Dialog(
                context as Context
            )
            terminalFragment.dialogInstance?.setContentView(
                R.layout.dialog_webview_layout
            )
            val webView = terminalFragment.dialogInstance?.findViewById<WebView>(
                R.id.webview_dialog_webview
            ) ?: return@launch
            webViewSetting(webView)
            ExecDownLoadManager.set(
                terminalFragment,
                webView
            )
            loadUrlHandler(
                webView,
                urlStr,
            )
            val progressBar = terminalFragment.dialogInstance?.findViewById<ProgressBar>(
                R.id.dialog_webview_progressBar
            )
            terminalFragment.dialogInstance?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            terminalFragment.dialogInstance?.show()

            terminalFragment.dialogInstance?.setOnCancelListener {
                terminalFragment.dialogInstance?.dismiss()
            }
            val menuMapStrList = QuoteTool.splitBySurroundedIgnore(
                menuMapStrListStr,
                '|'
            )
//                menuMapStrListStr.split("|")
            val btnWeight = culcBtnWeight(menuMapStrList)
            val firstBottomLinearLayout = terminalFragment.dialogInstance?.findViewById<LinearLayout>(
                R.id.first_bottom_linearlayout
            ) ?: return@launch
            menuMapStrList.forEach {
                val btnMenuMap = makeBtnOptionMap(
                    currentScriptPath,
                    it
                )
                val imageButton = webViewBtnSetter(
                    webView,
                    btnMenuMap,
                    btnWeight,
                    currentScriptPath
                )
                firstBottomLinearLayout.addView(imageButton)
            }
            webViewClientSetter(webView)
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

    private fun webViewBtnSetter(
        webView: WebView,
        targetMenuMap: Map<String, String>?,
        culcBtnWeight: Float,
        currentScriptPath: String,
    ): ImageButton {
        val targetBtn = makeImageButton(
            targetMenuMap,
            culcBtnWeight
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
            true
        }
        return targetBtn
    }

    private fun btnActionHandler(
        contextSrc: Context?,
        targetBtn: ImageButton,
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
        val jsScriptUrl = JavaScriptLoadUrl.makeFromContents(jsContents)
            ?: return
        webView.loadUrl(jsScriptUrl)
    }

    private fun launchUrlAtLocal(
        webView: WebView,
    ){
        val currentUrl = webView.url
            ?: return
        terminalFragment.dialogInstance?.dismiss()
        ScrollPosition.save(
            terminalFragment,
            currentUrl,
            webView.scrollY,
            100f,
            0f,
        )
        terminalFragment.binding.terminalWebView.loadUrl(currentUrl)
    }

    private fun launchMenu(
        contextSrc: Context?,
        webViewSearchBtn: ImageButton,
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
        val selectedMenuLine =  menuList.filter {
            it.split("\t").firstOrNull() == selectedMenuStr
        }.firstOrNull() ?: return String()
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
    ){
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
                '!'
            )
        }.toMap()
    }

    private fun webViewLongClickListener(
        webView: WebView,
        currentScriptPath: String,
        longPressMenuMapListStr: String,
    ){
        webView.setOnLongClickListener() { view ->
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
                    terminalFragment.dialogInstance?.dismiss()
                }
            }
        }
    }

    private fun setIcon(
        imageButton: ImageButton?,
        btnOptionMap:  Map<String, String>?,
    ){
        val iconName = btnOptionMap
            ?.get(
                WebViewMenuMapType.iconName.name
            ) ?: String()
        val iconId = CmdClickIcons.values().filter {
            val curIconName = it.str
            curIconName == iconName
        }.firstOrNull()?.id ?: return
        imageButton?.setImageResource(iconId)
    }

    private fun makeImageButton(
        menuBtnMap: Map<String, String>?,
        buttonWeight: Float,
    ): ImageButton {
        val imageButton = ImageButton(context)
        imageButton.imageTintList = context?.getColorStateList(R.color.web_icon_color)
        imageButton.backgroundTintList = context?.getColorStateList(R.color.white)
        val linearLayoutForImageButtonParam = LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        linearLayoutForImageButtonParam.weight = buttonWeight
        linearLayoutForImageButtonParam.gravity = Gravity.CENTER
        imageButton.layoutParams = linearLayoutForImageButtonParam
        setIcon(
            imageButton,
            menuBtnMap
        )
        return imageButton

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
        return ReadText(
            menuFilePath
        ).textToList().filter {
            val timeLine = it.trim()
            !timeLine.startsWith(commentOutMark)
                    && !timeLine.startsWith("#")
                    && timeLine.isNotEmpty()
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
        when(jsPath){
            JsMacroType.GO_BACK_JS.str
            -> execGoBack(webView)
            JsMacroType.HIGHLIGHT_SCH_JS.str -> highLightSearch(
                context,
                webView
            )
            JsMacroType.LAUNCH_LOCAL_JS.str
            -> launchUrlAtLocal(
                webView,
            )
            JsMacroType.HIGHLIGHT_COPY_JS.str
            -> assetsCopy(
                webView
            )
            else
            -> execLoadJs(
                currentScriptPath,
                jsPath,
                webView,
            )
        }
    }

    private fun execLoadJs(
        currentScriptPath: String,
        jsPath: String,
        webView: WebView,
    ){
        val setReplaceVariableMap = SetReplaceVariabler.makeSetReplaceVariableMapFromSubFannel(
            currentScriptPath,
        )
        val fannelPath = CcPathTool.getMainFannelFilePath(currentScriptPath)
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

    private fun loadUrlHandler(
        webView: WebView,
        urlCon: String,
    ){
        val textConUriPrefix = UriPrefix.TEXT_CON.prefix
        val mdConUriPrefix = UriPrefix.MD_CON.prefix
        val trimUrlCon = urlCon.trim()
        when(true){
            trimUrlCon.startsWith(textConUriPrefix) -> {
                val textUrl = UriPrefix.TEXT_CON.url
                val removePrefixCon = trimUrlCon.removePrefix(textConUriPrefix)
                webView.loadDataWithBaseURL(
                    textUrl,
                    removePrefixCon,
                    "text/html",
                    "utf-8",
                    textUrl
                )
            }
            trimUrlCon.startsWith(mdConUriPrefix) -> {
                val mdUrl = UriPrefix.MD_CON.url
                val removePrefixCon = trimUrlCon.removePrefix(mdConUriPrefix)
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

    private fun assetsCopy(
        webView: WebView,
    ){
        val jsContents = AssetsFileManager.readFromAssets(
            context,
            AssetsFileManager.assetsHighlightCopy
        ).split("\n")
        val jsScriptUrl = JavaScriptLoadUrl.makeFromContents(jsContents)
            ?: return
        webView.loadUrl(jsScriptUrl)
    }
}

enum class WebViewMenuMapType {
    clickMenuFilePath,
    longPressMenuFilePath,
    dismissType,
    dismissDelayMiliTime,
    iconName,
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
    HIGHLIGHT_COPY_JS("HIGHLIGHT_COPY.js")
}

private enum class UriPrefix(
    val prefix: String,
    val url: String,
) {
    TEXT_CON("textCon://", "${WebUrlVariables.filePrefix}///textCon.txt"),
    MD_CON("mdCon://", "${WebUrlVariables.filePrefix}///descMd.txt"),
}
