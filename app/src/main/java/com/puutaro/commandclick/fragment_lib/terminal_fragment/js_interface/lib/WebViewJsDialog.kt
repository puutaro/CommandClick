package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib


import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.ProgressBar
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
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.io.File


class WebViewJsDialog(
    private val terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
    private val activity = terminalFragment.activity
    private val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
    private val currentAppDirPath = terminalFragment.currentAppDirPath
    private val centerMenuGroupId = 110000
    private val rightMenuGroupId = 120000

    fun create(
        urlStrSrc: String,
        currentFannelPath: String,
        centerMenuMapStr: String,
        rightMenuMapStr: String,
        srcAnchorImageMapStr: String,
        srcAnchorMapStr: String,
        imageMapStr: String,
    ) {
        val urlStr = if(
            urlStrSrc.startsWith(WebUrlVariables.slashPrefix)
            || urlStrSrc.startsWith(WebUrlVariables.filePrefix)
            || urlStrSrc.startsWith(WebUrlVariables.httpPrefix)
            || urlStrSrc.startsWith(WebUrlVariables.httpsPrefix)
        ) urlStrSrc
        else "${WebUrlVariables.queryUrl}${urlStrSrc}"
        CoroutineScope(Dispatchers.Main).launch{
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
            webView.loadUrl(
                urlStr
            )
            val progressBar = terminalFragment.dialogInstance?.findViewById<ProgressBar>(
                R.id.dialog_webview_progressBar
            )
            val webViewSearchBtn = terminalFragment.dialogInstance?.findViewById<ImageButton>(
                R.id.webview_dialog_search
            )
            val webViewLaunchLocalBtn = terminalFragment.dialogInstance?.findViewById<ImageButton>(
                R.id.webview_dialog_launch_local
            )
            val webViewBackBtn = terminalFragment.dialogInstance?.findViewById<ImageButton>(
                R.id.webview_dialog_back
            )
            terminalFragment.dialogInstance?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            terminalFragment.dialogInstance?.show()

            terminalFragment.dialogInstance?.setOnCancelListener {
                terminalFragment.dialogInstance?.dismiss()
            }
            webViewSearchBtnSetClickListener(
                webView,
                webViewSearchBtn,
                currentFannelPath,
                centerMenuMapStr,
            )
            webViewLaunchLocalBtnSetClickListener(
                webView,
                webViewLaunchLocalBtn,
                currentFannelPath,
                rightMenuMapStr
            )
            webViewBackBtnSetClickListner(
                webView,
                webViewBackBtn,
            )
            webViewClientSetter(webView)
            WebChromeClientSetter.set(
                terminalFragment,
                webView,
                progressBar as ProgressBar
            )
            webViewLongClickListener(
                webView,
                currentFannelPath,
                srcAnchorImageMapStr,
                srcAnchorMapStr,
                imageMapStr,
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
    }

    private fun webViewSearchBtnSetClickListener(
        webView: WebView,
        webViewSearchBtn: ImageButton?,
        currentFannelPath: String,
        centerMenuMapStr: String
    ){
        val centerMenuMap = makeMenuMap(
            currentFannelPath,
            centerMenuMapStr
        )
        val onSwitch = !centerMenuMap
            ?.get(
                webViewMenuMapType.onSwitch.name
            ).isNullOrEmpty()
        if(onSwitch){
            webViewSearchBtn?.setImageResource(
                R.drawable.icons8_history
            )
        }
        val menuFilePath = centerMenuMap
            ?.get(
                webViewMenuMapType.menuFilePath.name
            ) ?: String()
        val menuFilePathObj = File(menuFilePath)
        val menuFileDirPath = menuFilePathObj.parent
            ?: String()
        val menuFileName = menuFilePathObj.name
        val menuList = ReadText(
            menuFileDirPath,
            menuFileName
        ).textToList()
        webViewSearchBtn?.setOnClickListener{
            val btnContext = it.context
            launchHandlerForCenterBtn(
                btnContext,
                onSwitch,
                webView,
                webViewSearchBtn,
                menuList
            )
        }
        webViewSearchBtn?.setOnLongClickListener{
            val btnContext = it.context
            launchHandlerForCenterBtn(
                btnContext,
                !onSwitch,
                webView,
                webViewSearchBtn,
                menuList
            )
            true
        }
    }

    private fun launchHandlerForCenterBtn(
        context: Context?,
        onSwitch: Boolean,
        webView: WebView,
        webViewSearchBtn: ImageButton,
        menuList: List<String>
    ){
        if(!onSwitch){
            highLightSearch(
                context,
                webView,
            )
            return
        }
        launchMenu(
            context,
            webViewSearchBtn,
            menuList,
            webView,
            centerMenuGroupId
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
    private fun webViewLaunchLocalBtnSetClickListener(
        webView: WebView,
        webViewLaunchLocalBtn: ImageButton?,
        currentFannelPath: String,
        rightMenuMapStr: String
    ){
        val rightMenuMap = makeMenuMap(
            currentFannelPath,
            rightMenuMapStr
        )
        val onSwitch = !rightMenuMap
            ?.get(
                webViewMenuMapType.onSwitch.name
            ).isNullOrEmpty()
        if(onSwitch){
            webViewLaunchLocalBtn?.setImageResource(
                androidx.appcompat.R.drawable.abc_ic_menu_overflow_material
            )
        }
        val menuFilePath = rightMenuMap
            ?.get(
                webViewMenuMapType.menuFilePath.name
            ) ?: String()
        val menuFilePathObj = File(menuFilePath)
        val menuFileDirPath = menuFilePathObj.parent
            ?: String()
        val menuFileName = menuFilePathObj.name
        val menuList = ReadText(
            menuFileDirPath,
            menuFileName
        ).textToList()
        webViewLaunchLocalBtn?.setOnClickListener {
            val btnContext = it.context
            launchHandlerForRightBtn(
                btnContext,
                onSwitch,
                webView,
                webViewLaunchLocalBtn,
                menuList
            )
        }
        webViewLaunchLocalBtn?.setOnLongClickListener {
            val btnContext = it.context
            launchHandlerForRightBtn(
                btnContext,
                !onSwitch,
                webView,
                webViewLaunchLocalBtn,
                menuList
            )
            true
        }
    }

    private fun launchHandlerForRightBtn(
        context: Context?,
        onSwitch: Boolean,
        webView: WebView,
        webViewLocalBtn: ImageButton,
        menuList: List<String>
    ){
        if(!onSwitch){
            launchUrlAtLocal(
                webView,
            )
            return
        }
        launchMenu(
            context,
            webViewLocalBtn,
            menuList,
            webView,
            rightMenuGroupId
        )
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

    private fun webViewBackBtnSetClickListner(
        webView: WebView,
        webViewBackBtn: ImageButton?,
    ){
        webViewBackBtn?.setOnClickListener{
            if(
                !webView.canGoBack()
            ) return@setOnClickListener
            webView.goBack()
        }
        webViewBackBtn?.setOnLongClickListener{
            terminalFragment.dialogInstance?.dismiss()
            true
        }
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
        if(
            menuList.size == 1
        ){
            val jsScriptUrl = JavaScriptLoadUrl.make(
                context,
                "${currentAppDirPath}/${menuList[0]}",
            ) ?: return
            webView.loadUrl(jsScriptUrl)
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
            val jsScriptUrl = JavaScriptLoadUrl.make(
                context,
                "${currentAppDirPath}/${menuItem}",
            )
                ?: return@setOnMenuItemClickListener true
            webView.loadUrl(jsScriptUrl)
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

    private fun makeMenuMap(
        currentFannelPath: String,
        centerMenuMapStr: String
    ): Map<String, String>? {
        val currentFannelPathObj = File(currentFannelPath)
        if(
            !currentFannelPathObj.isFile
        ) return null
        val currentAppDirPath =
            currentFannelPathObj.parent
                ?: return null
        val fannelName =
            currentFannelPathObj.name
                ?: return null
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
        }.split("\t").map{
            CcScript.makeKeyValuePairFromSeparatedString(
                it,
                "="
            )
        }.toMap()
    }

    private fun webViewLongClickListener(
        webView: WebView,
        currentFannelPath: String,
        srcAnchorImageMapStr: String,
        srcAnchorMapStr: String,
        imageMapStr: String,
    ){
        webView.setOnLongClickListener() { view ->
            val lContext = view.context
            val hitTestResult = webView.hitTestResult
            val title = webView.title
            val currentPageUrl = webView.url
            val httpsStartStr = WebUrlVariables.httpsPrefix
            val httpStartStr = WebUrlVariables.httpPrefix
            when (hitTestResult.type) {
                WebView.HitTestResult.IMAGE_TYPE -> {
                    if (
                        currentPageUrl?.startsWith(httpsStartStr) == true
                        || currentPageUrl?.startsWith(httpStartStr) == true
                    ) {
                        val menuFilePath = makeLongPressMenu(
                            currentFannelPath,
                            imageMapStr
                        )
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
                        val menuFilePath = makeLongPressMenu(
                            currentFannelPath,
                            srcAnchorImageMapStr
                        )
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
                        val menuFilePath = makeLongPressMenu(
                            currentFannelPath,
                            srcAnchorMapStr
                        )
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
        imageMapStr: String
    ): String {
        val imageMenuMap = makeMenuMap(
            currentFannelPath,
            imageMapStr
        )
        return imageMenuMap
            ?.get(
                webViewMenuMapType.menuFilePath.name
            ) ?: String()
    }
}

private enum class webViewMenuMapType {
    onSwitch,
    menuFilePath
}