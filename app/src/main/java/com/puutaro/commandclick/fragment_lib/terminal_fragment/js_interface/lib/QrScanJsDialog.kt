package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.icon.CmdClickIcons
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.CcScript
import com.puutaro.commandclick.util.CmdClickMap
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.ScriptPreWordReplacer
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class QrScanJsDialog(
    terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
    private val longpressMenuGroupId = 210000
    private val clickMenuGroupId = 220000
    private val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
    private var qrScanDialogObj: Dialog? = null
    private var webView = terminalFragment.binding.terminalWebView
    private val qrDecodeTextReplaceMark = "QR_DECODE_TEXT"

    fun create(
        title: String,
        currentScriptPath: String,
        callBackJsPath: String,
        menuMapStrListStr: String,
    ) {
        terminalViewModel.onDialog = true
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.Main){
                execCreate(
                    title,
                    currentScriptPath,
                    callBackJsPath,
                    menuMapStrListStr,
                )
            }
        }
    }

    private fun execCreate(
        title: String,
        currentScriptPath: String,
        callBackJsPath: String,
        menuMapStrListStr: String,
    ){
        if(
            context == null
        ) return

        qrScanDialogObj = Dialog(
            context,
            R.style.BottomSheetDialogTheme
        )
        qrScanDialogObj?.setContentView(
            R.layout.qr_scan_dialog
        )
        val qrScanTitle =
            qrScanDialogObj?.findViewById<AppCompatTextView>(
                R.id.qr_scan_dialog_title
            )
        if(
            title.isNotEmpty()
        ) qrScanTitle?.text = title
        else qrScanTitle?.isVisible = false
        val qrScanView =
            qrScanDialogObj?.findViewById<CodeScannerView>(
                R.id.qr_scan_view
            ) ?: return
        val codeScanner = CodeScanner(context, qrScanView)

        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            CoroutineScope(Dispatchers.Main).launch {
                val decodeText = it.text
                Toast.makeText(context, "Scan result: ${decodeText}", Toast.LENGTH_LONG).show()
                codeScanner.releaseResources()
                qrScanDialogObj?.dismiss()
                terminalViewModel.onDialog = false
                loadJsForQrMenu(
                    currentScriptPath,
                    callBackJsPath,
                    webView,
                    "${qrDecodeTextReplaceMark}=${decodeText}"
                )
            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(context, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG).show()
                dismissProcess(codeScanner)
            }
        }

        qrScanView.setOnClickListener {
            codeScanner.startPreview()
        }

        val menuMapStrList = menuMapStrListStr.split("|")
        val btnWeight = culcBtnWeight(menuMapStrList)
        val qrScanBottomLinearLayout = qrScanDialogObj?.findViewById<LinearLayout>(
            R.id.qr_scan_bottom_linearlayout
        ) ?: return
        menuMapStrList.forEach {
            val btnMenuMap = makeBtnOptionMap(
                currentScriptPath,
                it
            )
            val imageButton = qrBtnSetter(
                webView,
                btnMenuMap,
                btnWeight,
                currentScriptPath
            )
            qrScanBottomLinearLayout.addView(imageButton)
        }
        qrScanDialogObj?.setOnCancelListener {
            dismissProcess(codeScanner)
        }

        codeScanner.startPreview()

        qrScanDialogObj?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        qrScanDialogObj?.show()

    }

    private fun dismissProcess(
        codeScanner: CodeScanner
    ){
        codeScanner.releaseResources()
        qrScanDialogObj?.dismiss()
        terminalViewModel.onDialog = false
    }

    private fun qrBtnSetter(
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
            QrMenuMapType.clickMenuFilePath.name,
        )
        val longPressMenuList = makeMenu(
            targetMenuMap,
            QrMenuMapType.longPressMenuFilePath.name,
        )
        targetBtn.setOnClickListener{
            val btnContext = it.context
            btnActionHandler(
                btnContext,
                targetBtn,
                targetMenuMap,
                clickMenuList,
                webView,
                DismissTypeForQr.click.name,
                clickMenuGroupId,
                currentScriptPath,
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
                DismissTypeForQr.longpress.name,
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
            loadJsForQrMenu(
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
            menuList,
            webView,
            currentScriptPath
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
            loadJsForQrMenu(
                currentScriptPath,
                selectedJsPath,
                webView,
            )
            true
        }
    }

    private fun loadJsForQrMenu(
        currentScriptPath: String,
        jsPath: String,
        webView: WebView,
        replaceMapStr: String = String(),
    ){
        val setReplaceVariableMap = SetReplaceVariabler.makeSetReplaceVariableMapFromSubFannel(
            currentScriptPath,
        )
        val fannelPath = CcPathTool.getMainFannelFilePath(currentScriptPath)
        val fannelPathObj = File(fannelPath)
        if(!fannelPathObj.isFile) return
        val currentAppDirPath = fannelPathObj.parent
            ?: return
        val fannelName = fannelPathObj.name
        val fannelDirName = CcPathTool.makeFannelDirName(
            fannelName
        )
        val execJsPath = SetReplaceVariabler.execReplaceByReplaceVariables(
            ScriptPreWordReplacer.replace(
                jsPath,
                currentAppDirPath,
                fannelDirName,
                fannelName
            ),
            setReplaceVariableMap,
            currentAppDirPath,
            fannelDirName,
            fannelName
        )

        val jsScriptUrl = JavaScriptLoadUrl.make(
            context,
            execJsPath,
            makeJsList(
                execJsPath,
                replaceMapStr,
            ),
            setReplaceVariableMapSrc = setReplaceVariableMap
        ) ?: return
        webView.loadUrl(jsScriptUrl)
    }

    private fun makeJsList(
        execJsPath: String,
        replaceMapStr: String?
    ): List<String> {
        val execJsPathObj = File(execJsPath)
        val execJsDirPath = execJsPathObj.parent
            ?: return emptyList()
        val execJsName = execJsPathObj.name
        if(
            replaceMapStr.isNullOrEmpty()
        ) return ReadText(
            execJsDirPath,
            execJsName
        ).textToList()
        var replaceJsCon = ReadText(
            execJsDirPath,
            execJsName
        ).readText()
        val replacedStr = CmdClickMap.createMap(
            replaceMapStr,
            "\n"
        )
        replacedStr.forEach {
            replaceJsCon = replaceJsCon.replace(
                it.first,
                it.second
            )
        }
        return replaceJsCon.split("\n")
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
        val fannelDirName = CcPathTool.makeFannelDirName(
            fannelName
        )

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

    private fun dismissHandler(
        btnOptionMap: Map<String, String>?,
        triggerType: String,
    ){
        val dismissType = getDismissType(
            btnOptionMap
        )
        if(
            dismissType == triggerType
            || dismissType == DismissTypeForQr.both.name
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.IO) {
                    val delayMiliTime = getDismissDelayMiliTime(
                        btnOptionMap
                    )
                    delay(delayMiliTime)
                }
                withContext(Dispatchers.Main) {
                    qrScanDialogObj?.dismiss()
                }
            }
        }
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

    private fun setIcon(
        imageButton: ImageButton?,
        btnOptionMap:  Map<String, String>?,
    ){
        val iconName = btnOptionMap
            ?.get(
                QrMenuMapType.iconName.name
            ) ?: String()
        val iconId = CmdClickIcons.values().filter {
            val curIconName = it.str
            curIconName == iconName
        }.firstOrNull()?.id ?: return
        imageButton?.setImageResource(iconId)
    }


    private fun culcBtnWeight(
        btnMenuMapStrList: List<String>
    ): Float {
        return 1.0f / btnMenuMapStrList.size
    }

    private fun getDismissType(
        btnOptionMap:  Map<String, String>?,
    ): String {
        return btnOptionMap
            ?.get(
                QrMenuMapType.dismissType.name
            ) ?: String()
    }
}

enum class QrMenuMapType {
    clickMenuFilePath,
    longPressMenuFilePath,
    dismissType,
    dismissDelayMiliTime,
    iconName,
}

private enum class DismissTypeForQr {
    longpress,
    click,
    both,
}
