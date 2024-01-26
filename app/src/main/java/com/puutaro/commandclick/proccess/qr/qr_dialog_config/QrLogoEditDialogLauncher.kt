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
import com.puutaro.commandclick.proccess.qr.qr_dialog_config.config_settings.QrTypeSettingsForQrDialog
import com.puutaro.commandclick.util.CcPathTool

object QrLogoEditDialogLauncher {

    private var qrLogoDialogObj: Dialog? = null

    fun launch(
        fragment: Fragment,
        parentDirPath: String,
        fannelName: String,
        qrDialogConfigMap: Map<String, String>,
    ){

        val logoConfigMap = QrDialogConfig.makeLogoConfigMap(qrDialogConfigMap)
        val disableLogo =
            QrDialogConfig.howDisableQrLogo(logoConfigMap)
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

//        val context = fragment.context
//            ?: return
//        val fannelDirName = CcPathTool.makeFannelDirName(fannelName)
//        val qrLogoPath = "${parentDirPath}/$fannelDirName/${UsePath.qrPngRelativePath}"
//        qrLogoDialogObj = Dialog(
//            context
//        )
//        qrLogoDialogObj?.setContentView(
//            R.layout.qr_logo_list_index_dialog_layout
//        )
//        val titleTextView = qrLogoDialogObj?.findViewById<AppCompatTextView>(
//            R.id.qr_logo_list_index_dialog_title
//        )
//
//        titleTextView?.text = fannelName
//        qrLogoDialogObj?.findViewById<AppCompatImageView>(
//            R.id.qr_logo_list_index_dialog_top_image
//        )?.load(qrLogoPath)
//        val isFileCon =
//            QrDialogConfig.howQrType(logoConfigMap) ==
//                    QrDialogConfig.QrTypeSettingKey.FILE_CON.type
//        val buttonWeight = decideButtonWeight(isFileCon)
//        setPassButton(
//            fragment,
//            parentDirPath,
//            fannelName,
//            buttonWeight,
//        )
//        setShareButton(
//            fragment,
//            qrLogoPath,
//            buttonWeight
//        )
//        setConUpdateButton(
//            fragment,
//            parentDirPath,
//            fannelName,
//            buttonWeight,
//            isFileCon
//        )
//        setChangeButton(
//            fragment,
//            parentDirPath,
//            fannelName,
//            buttonWeight,
//            isFileCon
//        )
//        setCancelButton(buttonWeight)
//        val cancelButton = qrLogoDialogObj?.findViewById<AppCompatImageButton>(
//            R.id.qr_logo_list_index_dialog_cancel
//        )
//        cancelButton?.setOnClickListener {
//            qrLogoDialogObj?.dismiss()
//        }
//        qrLogoDialogObj?.setOnCancelListener {
//            qrLogoDialogObj?.dismiss()
//        }


//                    qrLogoDialogObj?.window?.setLayout(
//                        ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT
//                    )
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
            QrDialogConfig.howQrType(logoConfigMap) ==
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
            qrLogoDialogObj?.dismiss()
        }
        qrLogoDialogObj?.setOnCancelListener {
            qrLogoDialogObj?.dismiss()
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
        val shareButton = qrLogoDialogObj?.findViewById<AppCompatImageButton>(
            R.id.qr_logo_list_index_dialog_share
        )?: return
        val layoutParams = shareButton.layoutParams as LinearLayoutCompat.LayoutParams
        layoutParams.weight = buttonWeight
        shareButton.setOnClickListener {
            qrLogoDialogObj?.dismiss()
            val myBitmap =
                BitmapFactory.decodeFile(
                    qrLogoPath
                )
            QrDialogMethod.execShare(
                fragment,
                myBitmap
            )
        }
    }

    private fun setPassButton(
        fragment: Fragment,
        parentDirPath: String,
        fannelName: String,
        buttonWeight: Float,
    ){
        val passButton = qrLogoDialogObj?.findViewById<AppCompatImageButton>(
            R.id.qr_logo_list_index_dialog_pass
        )?: return
        val layoutParams = passButton.layoutParams as LinearLayoutCompat.LayoutParams
        layoutParams.weight = buttonWeight
        passButton.setOnClickListener {
            qrLogoDialogObj?.dismiss()
            QrDialogMethod.launchPassDialog(
                fragment,
                parentDirPath,
                fannelName,
            )
        }
    }

    private fun setChangeButton(
        fragment: Fragment,
        parentDirPath: String,
        fannelName: String,
        buttonWeight: Float,
        isFileCon: Boolean
    ){
        val changeButton = qrLogoDialogObj?.findViewById<AppCompatImageButton>(
            R.id.qr_logo_list_index_dialog_change
        )?: return
        val layoutParams = changeButton.layoutParams as LinearLayoutCompat.LayoutParams
        layoutParams.weight = buttonWeight
        changeButton.setOnClickListener {
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

    private fun setConUpdateButton(
        fragment: Fragment,
        parentDirPath: String,
        fannelName: String,
        buttonWeight: Float,
        isFileCon: Boolean,
    ){
        val conUpdateButton = qrLogoDialogObj?.findViewById<AppCompatImageButton>(
            R.id.qr_logo_list_index_dialog_con_update
        )?: return
        if(!isFileCon){
            conUpdateButton.isVisible = false
            return
        }
        val layoutParams = conUpdateButton.layoutParams as LinearLayoutCompat.LayoutParams
        layoutParams.weight = buttonWeight
        conUpdateButton.setOnClickListener {
            QrDialogMethod.execConUpdate(
                fragment,
                parentDirPath,
                fannelName,
                qrLogoDialogObj,
                R.id.qr_logo_list_index_dialog_top_image
            )
        }
    }

    private fun setCancelButton(
        buttonWeight: Float,
    ) {
        val cancelButton = qrLogoDialogObj?.findViewById<AppCompatImageButton>(
            R.id.qr_logo_list_index_dialog_cancel
        )?: return
        val layoutParams = cancelButton.layoutParams as LinearLayoutCompat.LayoutParams
        layoutParams.weight = buttonWeight
        cancelButton.setOnClickListener {
            qrLogoDialogObj?.dismiss()
        }
    }
}