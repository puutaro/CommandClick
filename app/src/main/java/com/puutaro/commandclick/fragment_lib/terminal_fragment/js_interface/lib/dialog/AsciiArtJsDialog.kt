package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.Spannable
import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.bachors.img2ascii.Img2Ascii
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.Intent.IntentVariant
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.ScreenSizeCalculator
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.image_tools.BitmapTool
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream


class AsciiArtJsDialog(
    private val terminalFragment: TerminalFragment
) {
    val context = terminalFragment.context
    val activity = terminalFragment.activity
    private val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
    private var spannableDialogObj: Dialog? = null
    private val asciiArtMapSeparator = '|'
    private var isSuccess = false

    fun create(
        title: String,
        imagePath: String,
        asciiArtMapCon: String,
    ): Boolean {
        isSuccess = false
        terminalViewModel.onDialog = true
        runBlocking {
            val asciiArtSpannable = withContext(Dispatchers.IO){
                makeAsciiArt(
                    imagePath,
                )

            }
            val asciiArtMap = withContext(Dispatchers.IO) {
                CmdClickMap.createMap(
                    asciiArtMapCon,
                    asciiArtMapSeparator
                ).toMap()
            }
            withContext(Dispatchers.Main) {
                execCreate(
                    title,
                    asciiArtSpannable,
                    asciiArtMap
                )
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
        spannable: Spannable?,
        asciiArtMap: Map<String, String>
    ){
        if(
            context == null
        ){
            terminalViewModel.onDialog = false
            return
        }

        spannableDialogObj = Dialog(
            context
        )
        spannableDialogObj?.setContentView(
            com.puutaro.commandclick.R.layout.spannable_grid_dialog_layout
        )
        val spannableTitleLinearLayout = spannableDialogObj?.findViewById<LinearLayoutCompat>(
            com.puutaro.commandclick.R.id.spannable_dialog_title_linearlayout
        )
        val titleTextView = spannableDialogObj?.findViewById<AppCompatTextView>(
            com.puutaro.commandclick.R.id.spannable_dialog_title
        )
        if(
            title.isNotEmpty()
        ) titleTextView?.text = title
        else spannableTitleLinearLayout?.isVisible = false
        val spannableTextView = spannableDialogObj?.findViewById<AppCompatTextView>(
            com.puutaro.commandclick.R.id.spannable_dialog_contents
        )
        spannableTextView?.text = spannable
        val shareButton = setShareButton()
        val cancelButton = spannableDialogObj?.findViewById<AppCompatImageButton>(
            com.puutaro.commandclick.R.id.spannable_dialog_cancel
        )
        cancelButton?.setOnClickListener {
            exitDialog(false)
        }
        val okButton = spannableDialogObj?.findViewById<AppCompatImageButton>(
            com.puutaro.commandclick.R.id.spannable_dialog_ok
        )
        okButton?.setOnClickListener {
            exitDialog(true)
        }
        spannableDialogObj?.setOnCancelListener {
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
            asciiArtMap,
            hideButtonMap,
            3,
        )
        spannableDialogObj?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        spannableDialogObj?.window?.setGravity(Gravity.BOTTOM)
        spannableDialogObj?.show()
        CoroutineScope(Dispatchers.IO).launch{
            val savePath = withContext(Dispatchers.IO){
                asciiArtMap.get(JsDialogButtonMapKey.SAVE_PATH.key)
            }
            if(
                savePath.isNullOrEmpty()
            ) return@launch
            val spannableImagePathObj =
                withContext(Dispatchers.IO) {
                    makeSpannableImageFromView()
                } ?: return@launch
            withContext(Dispatchers.IO) {
                for(i in 1..30) {
                    delay(200)
                    if(
                        spannableImagePathObj.isFile
                    ) break
                }
            }
            withContext(Dispatchers.IO){
                FileSystems.copyFile(
                    spannableImagePathObj.absolutePath,
                    savePath
                )
            }
        }
    }

    private fun exitDialog(isOk: Boolean){
        isSuccess = isOk
        terminalViewModel.onDialog = false
        spannableDialogObj?.dismiss()
        spannableDialogObj = null
    }

    private fun setShareButton(): AppCompatImageButton?  {
        val shareButton = spannableDialogObj?.findViewById<AppCompatImageButton>(
            com.puutaro.commandclick.R.id.spannable_dialog_share
        )
        shareButton?.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val spannableImagePathObj =
                    withContext(Dispatchers.IO) {
                        makeSpannableImageFromView()
                    } ?: return@launch

                IntentVariant.sharePngImage(
                    spannableImagePathObj,
                    context,
                    activity
                )
            }
        }
        return shareButton
    }

    private suspend fun makeSpannableImageFromView(): File? {
        withContext(Dispatchers.IO) {
            FileSystems.removeAndCreateDir(
                UsePath.cmdclickTempCreateDirPath
            )
        }
        val spannableContents = withContext(Dispatchers.Main) {
            spannableDialogObj?.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.spannable_dialog_contents
            )
        }?: return null
        val bitmap = withContext(Dispatchers.Main) {
            for(i in 1..10) {
                try {
                    val bm = BitmapTool.getScreenShotFromView(
                        spannableContents
                    )
                    return@withContext bm
                } catch (e: Exception) {
                    delay(100)
                    continue
                }
            }
            LogSystems.stdErr(
                context,
                "Cannot save screen shot"
            )
            null
        } ?: return null
        val imageName = withContext(Dispatchers.IO) {
            BitmapTool.hash(
                bitmap
            ) + ".png"
        }
        val file = File(
            UsePath.cmdclickTempCreateDirPath,
            imageName
        )
        withContext(Dispatchers.IO) {
            FileOutputStream(file).use { stream ->
                bitmap.compress(
                    Bitmap.CompressFormat.PNG,
                    100,
                    stream
                )
            }
        }
        return file
    }

    private suspend fun makeAsciiArt(
        imagePath: String,
    ): Spannable? {
        if(
            !File(imagePath).isFile
        ) return null
        val beforeResizeBitMap = withContext(
            Dispatchers.IO
        ) {
            BitmapFactory.decodeFile(imagePath)
        } ?: return null
        withContext(Dispatchers.IO){
            for(i in 1..20){
                try {
                    if(
                        beforeResizeBitMap.width > 0
                    ) break
                } catch (e: Exception) {
                    delay(100)
                    continue
                }
                delay(100)
            }
        }
        val baseWidth = withContext(Dispatchers.IO) {
            ScreenSizeCalculator.dpWidth(terminalFragment)
        }
        val beforeResizeBitMapWidth = withContext(Dispatchers.IO){
            beforeResizeBitMap.width
        }
        val resizeScale: Double = withContext(Dispatchers.IO
        ) {
            (baseWidth / beforeResizeBitMapWidth).toDouble()
        }
        val bitMap = withContext(Dispatchers.IO) {
            Bitmap.createScaledBitmap(
                beforeResizeBitMap,
                (beforeResizeBitMapWidth * resizeScale).toInt(),
                (beforeResizeBitMap.height * resizeScale).toInt(),
                true
            )
        }
        var htmlSpannableStr: Spannable? = null

        withContext(Dispatchers.IO) {
            Img2Ascii()
                .bitmap(bitMap)
                .quality(5) // 1 - 5
                .color(true)
                .convert(object : Img2Ascii.Listener {
                    override fun onProgress(percentage: Int) {
//                                    textView.setText("$percentage %")
                    }

                    override fun onResponse(text: Spannable) {
                        htmlSpannableStr = text
                    }
                })
        }
        withContext(Dispatchers.IO){
            for(i in 1..50){
                delay(100)
                if(
                    !htmlSpannableStr.isNullOrEmpty()
                ) break
            }
        }
    return htmlSpannableStr
    }

}
