package com.puutaro.commandclick.proccess.qr

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
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
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeForEdit
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ExecAddForListIndexAdapter
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.EditSettingExtraArgsTool
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.TypeSettingsForListIndex
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class QrScanner(
    private val fragment: Fragment,
    private val currentAppDirPath: String,
    private val stockDirAndCompMap: Map<String, String>? = null,
) {
    private val fragContext = fragment.context

    private var qrScanDialogObj: Dialog? = null

    fun scanFromImage(
        qrImagePath: String,
    ): String {
        var errMessage = String()
        for(i in 1..3) {
            try {
                return execScanFromImage(qrImagePath)
            } catch (e: Exception) {
                errMessage = e.toString()
                continue
            }
        }
        return errMessage
    }
    private fun execScanFromImage(
        qrImagePath: String,
    ): String {
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

    fun scanFromCamera() {
        val terminalViewModel: TerminalViewModel by fragment.activityViewModels()
        getCameraPermission()
        CoroutineScope(Dispatchers.IO).launch {
            val isCameraPermission = withContext(Dispatchers.IO) {
                howCameraPermission(terminalViewModel)
            }
            if(!isCameraPermission) return@launch
            withContext(Dispatchers.Main) {
                launchCameraDialog()
            }
        }
    }

    fun saveFromCamera(
        fileName: String,
    ) {
        val terminalViewModel: TerminalViewModel by fragment.activityViewModels()
        getCameraPermission()
        CoroutineScope(Dispatchers.IO).launch {
            val isCameraPermission = withContext(Dispatchers.IO) {
                howCameraPermission(terminalViewModel)
            }
            if(!isCameraPermission) return@launch
            withContext(Dispatchers.Main) {
                launchCameraDialogForSave(fileName)
            }
        }
    }
    private suspend fun howCameraPermission(
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

    private fun getCameraPermission(){
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

    private fun launchCameraDialogForSave(
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
                        fileName,
                        decodeText
                    )
                }
                qrScanDialogObj?.dismiss()
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
        fileName: String,
        decodeText: String
    ){
        if(
            fragment is CommandIndexFragment
            || fragment is TerminalFragment
        ) {
            addFile(
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
                fileName,
                decodeText
            )
            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT -> {
                if(
                    stockDirAndCompMap == null
                ) return
                val parentDirPath = EditSettingExtraArgsTool.getParentDirPath(
                    stockDirAndCompMap,
                    currentAppDirPath,
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
        fileName: String,
        decodeText: String
    ){
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
        qrScanDialogObj?.dismiss()
    }

    private suspend fun loadDecodedText(
        fragment: Fragment,
        qrScanDialogObj: Dialog?,
        codeScanner: CodeScanner,
        decodeText: String,
    ){
        val title = QrDecodedTitle.makeTitle(
            fragContext,
            decodeText
        )
        QrConfirmDialog(
            fragment,
            qrScanDialogObj,
            codeScanner,
            currentAppDirPath,
            title,
            decodeText,
        ).launch()
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
}
