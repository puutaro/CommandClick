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
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeForEdit
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.SubMenuAdapter
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ExecAddForListIndexAdapter
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.EditSettingExtraArgsTool
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.TypeSettingsForListIndex
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


object QrScanner{

    private var qrScanDialogObj: Dialog? = null
    fun scanFromImage(
        fragment: Fragment,
        qrImagePath: String,
    ): String {
        var errMessage = String()
        for(i in 1..3) {
            try {
                return execScanFromImage(
                    fragment,
                    qrImagePath
                )
            } catch (e: Exception) {
                errMessage = e.toString()
                continue
            }
        }
        return errMessage
    }
    private fun execScanFromImage(
        fragment: Fragment,
        qrImagePath: String,
    ): String {
        val fragContext = fragment.context
        try {
            val bMap = BitmapFactory.decodeFile(qrImagePath)

            val intArray = IntArray(bMap.width * bMap.height)
            bMap.getPixels(intArray, 0, bMap.width, 0, 0, bMap.width, bMap.height)

            val source: LuminanceSource =
                RGBLuminanceSource(bMap.width, bMap.height, intArray)
            val bitmap = BinaryBitmap(HybridBinarizer(source))

            val reader = MultiFormatReader()
            return reader.decode(bitmap).text
        } catch (e: Exception){
            val errOutput = e.toString()
            LogSystems.stdErr(
                fragContext,
                errOutput
            )
            return errOutput
        }
    }

    fun scanFromCamera(
        fragment: Fragment,
//        currentAppDirPath: String,
    ) {
        val terminalViewModel: TerminalViewModel by fragment.activityViewModels()
        getCameraPermission(
            fragment,
        )
        CoroutineScope(Dispatchers.IO).launch {
            val isCameraPermission = withContext(Dispatchers.IO) {
                howCameraPermission(
                    fragment,
                    terminalViewModel
                )
            }
            if(!isCameraPermission) return@launch
            withContext(Dispatchers.Main) {
                launchCameraDialog(
                    fragment,
//                    currentAppDirPath,
                )
            }
        }
    }

    fun saveFromCamera(
        fragment: Fragment,
        currentAppDirPath: String,
        stockDirAndCompMap: Map<String, String>? = null,
        qrImagePath: String,
    ) {
        val terminalViewModel: TerminalViewModel by fragment.activityViewModels()
        getCameraPermission(
            fragment,
        )
        CoroutineScope(Dispatchers.IO).launch {
            val isCameraPermission = withContext(Dispatchers.IO) {
                howCameraPermission(
                    fragment,
                    terminalViewModel
                )
            }
            if(!isCameraPermission) return@launch
            withContext(Dispatchers.Main) {
                launchCameraDialogForSave(
                    fragment,
                    currentAppDirPath,
                    stockDirAndCompMap,
                    qrImagePath,
                )
            }
        }
    }
    private suspend fun howCameraPermission(
        fragment: Fragment,
        terminalViewModel: TerminalViewModel
    ): Boolean {
        val activity = fragment.activity
            ?: return false
        for (i in 1..100) {
            if (!terminalViewModel.onPermDialog) break
            delay(100)
        }
       return ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getCameraPermission(
        fragment: Fragment,
    ){
        val fragContext = fragment.context
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
    }


    private suspend fun launchCameraDialog(
        fragment: Fragment,
//        currentAppDirPath: String,
    ) {
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
//                    currentAppDirPath,
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
        val cancelButton = makeCancelButton(
            fragment,
            codeScanner
        )
        qrScanBottomLinearLayout.addView(cancelButton)
        val historyButton = makeHistoryButton(
            fragment,
//            currentAppDirPath,
            codeScanner,
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

    private fun launchCameraDialogForSave(
        fragment: Fragment,
        currentAppDirPath: String,
        stockDirAndCompMap: Map<String, String>? = null,
        fileName: String,
    ) {
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
                withContext(Dispatchers.IO) {
                    qrSaverForType(
                        fragment,
                        currentAppDirPath,
                        stockDirAndCompMap,
                        fileName,
                        decodeText
                    )
                }
                qrDialogDismiss()
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
        val cancelButton = makeCancelButton(
            fragment,
            codeScanner,
            1F,
        )
        qrScanBottomLinearLayout.addView(cancelButton)
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

    private suspend fun qrSaverForType(
        fragment: Fragment,
        currentAppDirPath: String,
        stockDirAndCompMap: Map<String, String>? = null,
        fileName: String,
        decodeText: String
    ){
        if(
            fragment is CommandIndexFragment
            || fragment is TerminalFragment
        ) {
            addFile(
                fragment,
                currentAppDirPath,
                fileName,
                decodeText
            )
            return
        }
        if(
            fragment !is EditFragment
        ) return
        val type = ListIndexEditConfig.getListIndexType(
            fragment
        )
        when(type){
            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL
            -> return
            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
            -> addFile(
                fragment,
                currentAppDirPath,
                fileName,
                decodeText
            )
            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT -> {
                if(
                    stockDirAndCompMap == null
                ) return
                val parentDirPath = EditSettingExtraArgsTool.getParentDirPath(
                    stockDirAndCompMap,
//                    currentAppDirPath,
                )
                val compFileName = EditSettingExtraArgsTool.makeCompFileName(
                    fragment,
                    fileName,
                    stockDirAndCompMap
                )
                FileSystems.writeFile(
                    File(
                        parentDirPath,
                        compFileName
                    ).absolutePath,
                    decodeText,
                )
                val insertLine = "${compFileName}\t${File(parentDirPath, compFileName).absolutePath}"
                withContext(Dispatchers.Main) {
                    ExecAddForListIndexAdapter.execAddForTsv(
                        fragment,
                        insertLine
                    )
                }
            }
        }
    }

    private fun addFile(
        fragment: Fragment,
        currentAppDirPath: String,
        fileName: String,
        decodeText: String
    ){
        val fragContext = fragment.context
        FileSystems.writeFile(
            File(
                currentAppDirPath,
                fileName
            ).absolutePath,
            decodeText
        )
        BroadcastSender.normalSend(
            fragContext,
            BroadCastIntentSchemeForEdit.UPDATE_INDEX_LIST.action
        )
    }

    private fun dismissProcess(
        codeScanner: CodeScanner
    ){
        codeScanner.releaseResources()
        qrDialogDismiss()
    }

    private suspend fun loadDecodedText(
        fragment: Fragment,
//        currentAppDirPath: String,
        codeScanner: CodeScanner,
        decodeText: String,
    ){
        val fragContext = fragment.context
        val title = QrDecodedTitle.makeTitle(
            fragContext,
            decodeText
        )
        QrConfirmDialog(
            fragment,
            codeScanner,
//            currentAppDirPath,
            title,
            decodeText,
        ).launch()
    }

    private fun makeHistoryButton(
        fragment: Fragment,
//        currentAppDirPath: String,
        codeScanner: CodeScanner,
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
        imageButton.setImageResource(R.drawable.icons8_history)
        imageButton.setOnClickListener {
            codeScanner.releaseResources()
            QrHistoryListDialog.launch(
                fragment,
                codeScanner,
//                currentAppDirPath,
            )
        }
        return imageButton
    }

    private fun makeCancelButton(
        fragment: Fragment,
        codeScanner: CodeScanner,
        weight: Float = 0.5F
    ): ImageButton {
        val fragContext = fragment.context
        val imageButton = ImageButton(fragContext)
        imageButton.imageTintList = fragContext?.getColorStateList(R.color.web_icon_color)
        imageButton.backgroundTintList = fragContext?.getColorStateList(R.color.white)
        val linearLayoutForImageButtonParam = LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        linearLayoutForImageButtonParam.weight = weight
        linearLayoutForImageButtonParam.gravity = Gravity.CENTER
        imageButton.layoutParams = linearLayoutForImageButtonParam
        imageButton.setImageResource(R.drawable.icons8_cancel)
        imageButton.setOnClickListener {
            dismissProcess(codeScanner)
        }
        return imageButton
    }

    object QrHistoryListDialog{

        private var subMenuDialog: Dialog? = null
        fun launch(
            fragment: Fragment,
            codeScanner: CodeScanner,
//            currentAppDirPath: String,
        ){
            val context = fragment.context ?: return

            subMenuDialog = Dialog(
                context
            )
            subMenuDialog?.setContentView(
                R.layout.submenu_dialog
            )
            setListView(
                fragment,
//                currentAppDirPath,
                codeScanner,
            )
            setCancelListener(codeScanner)
            subMenuDialog?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            subMenuDialog
                ?.window
                ?.setGravity(Gravity.BOTTOM)
            subMenuDialog?.show()
        }

        private fun setCancelListener(
            codeScanner: CodeScanner,
        ){
            val cancelImageButton =
                subMenuDialog?.findViewById<ImageButton>(
                    R.id.submenu_dialog_cancel
                )
            cancelImageButton?.setOnClickListener {
                codeScanner.startPreview()
                subMenuDialog?.dismiss()
                subMenuDialog = null
            }
            subMenuDialog?.setOnCancelListener {
                codeScanner.startPreview()
                subMenuDialog?.dismiss()
                subMenuDialog = null
            }
        }

        private fun setListView(
            fragment: Fragment,
//            currentAppDirPath: String,
            codeScanner: CodeScanner,
        ) {
            val context = fragment.context
                ?: return
            val subMenuListView =
                subMenuDialog?.findViewById<ListView>(
                    R.id.sub_menu_list_view
                )
            val subMenuPairList = makeQrTitleList(
//                currentAppDirPath
            )
            val subMenuAdapter = SubMenuAdapter(
                context,
                subMenuPairList.toMutableList()
            )
            subMenuListView?.adapter = subMenuAdapter
            subMenuItemClickListener(
                fragment,
                subMenuListView,
//                currentAppDirPath,
                codeScanner,
            )
        }

        private fun subMenuItemClickListener(
            fragment: Fragment,
            subMenuListView: ListView?,
//            currentAppDirPath: String,
            codeScanner: CodeScanner,
        ){
            subMenuListView?.setOnItemClickListener {
                    parent, view, position, id ->
                qrDialogDismiss()
                codeScanner.releaseResources()
                subMenuDialog?.dismiss()
                subMenuDialog = null
                val menuListAdapter = subMenuListView.adapter as SubMenuAdapter
                val selectedQrTitle = menuListAdapter.getItem(position)
                    ?: return@setOnItemClickListener
                val selectedQrTitleUriLine = makeQrHistoryList().filter {
                    val titleUriList = it.split("\t")
                    val title = titleUriList.firstOrNull() ?: String()
                    title == selectedQrTitle
                }.firstOrNull() ?: return@setOnItemClickListener
                val selectedTitleQrList = selectedQrTitleUriLine.split("\t")
                val selectedQrUri = selectedTitleQrList.filterIndexed{
                        index, s -> index > 0
                }.joinToString()
                QrUriHandler.handle(
                    fragment,
//                    currentAppDirPath,
                    selectedQrUri
                )
                QrHistoryManager.registerQrUriToHistory(
//                    currentAppDirPath,
                    selectedQrTitle,
                    selectedQrUri,
                )

            }
        }

        private fun makeQrTitleList(
//            currentAppDirPath: String
        ): List<Pair<String, Int>> {
            val qrLogo = R.drawable.icons_qr_code
            return makeQrHistoryList(
//                currentAppDirPath
            ).map {
                val titleUriList = it.split("\t")
                val title = titleUriList.firstOrNull() ?: String()
                title to qrLogo
            }.filter { it.first.isNotEmpty() }.reversed()
        }

        private fun makeQrHistoryList(
//            currentAppDirPath: String
        ): List<String>
        {
            val qrHistoryParentDirPath = "${UsePath.cmdclickDefaultAppDirPath}/${UsePath.cmdclickQrSystemDirRelativePath}"
            val cmdclickQrHistoryFileName = UsePath.cmdclickQrHistoryFileName
            return ReadText(
                File(
                    qrHistoryParentDirPath,
                    cmdclickQrHistoryFileName
                ).absolutePath
            ).textToList()
        }
    }

    fun qrDialogDismiss(){
        qrScanDialogObj?.dismiss()
        qrScanDialogObj = null
    }

}
