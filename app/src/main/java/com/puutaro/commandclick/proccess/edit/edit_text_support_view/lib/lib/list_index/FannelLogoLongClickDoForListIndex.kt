package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.list_index

import android.app.Dialog
import android.graphics.BitmapFactory
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import coil.load
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.qr.QrDialogMethod
import com.puutaro.commandclick.util.CcPathTool

object FannelLogoLongClickDoForListIndex {

    private var qrLogoDialogObj: Dialog? = null

    fun invoke(
        editFragment: EditFragment,
        currentAppDirPath: String,
    ) {

        val context = editFragment.context
            ?: return
        val binding = editFragment.binding
        val listIndexForEditAdapter = binding.editListRecyclerView.adapter as ListIndexForEditAdapter
        listIndexForEditAdapter.qrLongClickListener =
            object: ListIndexForEditAdapter.OnQrLogoLongClickListener {
                override fun onQrLongClick(
                    imageView: AppCompatImageView,
                    holder: ListIndexForEditAdapter.ListIndexListViewHolder,
                    isConQr: Boolean,
                    position: Int
                ) {
                    val fannelName = holder.fannelNameTextView.text.toString()
                    if(
                        fannelName.isEmpty()
                        || fannelName == "-"
                    ) {
                        Toast.makeText(
                            context,
                            "no file",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }
                    val fannelDirName = CcPathTool.makeFannelDirName(fannelName)
                    val qrLogoPath = "${currentAppDirPath}/$fannelDirName/${UsePath.qrPngRelativePath}"
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
                    val buttonWeight = decideButtonWeight(isConQr)
                    setPassButton(
                        editFragment,
                        currentAppDirPath,
                        fannelName,
                        buttonWeight,
                    )
                    setShareButton(
                        editFragment,
                        qrLogoPath,
                        buttonWeight
                    )
                    setConUpdateButton(
                        editFragment,
                        currentAppDirPath,
                        fannelName,
                        buttonWeight,
                        isConQr
                    )
                    setChangeButton(
                        editFragment,
                        currentAppDirPath,
                        fannelName,
                        buttonWeight,
                        isConQr
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
//                    qrLogoDialogObj?.window?.setLayout(
//                        ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT
//                    )
                    qrLogoDialogObj?.window?.setGravity(Gravity.BOTTOM)
                    qrLogoDialogObj?.show()
                }
            }
    }

    private fun decideButtonWeight(isConQr: Boolean): Float {
        val fourWeight = 0.25f
        val fileWeight = 0.2f
        return when(isConQr){
            true -> fourWeight
            else -> fileWeight
        }
    }

    private fun setShareButton(
        editFragment: EditFragment,
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
                editFragment,
                myBitmap
            )
        }
    }

    private fun setPassButton(
        editFragment: EditFragment,
        currentAppDirPath: String,
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
                editFragment,
                currentAppDirPath,
                fannelName,
            )
        }
    }

    private fun setChangeButton(
        editFragment: EditFragment,
        currentAppDirPath: String,
        fannelName: String,
        buttonWeight: Float,
        isConQr: Boolean
    ){
        val changeButton = qrLogoDialogObj?.findViewById<AppCompatImageButton>(
            R.id.qr_logo_list_index_dialog_change
        )?: return
        val layoutParams = changeButton.layoutParams as LinearLayoutCompat.LayoutParams
        layoutParams.weight = buttonWeight
        changeButton.setOnClickListener {
            QrDialogMethod.execChange(
                editFragment,
                currentAppDirPath,
                fannelName,
                qrLogoDialogObj,
                R.id.qr_logo_list_index_dialog_top_image,
                isConQr,
            )
        }
    }

    private fun setConUpdateButton(
        editFragment: EditFragment,
        currentAppDirPath: String,
        fannelName: String,
        buttonWeight: Float,
        isConQr: Boolean,
    ){
        val conUpdateButton = qrLogoDialogObj?.findViewById<AppCompatImageButton>(
            R.id.qr_logo_list_index_dialog_con_update
        )?: return
        if(!isConQr){
            conUpdateButton.isVisible = false
            return
        }
        val layoutParams = conUpdateButton.layoutParams as LinearLayoutCompat.LayoutParams
        layoutParams.weight = buttonWeight
        conUpdateButton.setOnClickListener {
            QrDialogMethod.execConUpdate(
                editFragment,
                currentAppDirPath,
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
