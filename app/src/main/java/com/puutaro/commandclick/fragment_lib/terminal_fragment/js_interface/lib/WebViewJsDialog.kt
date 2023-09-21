package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib


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
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.common.variable.WebUrlVariables
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.ExecDownLoadManager
import com.puutaro.commandclick.fragment_lib.terminal_fragment.WebChromeClientSetter
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsFileSystem
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsToast
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsUrl
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsUtil
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog_js_interface.JsWebViewDialogManager
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.LongPressForImage
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.LongPressForSrcAnchor
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.LongPressForSrcImageAnchor
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.ScrollPosition
import com.puutaro.commandclick.fragment_lib.terminal_fragment.variables.DialogJsInterfaceVariant
import com.puutaro.commandclick.fragment_lib.terminal_fragment.variables.JsInterfaceVariant
import com.puutaro.commandclick.util.AssetsFileManager
import com.puutaro.commandclick.util.CcScript
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.ScriptPreWordReplacer
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
    private val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
    private val currentAppDirPath = terminalFragment.currentAppDirPath
    private val longpressMenuGroupId = 110000
    private val clickMenuGroupId = 120000

    fun create(
        urlStrSrc: String,
        currentFannelPath: String,
        menuMapStrListStr: String,
        longPressMenuMapListStr: String,
    ) {
        val urlStr = if(
            urlStrSrc.startsWith(WebUrlVariables.slashPrefix)
            || urlStrSrc.startsWith(WebUrlVariables.filePrefix)
            || urlStrSrc.startsWith(WebUrlVariables.httpPrefix)
            || urlStrSrc.startsWith(WebUrlVariables.httpsPrefix)
            || urlStrSrc.startsWith(UriPrefix.TEXT_CON.str)
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
            val menuMapStrList = menuMapStrListStr.split("|")
            val btnWeight = culcBtnWeight(menuMapStrList)
            val firstBottomLinearLayout = terminalFragment.dialogInstance?.findViewById<LinearLayout>(
                R.id.first_bottom_linearlayout
            ) ?: return@launch
            menuMapStrList.forEach {
                val btnMenuMap = makeBtnOptionMap(
                    currentFannelPath,
                    it
                )
                val imageButton = webViewBtnSetter(
                    webView,
                    btnMenuMap,
                    btnWeight
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
                currentFannelPath,
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
        webView.addJavascriptInterface(
            JsToast(terminalFragment),
            JsInterfaceVariant.jsToast.name
        )
        webView.addJavascriptInterface(
            JsFileSystem(terminalFragment),
            JsInterfaceVariant.jsFileSystem.name
        )
        webView.addJavascriptInterface(
            JsUrl(terminalFragment),
            JsInterfaceVariant.jsUrl.name
        )
        webView.addJavascriptInterface(
            JsWebViewDialogManager(terminalFragment),
            DialogJsInterfaceVariant.jsWebViewDialogManager.name
        )
        webView.addJavascriptInterface(
            JsUtil(terminalFragment),
            JsInterfaceVariant.jsUtil.name
        )
    }

    private fun webViewBtnSetter(
        webView: WebView,
        targetMenuMap: Map<String, String>?,
        culcBtnWeight: Float,
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
    ){
        launchMenu(
            contextSrc,
            targetBtn,
            menuList,
            webView,
            menuGroupId
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
    ){
        if (
            menuList.isEmpty()
        ) return
        if(menuList.size == 1){
            jsOrMacroHandler(
                context,
                menuList.first(),
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
            val menuName = menuList[it]
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
            webView,
        )
        popupMenu.show()
    }

    private fun popupMenuItemSelected(
        popup: PopupMenu,
        webView: WebView,
    ){
        popup.setOnMenuItemClickListener {
                menuItem ->
            jsOrMacroHandler(
                context,
                menuItem.title.toString(),
                webView,
            )
            true
        }
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
                val blocklist = terminalViewModel.blocklist
                if (blocklist.contains(":::::" + request?.url?.host)) {
                    return WebResourceResponse("text/plain", "utf-8", empty3)
                }
                return super.shouldInterceptRequest(view, request)
            }
        }
    }

    private fun makeBtnOptionMap(
        currentFannelPath: String,
        centerMenuMapStr: String
    ): Map<String, String>? {
        val currentFannelPathObj = File(currentFannelPath)
        if(
            !currentFannelPathObj.isFile
            && currentFannelPath.isNotEmpty()
        ) return null
        val currentAppDirPath =
            currentFannelPathObj.parent
                ?: String()
        val fannelName =
            currentFannelPathObj.name
                ?: String()
        val fannelDirName = fannelName
            .removeSuffix(UsePath.JS_FILE_SUFFIX)
            .removeSuffix(UsePath.SHELL_FILE_SUFFIX) +
                "Dir"

        return centerMenuMapStr.let {
            ScriptPreWordReplacer.replace(
                it,
                currentAppDirPath,
                fannelDirName,
                fannelName
            )
        }.split("!").map{
            CcScript.makeKeyValuePairFromSeparatedString(
                it,
                "="
            )
        }.toMap()
    }

    private fun webViewLongClickListener(
        webView: WebView,
        currentFannelPath: String,
        longPressMenuMapListStr: String,
    ){
        webView.setOnLongClickListener() { view ->
            val lContext = view.context
            val hitTestResult = webView.hitTestResult
            val title = webView.title
            val currentPageUrl = webView.url
            val longPressMenuMap = makeBtnOptionMap(
                currentFannelPath,
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

    private fun makeLongPressMenu(
        currentFannelPath: String,
        imageMapStr: String?
    ): String {
        if(
            imageMapStr.isNullOrEmpty()
        ) return String()
        val imageMenuMap = makeBtnOptionMap(
            currentFannelPath,
            imageMapStr
        )
        return imageMenuMap
            ?.get(
                WebViewMenuMapType.clickMenuFilePath.name
            ) ?: String()
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
        when(iconName){
            IconType.COPY.str
            -> imageButton?.setImageResource(IconType.COPY.id)
            IconType.SEARCH.str
            -> imageButton?.setImageResource(IconType.SEARCH.id)
            IconType.WHEEL.str
            -> imageButton?.setImageResource(IconType.WHEEL.id)
            IconType.BACK.str
            -> imageButton?.setImageResource(IconType.BACK.id)
            IconType.HISTORY.str
            -> imageButton?.setImageResource(IconType.HISTORY.id)
            IconType.OVERFLOW.str
            -> imageButton?.setImageResource(IconType.OVERFLOW.id)
            IconType.CANCEL.str
            -> imageButton?.setImageResource(IconType.CANCEL.id)
        }
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
        val menuFilePathObj = File(menuFilePath)
        val menuFileDirPath = menuFilePathObj.parent
            ?: String()
        val menuFileName = menuFilePathObj.name
        return ReadText(
            menuFileDirPath,
            menuFileName
        ).textToList()
    }

    private fun culcBtnWeight(
        btnMenuMapStrList: List<String>
    ): Float {
        return 1.0f / btnMenuMapStrList.size
    }


    private fun jsOrMacroHandler(
        context: Context?,
        scriptName: String,
        webView: WebView,
    ){
        when(scriptName){
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
            else -> {
                val jsScriptUrl = JavaScriptLoadUrl.make(
                    context,
                    "${currentAppDirPath}/${scriptName}",
                ) ?: return
                webView.loadUrl(jsScriptUrl)
            }
        }
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
        val textConUriPrefix = UriPrefix.TEXT_CON.str
        val trimUrlCon = urlCon.trim()
        when(true){
            trimUrlCon.startsWith(textConUriPrefix) -> {
                val removePrefixCon = trimUrlCon.removePrefix(textConUriPrefix)
                webView.loadDataWithBaseURL(
                    "",
                    removePrefixCon,
                    "text/html",
                    "utf-8",
                    null
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
    iconName
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

private enum class IconType(
    val str: String,
    val id: Int,
) {
    COPY("copy", androidx.appcompat.R.drawable.abc_ic_menu_copy_mtrl_am_alpha),
    SEARCH("search", R.drawable.icons8_search),
    BACK("back", com.afollestad.materialdialogs.R.drawable.md_nav_back),
    WHEEL("wheel", com.skydoves.colorpickerview.R.drawable.wheel),
    HISTORY("history", R.drawable.icons8_history),
    OVERFLOW("oeverflow", androidx.appcompat.R.drawable.abc_ic_menu_overflow_material),
    CANCEL("cancel", R.drawable.icons8_cancel)
}

enum class JsMacroType(val str: String,) {
    HIGHLIGHT_SCH_JS("HIGHLIGHT_SCH.js"),
    GO_BACK_JS("GO_BACK.js"),
    LAUNCH_LOCAL_JS("LAUNCH_LOCAL.js"),
    HIGHLIGHT_COPY_JS("HIGHLIGHT_COPY.js")
}

private enum class UriPrefix(
    val str: String
) {
    TEXT_CON("textCon://")
}
