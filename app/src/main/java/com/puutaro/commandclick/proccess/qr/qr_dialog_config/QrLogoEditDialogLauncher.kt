package com.puutaro.commandclick.proccess.qr.qr_dialog_config

import android.app.Dialog
import android.graphics.BitmapFactory
import android.view.Gravity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import coil.load
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.qr.QrDialogConfig
import com.puutaro.commandclick.proccess.qr.QrDialogMethod
import com.puutaro.commandclick.proccess.qr.qr_dialog_config.config_settings.QrLogoSettingsForQrDialog
import com.puutaro.commandclick.proccess.qr.qr_dialog_config.config_settings.QrTypeSettingsForQrDialog
import com.puutaro.commandclick.util.CcPathTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object QrLogoEditDialogLauncher {

    private var qrLogoDialogObj: Dialog? = null

    fun launch(
        fragment: Fragment,
        parentDirPath: String,
        fannelName: String,
        qrDialogConfigMap: Map<String, String>,
    ){

        val logoConfigMap = QrLogoSettingsForQrDialog.makeLogoConfigMap(qrDialogConfigMap)
        val disableLogo =
            QrLogoSettingsForQrDialog.Disable.how(logoConfigMap)
        when(disableLogo){
            true -> QrDialogMethod.launchPassDialog(
                fragment,
                parentDirPath,
                fannelName,
            )
            else -> launchQrConDialog(
                fragment,
                parentDirPath,
                fannelName,
                logoConfigMap
            )
        }
        qrLogoDialogObj?.window?.setGravity(Gravity.BOTTOM)
        qrLogoDialogObj?.show()
    }

    private fun launchQrConDialog(
        fragment: Fragment,
        parentDirPath: String,
        fannelName: String,
        logoConfigMap: Map<String, String>,
    ){
        val context = fragment.context
            ?: return
        val fannelDirName = CcPathTool.makeFannelDirName(fannelName)
        val qrLogoPath = "${parentDirPath}/$fannelDirName/${UsePath.qrPngRelativePath}"
        qrLogoDialogObj = Dialog(
            context
        )
        qrLogoDialogObj?.setContentView(
            R.layout.qr_logo_list_index_dialog_layout
        )
        val titleTextView = qrLogoDialogObj?.findViewById<AppCompatTextView>(
            R.id.qr_logo_list_index_dialog_title
        )

        titleTextView?.text = fannelName
        qrLogoDialogObj?.findViewById<AppCompatImageView>(
            R.id.qr_logo_list_index_dialog_top_image
        )?.load(qrLogoPath)
        val isFileCon =
            QrLogoSettingsForQrDialog.Type.how(logoConfigMap) ==
                    QrTypeSettingsForQrDialog.QrTypeSettingKey.FILE_CON.type
        val buttonWeight = decideButtonWeight(isFileCon)
        setPassButton(
            fragment,
            parentDirPath,
            fannelName,
            buttonWeight,
        )
        setShareButton(
            fragment,
            qrLogoPath,
            buttonWeight
        )
        setConUpdateButton(
            fragment,
            parentDirPath,
            fannelName,
            buttonWeight,
            isFileCon
        )
        setChangeButton(
            fragment,
            parentDirPath,
            fannelName,
            buttonWeight,
            isFileCon
        )
        setCancelButton(buttonWeight)
        val cancelButton = qrLogoDialogObj?.findViewById<AppCompatImageButton>(
            R.id.qr_logo_list_index_dialog_cancel
        )
        cancelButton?.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                qrLogoDialogObj?.dismiss()
            }
        }
        qrLogoDialogObj?.setOnCancelListener {
            CoroutineScope(Dispatchers.Main).launch {
                qrLogoDialogObj?.dismiss()
            }
        }
    }

    private fun decideButtonWeight(isFileCon: Boolean): Float {
        val fourWeight = 0.25f
        val fileWeight = 0.2f
        return when(isFileCon){
            true -> fourWeight
            else -> fileWeight
        }
    }

    private fun setShareButton(
        fragment: Fragment,
        qrLogoPath: String,
        buttonWeight: Float,
    ){
        CoroutineScope(Dispatchers.Main).launch {
            val shareButton = withContext(Dispatchers.Main) {
                qrLogoDialogObj?.findViewById<AppCompatImageButton>(
                    R.id.qr_logo_list_index_dialog_share
                )
            } ?: return@launch
            withContext(Dispatchers.Main) {
                val layoutParams = shareButton.layoutParams as LinearLayoutCompat.LayoutParams
                layoutParams.weight = buttonWeight
            }
            shareButton.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.Main) {
                        qrLogoDialogObj?.dismiss()
                    }
                    val myBitmap =
                        withContext(Dispatchers.Main) {
                            BitmapFactory.decodeFile(
                                qrLogoPath
                            )
                        }
                    withContext(Dispatchers.Main) {
                        QrDialogMethod.execShare(
                            fragment,
                            myBitmap
                        )
                    }
                }
            }
        }
    }

    private fun setPassButton(
        fragment: Fragment,
        parentDirPath: String,
        fannelName: String,
        buttonWeight: Float,
    ){
        CoroutineScope(Dispatchers.Main).launch {
            val passButton = withContext(Dispatchers.Main) {
                qrLogoDialogObj?.findViewById<AppCompatImageButton>(
                    R.id.qr_logo_list_index_dialog_pass
                )
            } ?: return@launch
            withContext(Dispatchers.Main) {
                val layoutParams = passButton.layoutParams as LinearLayoutCompat.LayoutParams
                layoutParams.weight = buttonWeight
            }
            passButton.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.Main){
                        qrLogoDialogObj?.dismiss()
                    }
                    withContext(Dispatchers.Main) {
                        QrDialogMethod.launchPassDialog(
                            fragment,
                            parentDirPath,
                            fannelName,
                        )
                    }
                }
            }
        }
    }

    private fun setChangeButton(
        fragment: Fragment,
        parentDirPath: String,
        fannelName: String,
        buttonWeight: Float,
        isFileCon: Boolean
    ){
        CoroutineScope(Dispatchers.Main).launch {
            val changeButton = withContext(Dispatchers.Main) {
                qrLogoDialogObj?.findViewById<AppCompatImageButton>(
                    R.id.qr_logo_list_index_dialog_change
                )
            } ?: return@launch
            withContext(Dispatchers.Main) {
                val layoutParams = changeButton.layoutParams as LinearLayoutCompat.LayoutParams
                layoutParams.weight = buttonWeight
            }
            changeButton.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch {
                    QrDialogMethod.execChange(
                        fragment,
                        parentDirPath,
                        fannelName,
                        qrLogoDialogObj,
                        R.id.qr_logo_list_index_dialog_top_image,
                        isFileCon,
                    )
                }
            }
        }
    }

    private fun setConUpdateButton(
        fragment: Fragment,
        parentDirPath: String,
        fannelName: String,
        buttonWeight: Float,
        isFileCon: Boolean,
    ){
        CoroutineScope(Dispatchers.Main).launch {
            val conUpdateButton = withContext(Dispatchers.Main) {
                qrLogoDialogObj?.findViewById<AppCompatImageButton>(
                    R.id.qr_logo_list_index_dialog_con_update
                )
            } ?: return@launch
            if (!isFileCon) {
                withContext(Dispatchers.Main) {
                    conUpdateButton.isVisible = false
                }
                return@launch
            }
            withContext(Dispatchers.Main) {
                val layoutParams = conUpdateButton.layoutParams as LinearLayoutCompat.LayoutParams
                layoutParams.weight = buttonWeight
            }
            conUpdateButton.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch {
                    QrDialogMethod.execConUpdate(
                        fragment,
                        parentDirPath,
                        fannelName,
                        qrLogoDialogObj,
                        R.id.qr_logo_list_index_dialog_top_image
                    )
                }
            }
        }
    }

    private fun setCancelButton(
        buttonWeight: Float,
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            val cancelButton = withContext(Dispatchers.Main) {
                qrLogoDialogObj?.findViewById<AppCompatImageButton>(
                    R.id.qr_logo_list_index_dialog_cancel
                )
            } ?: return@launch
            withContext(Dispatchers.Main) {
                val layoutParams = cancelButton.layoutParams as LinearLayoutCompat.LayoutParams
                layoutParams.weight = buttonWeight
            }
            cancelButton.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch {
                    qrLogoDialogObj?.dismiss()
                }
            }
        }
    }
}