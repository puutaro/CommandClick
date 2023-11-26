package com.puutaro.commandclick.proccess.qr

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ListView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.google.zxing.BinaryBitmap
import com.google.zxing.LuminanceSource
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.QrLaunchType
import com.puutaro.commandclick.common.variable.variables.QrSeparator
import com.puutaro.commandclick.component.adapter.subMenuAdapter
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.GmailKey
import com.puutaro.commandclick.util.Intent.CurlManager
import com.puutaro.commandclick.util.NetworkTool
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup


class Scanner(
    private val fragment: Fragment,
    private val currentAppDirPath: String,
) {
    private val fragContext = fragment.context
    private val displayTitleTextLimit = 50
    private val jsDescSeparator = QrSeparator.sepalator.str

    private var qrScanDialogObj: Dialog? = null

    fun scanFromImage(
        qrImagePath: String
    ): String {
        val bMap = BitmapFactory.decodeFile(qrImagePath)

        val intArray = IntArray(bMap.width * bMap.height)
        bMap.getPixels(intArray, 0, bMap.width, 0, 0, bMap.width, bMap.height)

        val source: LuminanceSource =
            RGBLuminanceSource(bMap.width, bMap.height, intArray)
        val bitmap = BinaryBitmap(HybridBinarizer(source))

        val reader = MultiFormatReader()
        val result = reader.decode(bitmap)
        return result.getText()
    }

    fun scanFromCamera() {
        val activity = fragment.activity ?: return
        val terminalViewModel: TerminalViewModel by fragment.activityViewModels()
        terminalViewModel.onPermDialog = true
        val cameraPermissionStr = Manifest.permission.CAMERA
        when(fragment){
            is CommandIndexFragment -> {
                val listener =
                    fragContext as? CommandIndexFragment.OnGetPermissionListenerForCmdIndex
                listener?.onGetPermissionForCmdIndex(cameraPermissionStr)
            }
            is EditFragment -> {
                val listener =
                    fragContext as? EditFragment.OnGetPermissionListenerForEdit
                listener?.onGetPermissionForEdit(cameraPermissionStr)
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                for (i in 1..100) {
                    if (!terminalViewModel.onPermDialog) break
                    delay(100)
                }
            }
            val isCameraPermission = ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
            if(!isCameraPermission) return@launch
            withContext(Dispatchers.Main) {
                launchCameraDialog()
            }
        }
    }


    private suspend fun launchCameraDialog() {
        val context = fragment.context
            ?: return
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
        qrScanTitle?.isVisible = false
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
                codeScanner.releaseResources()
                loadDecodedText(
                    fragment,
                    qrScanDialogObj,
                    codeScanner,
                    decodeText
                )
            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            dismissProcess(codeScanner)
        }

        qrScanView.setOnClickListener {
            codeScanner.startPreview()
        }

        val qrScanBottomLinearLayout = qrScanDialogObj?.findViewById<LinearLayout>(
            R.id.qr_scan_bottom_linearlayout
        ) ?: return
        val cancelButton = makeCancelButton(codeScanner)
        qrScanBottomLinearLayout.addView(cancelButton)
        val historyButton = makeHistoryButton(
            codeScanner,
            qrScanDialogObj
        )
        qrScanBottomLinearLayout.addView(historyButton)
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
    }

    private suspend fun loadDecodedText(
        fragment: Fragment,
        qrScanDialogObj: Dialog?,
        codeScanner: CodeScanner,
        decodeText: String,
    ){
        val title = makeTitle(
            decodeText
        )
        ConfirmDialogForQr(
            fragment,
            qrScanDialogObj,
            codeScanner,
            currentAppDirPath,
            title,
            decodeText,
        ).launch()
    }

    private suspend fun makeTitle(scanCon: String): String{
        return when(true){
            scanCon.startsWith(QrLaunchType.Http.prefix),
            scanCon.startsWith(QrLaunchType.Https.prefix)
            -> makeDocFromUrl(
                    scanCon
                )
            scanCon.startsWith(QrLaunchType.Javascript.prefix) -> {
                scanCon.take(
                    displayTitleTextLimit
                )
            }
            scanCon.startsWith(QrLaunchType.CpFile.prefix) -> {
                createCopyFileTitle(scanCon)
            }
            scanCon.startsWith(QrLaunchType.JsDesc.prefix)
            -> extractTitleForJsDesc(scanCon)
            scanCon.startsWith(QrLaunchType.WIFI.prefix),
            scanCon.startsWith(QrLaunchType.WIFI.prefix.uppercase())
            -> createWifiTitle(scanCon)
            scanCon.startsWith(QrLaunchType.SMS.prefix),
            scanCon.startsWith(QrLaunchType.SMS.prefix.uppercase())
            -> createSmsTitle(scanCon)
            scanCon.startsWith(QrLaunchType.MAIL.prefix),
            scanCon.startsWith(QrLaunchType.MAIL.prefix.uppercase()),
            scanCon.startsWith(QrLaunchType.MAIL2.prefix),
            scanCon.startsWith(QrLaunchType.MAIL2.prefix.uppercase()),
                ->  createGmailTitle(scanCon)
            else -> "Copy ok?: $scanCon"
        }
    }

    private fun createCopyFileTitle(
        scanCon: String
    ): String {
        val urlAndFilePath = NetworkTool.extractCopyPath(scanCon)
        val url = urlAndFilePath?.first
        val filePath = urlAndFilePath?.second
        return "Copy ok?: path: ${filePath} from: ${url}".take(
            displayTitleTextLimit
        )
    }


    private fun createWifiTitle(
        scanCon: String
    ): String {
        return NetworkTool.getWifiWpaSsidAndPinPair(scanCon).let {
            "WIFI ssid: ${it.first} pin: ${it.second}"
        }.take(displayTitleTextLimit)
    }
    private fun createSmsTitle(
        scanCon: String
    ): String {
        return NetworkTool.getSmsNumAndBody(scanCon).let {
            "SMS tel: ${it.first} body: ${it.second}"
        }.take(displayTitleTextLimit)
    }

    private fun createGmailTitle(
        scanCon: String,
    ): String {
        val gmailMap = NetworkTool.makeGmailMap(scanCon)
        val subject = gmailMap?.get(GmailKey.SUBJECT.key)
        if(
            !subject.isNullOrEmpty()
        ) return subject
        val mailAd = gmailMap?.get(
            GmailKey.MAIL_AD.key
        )
        val body = gmailMap?.get(GmailKey.BODY.key)
        return "Ad ${mailAd} Body: ${body}"
            .take(displayTitleTextLimit)
    }

    private fun extractTitleForJsDesc(scanCon: String): String {
        return scanCon
            .split(jsDescSeparator)
            .firstOrNull()?.trim()
            ?.removePrefix(QrLaunchType.JsDesc.prefix)
            ?.trim()
            ?: scanCon.take(
                displayTitleTextLimit
            )
    }

    private suspend fun makeDocFromUrl(
        targetUrl: String
    ): String {
        val htmlString =
            withContext(Dispatchers.IO) {
                CurlManager.get(
                    targetUrl,
                    "",
                    "",
                    2000
                ).let {
                    CurlManager.convertResToStrByConn(it)
                }
            }
        val doc = Jsoup.parse(htmlString)
        val titleSrc = doc.title()
        if(
            titleSrc.isEmpty()
        ) return targetUrl
        return titleSrc
    }


    private fun makeHistoryButton(
        codeScanner: CodeScanner,
        qrScanDialogObj: Dialog?
    ): ImageButton {
        val imageButton = ImageButton(fragContext)
        imageButton.imageTintList = fragContext?.getColorStateList(R.color.web_icon_color)
        imageButton.backgroundTintList = fragContext?.getColorStateList(R.color.white)
        val linearLayoutForImageButtonParam = LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        linearLayoutForImageButtonParam.weight = 0.5F
        linearLayoutForImageButtonParam.gravity = Gravity.CENTER
        imageButton.layoutParams = linearLayoutForImageButtonParam
        imageButton.setImageResource(R.drawable.icons8_history)
        imageButton.setOnClickListener {
            codeScanner.releaseResources()
            QrHistoryListDialog(
                fragment,
                codeScanner,
                qrScanDialogObj,
                currentAppDirPath
            ).launch()
        }
        return imageButton
    }

    private fun makeCancelButton(
        codeScanner: CodeScanner
    ): ImageButton {
        val fragContext = fragment.context
        val imageButton = ImageButton(fragContext)
        imageButton.imageTintList = fragContext?.getColorStateList(R.color.web_icon_color)
        imageButton.backgroundTintList = fragContext?.getColorStateList(R.color.white)
        val linearLayoutForImageButtonParam = LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        linearLayoutForImageButtonParam.weight = 0.5F
        linearLayoutForImageButtonParam.gravity = Gravity.CENTER
        imageButton.layoutParams = linearLayoutForImageButtonParam
        imageButton.setImageResource(R.drawable.icons8_cancel)
        imageButton.setOnClickListener {
            dismissProcess(codeScanner)
        }
        return imageButton
    }
}

private class ConfirmDialogForQr(
    private val fragment: Fragment,
    private val qrScanDialogObj: Dialog?,
    private val codeScanner: CodeScanner,
    private val currentAppDirPath: String,
    private val title: String,
    private val body: String,
){
    private val context = fragment.context
    private var confirmDialogObj: Dialog? = null
    private val displayUriTextLimit = 200

    fun launch(){
        if(
            context == null
        ) return
        if(
            body.isEmpty()
        ) return

        confirmDialogObj = Dialog(
            context
        )
        confirmDialogObj?.setContentView(
            R.layout.confirm_text_dialog
        )
        val confirmTitleTextView =
            confirmDialogObj?.findViewById<AppCompatTextView>(
                R.id.confirm_text_dialog_title
            )
        confirmTitleTextView?.text = title
        val confirmContentTextView =
            confirmDialogObj?.findViewById<AppCompatTextView>(
                R.id.confirm_text_dialog_text_view
            )
        confirmContentTextView?.text = body.take(displayUriTextLimit)
        val confirmCancelButton =
            confirmDialogObj?.findViewById<AppCompatImageButton>(
                R.id.confirm_text_dialog_cancel
            )
        confirmCancelButton?.setOnClickListener {
            codeScanner.startPreview()
            confirmDialogObj?.dismiss()
        }
        val confirmOkButton =
            confirmDialogObj?.findViewById<AppCompatImageButton>(
                R.id.confirm_text_dialog_ok
            )
        confirmOkButton?.setOnClickListener {
            qrScanDialogObj?.dismiss()
            confirmDialogObj?.dismiss()
            QrUri.handler(
                fragment,
                currentAppDirPath,
                body
            )
            registerQrUriToHistory(
                currentAppDirPath,
                title,
                body,
            )

        }
        confirmDialogObj?.setOnCancelListener {
            codeScanner.startPreview()
            confirmDialogObj?.dismiss()
        }
        confirmDialogObj?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        confirmDialogObj?.window?.setGravity(
            Gravity.BOTTOM
        )
        confirmDialogObj?.show()
    }
}


private class QrHistoryListDialog(
    private val fragment: Fragment,
    private val codeScanner: CodeScanner,
    private val qrScanDialogObj: Dialog?,
    private val currentAppDirPath: String,
) {
    private val context = fragment.context
    private val qrLogo = R.drawable.qr_code
    private var subMenuDialog: Dialog? = null


    fun launch(){
        if(
            context == null
        ) return
        subMenuDialog = Dialog(
            context
        )
        subMenuDialog?.setContentView(
            R.layout.submenu_dialog
        )
        setListView(
            qrScanDialogObj,
            currentAppDirPath,
        )
        setCancelListener()
        subMenuDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        subMenuDialog
            ?.window
            ?.setGravity(Gravity.BOTTOM)
        subMenuDialog?.show()

    }

    private fun setCancelListener(){
        val cancelImageButton =
            subMenuDialog?.findViewById<ImageButton>(
                R.id.submenu_dialog_cancel
            )
        cancelImageButton?.setOnClickListener {
            codeScanner.startPreview()
            subMenuDialog?.dismiss()
        }
        subMenuDialog?.setOnCancelListener {
            codeScanner.startPreview()
            subMenuDialog?.dismiss()
        }
    }

    private fun setListView(
        qrScanDialogObj: Dialog?,
        currentAppDirPath: String
    ) {
        if(
            context == null
        ) return
        val subMenuListView =
            subMenuDialog?.findViewById<ListView>(
                R.id.sub_menu_list_view
            )
        val subMenuPairList = makeQrTitleList(
        )
        val subMenuAdapter = subMenuAdapter(
            context,
            subMenuPairList.toMutableList()
        )
        subMenuListView?.adapter = subMenuAdapter
        subMenuItemClickListener(
            subMenuListView,
            qrScanDialogObj,
            currentAppDirPath,
        )
    }

    private fun subMenuItemClickListener(
        subMenuListView: ListView?,
        qrScanDialogObj: Dialog?,
        currentAppDirPath: String,
    ){
        subMenuListView?.setOnItemClickListener {
                parent, view, position, id ->
            qrScanDialogObj?.dismiss()
            codeScanner.releaseResources()
            subMenuDialog?.dismiss()
            val menuListAdapter = subMenuListView.adapter as subMenuAdapter
            val selectedQrTitle = menuListAdapter.getItem(position)
                ?: return@setOnItemClickListener
            val selectedQrTitleUriLine = makeQrHistoryList().filter {
                val titleUriList = it.split("\t")
                val title = titleUriList.firstOrNull() ?: String()
                title == selectedQrTitle
            }.firstOrNull() ?: return@setOnItemClickListener
            val selectedTitleQrList = selectedQrTitleUriLine.split("\t")
            val selectedQrUri = selectedTitleQrList.last()
            QrUri.handler(
                fragment,
                currentAppDirPath,
                selectedQrUri
            )
            registerQrUriToHistory(
                currentAppDirPath,
                selectedQrTitle,
                selectedQrUri,
            )

        }
    }

    private fun makeQrTitleList(): List<Pair<String, Int>> {
        return makeQrHistoryList().map {
            val titleUriList = it.split("\t")
            val title = titleUriList.firstOrNull() ?: String()
            title to qrLogo
        }.filter { it.first.isNotEmpty() }.reversed()
    }

    private fun makeQrHistoryList(): List<String>
    {
        val qrHistoryParentDirPath = "$currentAppDirPath/${UsePath.cmdclickQrSystemDirRelativePath}"
        val cmdclickQrHistoryFileName = UsePath.cmdclickQrHistoryFileName
        return ReadText(
            qrHistoryParentDirPath,
            cmdclickQrHistoryFileName
        ).textToList()
    }
}


private fun registerQrUriToHistory(
    currentAppDirPath: String,
    title: String,
    selectedQrUri: String,
){
    val qrHistoryLimitRowSize = 50
    val qrHistoryParentDirPath =
        "${currentAppDirPath}/${UsePath.cmdclickQrSystemDirRelativePath}"
    val cmdclickQrHistoryFileName = UsePath.cmdclickQrHistoryFileName
    val qrHistoryList = ReadText(
        qrHistoryParentDirPath,
        cmdclickQrHistoryFileName
    ).textToList().take(qrHistoryLimitRowSize)
    val registerHistoryList = listOf("$title\t$selectedQrUri") + qrHistoryList.filter {
        val hisTitleUriList = it.split("\t")
        val hisTitle = hisTitleUriList.firstOrNull() ?: String()
        title != hisTitle
    }
    FileSystems.writeFile(
        qrHistoryParentDirPath,
        cmdclickQrHistoryFileName,
        registerHistoryList.joinToString("\n")
    )
}
