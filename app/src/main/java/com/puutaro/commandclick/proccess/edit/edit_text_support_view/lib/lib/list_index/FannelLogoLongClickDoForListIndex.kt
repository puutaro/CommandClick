package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.list_index

import android.app.Dialog
import android.graphics.BitmapFactory
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import coil.load
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.qr.QrDialogMethod
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.CommandClickVariables

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
                        R.layout.qr_logo_dialog_layout
                    )
                    val titleTextView = qrLogoDialogObj?.findViewById<AppCompatTextView>(
                        R.id.qr_logo_dialog_title
                    )

                    titleTextView?.text = fannelName
                    qrLogoDialogObj?.findViewById<AppCompatImageView>(
                        R.id.qr_logo_dialog_top_image
                    )?.load(qrLogoPath)

                    setPassButton(
                        editFragment,
                        currentAppDirPath,
                        fannelName,
                    )
                    setShareButton(
                        editFragment,
                        qrLogoPath
                    )
                    setChangeButton(
                        editFragment,
                        currentAppDirPath,
                        fannelName,
                    )
                    val cancelButton = qrLogoDialogObj?.findViewById<AppCompatImageButton>(
                        R.id.qr_logo_dialog_cancel
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

    private fun setShareButton(
        editFragment: EditFragment,
        qrLogoPath: String,
    ){
        val shareButton = qrLogoDialogObj?.findViewById<AppCompatImageButton>(
            R.id.qr_logo_dialog_share
        )
        shareButton?.setOnClickListener {
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
    ){
        val passButton = qrLogoDialogObj?.findViewById<AppCompatImageButton>(
            R.id.qr_logo_dialog_pass
        )
        passButton?.setOnClickListener {
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
    ){
        val changeButton = qrLogoDialogObj?.findViewById<AppCompatImageButton>(
            R.id.qr_logo_dialog_change
        )
        changeButton?.setOnClickListener {
            QrDialogMethod.execChange(
                editFragment,
                currentAppDirPath,
                fannelName,
                qrLogoDialogObj
            )
        }
    }
}
