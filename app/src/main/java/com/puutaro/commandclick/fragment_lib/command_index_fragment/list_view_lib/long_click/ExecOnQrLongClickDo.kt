package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.long_click

import android.app.Dialog
import android.graphics.BitmapFactory
import android.view.Gravity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import coil.load
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.FannelIndexListAdapter
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.proccess.qr.QrDialogMethod
import com.puutaro.commandclick.util.CcPathTool

object ExecOnQrLongClickDo {

    private var qrLogoDialogObj: Dialog? = null

    fun invoke(
        cmdIndexFragment: CommandIndexFragment,
        currentAppDirPath: String,
        fannelIndexListAdapter: FannelIndexListAdapter
    ) {

        val context = cmdIndexFragment.context
            ?: return
        fannelIndexListAdapter.qrLongClickListener =
            object: FannelIndexListAdapter.OnQrLongClickListener {
                override fun onQrLongClick(
                    imageView: AppCompatImageView,
                    holder: FannelIndexListAdapter.FannelIndexListViewHolder,
                    position: Int
                ) {
                    val fannelName = holder.fannelNameTextView.text.toString()
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
                        cmdIndexFragment,
                        currentAppDirPath,
                        fannelName,
                    )
                    setShareButton(
                        cmdIndexFragment,
                        qrLogoPath
                    )
                    setChangeButton(
                        cmdIndexFragment,
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
        cmdIndexFragment: CommandIndexFragment,
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
                cmdIndexFragment,
                myBitmap
            )
        }
    }

    private fun setPassButton(
        cmdIndexFragment: CommandIndexFragment,
        currentAppDirPath: String,
        fannelName: String,
    ){
        val passButton = qrLogoDialogObj?.findViewById<AppCompatImageButton>(
            R.id.qr_logo_dialog_pass
        )
        passButton?.setOnClickListener {
            qrLogoDialogObj?.dismiss()
            QrDialogMethod.launchPassDialog(
                cmdIndexFragment,
                currentAppDirPath,
                fannelName,
            )
        }
    }

    private fun setChangeButton(
        cmdIndexFragment: CommandIndexFragment,
        currentAppDirPath: String,
        fannelName: String,
    ){
        val changeButton = qrLogoDialogObj?.findViewById<AppCompatImageButton>(
            R.id.qr_logo_dialog_change
        )
        changeButton?.setOnClickListener {
            QrDialogMethod.execChange(
                cmdIndexFragment,
                currentAppDirPath,
                fannelName,
                qrLogoDialogObj,
                R.id.qr_logo_dialog_top_image
            )
        }
    }
}

