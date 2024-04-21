package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageButton
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

    fun create(
        title: String,
        imageSrcFilePath: String
    ){
        if(
            !File(imageSrcFilePath).isFile
        ) {
            ToastUtils.showShort("no image file\n ${imageSrcFilePath}")
            return
        }
        terminalViewModel.onDialog = true
        runBlocking {
            withContext(Dispatchers.Main) {
                try {
                    execCreate(
                        title,
                        imageSrcFilePath
                    )
                } catch (e: Exception){
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
    }

    private fun execCreate(
        title: String,
        imageSrcFilePath: String
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
        setShareButton(
            myBitmap
        )
        val cancelButton = imageDialogObj?.findViewById<ImageButton>(
            com.puutaro.commandclick.R.id.image_dialog_ok
        )
        cancelButton?.setOnClickListener {
            terminalViewModel.onDialog = false
            imageDialogObj?.dismiss()
        }
        imageDialogObj?.setOnCancelListener {
            terminalViewModel.onDialog = false
            imageDialogObj?.dismiss()
        }
        imageDialogObj?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        imageDialogObj?.window?.setGravity(Gravity.BOTTOM)
        imageDialogObj?.show()
    }

    private fun setShareButton(
        myBitmap: Bitmap
    ){
        val shareButton = imageDialogObj?.findViewById<ImageButton>(
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
                activity
            )
        }
    }
}