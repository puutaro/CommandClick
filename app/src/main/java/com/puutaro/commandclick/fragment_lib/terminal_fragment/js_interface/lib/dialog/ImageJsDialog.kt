package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.image_tools.BitmapTool
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.Intent.IntentVariant
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream


class ImageJsDialog(
    terminalFragment: TerminalFragment
) {
    val context = terminalFragment.context
    val activity = terminalFragment.activity
    private val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
    private var imageDialogObj: Dialog? = null
    private val imageDialogMapSeparator = '|'
    private var isSuccess = false

    fun create(
        title: String,
        imageSrcFilePath: String,
        imageDialogMapCon: String
    ): Boolean {
        if(
            !File(imageSrcFilePath).isFile
        ) {
            ToastUtils.showShort("no image file\n ${imageSrcFilePath}")
            return false
        }
        isSuccess = false
        terminalViewModel.onDialog = true
        val imageDialogMap = CmdClickMap.createMap(
            imageDialogMapCon,
            imageDialogMapSeparator
        ).toMap()
        runBlocking {
            withContext(Dispatchers.Main) {
                try {
                    execCreate(
                        title,
                        imageSrcFilePath,
                        imageDialogMap,
                    )
                } catch (e: Exception){
                    isSuccess = false
                    LogSystems.stdErr(
                        context,
                        e.toString()
                    )
                }
            }
            withContext(Dispatchers.IO) {
                while (true) {
                    delay(100)
                    if (!terminalViewModel.onDialog) break
                }
            }
        }
        return isSuccess
    }

    private fun execCreate(
        title: String,
        imageSrcFilePath: String,
        imageDialogMap: Map<String, String>?,
    ){

        if(
            context == null
        ){
            terminalViewModel.onDialog = false
            return
        }
        imageDialogObj = Dialog(
            context
        )
        imageDialogObj?.setContentView(
            com.puutaro.commandclick.R.layout.image_dialog_layout
        )
        val titleImageView = imageDialogObj?.findViewById<AppCompatImageView>(
            com.puutaro.commandclick.R.id.image_dialog_title_image
        )
        val titleTextView = imageDialogObj?.findViewById<AppCompatTextView>(
            com.puutaro.commandclick.R.id.image_dialog_title
        )
        if(
            title.isNotEmpty()
        ) titleTextView?.text = title
        else titleImageView?.isVisible = false
        val imageContentsView = imageDialogObj?.findViewById<AppCompatImageView>(
            com.puutaro.commandclick.R.id.image_dialog_image
        )
        val myBitmap: Bitmap = BitmapFactory.decodeFile(
            imageSrcFilePath
        )
        imageContentsView
            ?.setImageBitmap(myBitmap)
        val shareButton = setShareButton(
            myBitmap
        )
        val cancelButton = imageDialogObj?.findViewById<AppCompatImageButton>(
            com.puutaro.commandclick.R.id.image_dialog_cancel
        )
        cancelButton?.setOnClickListener {
            exitDialog(false)
        }
        val okButton = imageDialogObj?.findViewById<AppCompatImageButton>(
            com.puutaro.commandclick.R.id.image_dialog_ok
        )
        okButton?.setOnClickListener {
            exitDialog(true)
        }
        imageDialogObj?.setOnCancelListener {
            exitDialog(false)
        }
        val hideButtonMap = mapOf(
            HideDialogButton.HideButtonType.SHARE.type
                    to shareButton,
            HideDialogButton.HideButtonType.CANCEL.type
                    to cancelButton,
            HideDialogButton.HideButtonType.OK.type
                    to okButton,
        )
        HideDialogButton.buttonVisualHandler(
            imageDialogMap,
            hideButtonMap,
            3,
        )
        imageDialogObj?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        imageDialogObj?.window?.setGravity(Gravity.BOTTOM)
        imageDialogObj?.show()
    }

    private fun exitDialog(isOk: Boolean){
        isSuccess = isOk
        terminalViewModel.onDialog = false
        imageDialogObj?.dismiss()
        imageDialogObj = null
    }

    private fun setShareButton(
        myBitmap: Bitmap
    ): AppCompatImageButton? {
        val shareButton = imageDialogObj?.findViewById<AppCompatImageButton>(
            com.puutaro.commandclick.R.id.image_dialog_share
        )
        shareButton?.setOnClickListener {
            FileSystems.removeDir(
                UsePath.cmdclickTempCreateDirPath
            )
            FileSystems.createDirs(
                UsePath.cmdclickTempCreateDirPath
            )
            val imageName = BitmapTool.hash(
                myBitmap
            ) + ".png"
            val file = File(
                UsePath.cmdclickTempCreateDirPath,
                imageName
            )
            FileOutputStream(file).use { stream ->
                myBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            }
            IntentVariant.sharePngImage(
                file,
                context,
            )
        }
        return shareButton
    }
}