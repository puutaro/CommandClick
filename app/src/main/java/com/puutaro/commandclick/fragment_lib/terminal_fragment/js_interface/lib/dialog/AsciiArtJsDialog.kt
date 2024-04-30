package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.Spannable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.bachors.img2ascii.Img2Ascii
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.Intent.IntentVariant
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

    fun create(
        title: String,
        imagePath: String,
        asciiArtMapCon: String,
    ){
        terminalViewModel.onDialog = true
        runBlocking {
            val asciiArtSpannable =  withContext(Dispatchers.IO){
                makeAsciiArt(
                    imagePath,
                )
            }
            val asciiArtMap = CmdClickMap.createMap(
                asciiArtMapCon,
                asciiArtMapSeparator
            ).toMap()
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
        setShareButton()
        val cancelButton = spannableDialogObj?.findViewById<ImageButton>(
            com.puutaro.commandclick.R.id.spannable_dialog_ok
        )
        cancelButton?.setOnClickListener {
            terminalViewModel.onDialog = false
            spannableDialogObj?.dismiss()
        }
        spannableDialogObj?.setOnCancelListener {
            terminalViewModel.onDialog = false
            spannableDialogObj?.dismiss()
        }
        spannableDialogObj?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        spannableDialogObj?.window?.setGravity(Gravity.BOTTOM)
        spannableDialogObj?.show()
        CoroutineScope(Dispatchers.IO).launch{
            val savePath = withContext(Dispatchers.IO){
                asciiArtMap.get(AsciiArtMapKey.SAVE_PATH.key)
            }
            if(
                savePath.isNullOrEmpty()
            ) return@launch
            val spannableImagePathObj =
                withContext(Dispatchers.IO) {
                    makeSpannableImageFromView()
                } ?: return@launch
            FileSystems.copyFile(
                spannableImagePathObj.absolutePath,
                savePath
            )

        }
    }

    private fun setShareButton(){
        val shareButton = spannableDialogObj?.findViewById<ImageButton>(
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
    }

    private suspend fun makeSpannableImageFromView(): File? {
        FileSystems.removeAndCreateDir(
            UsePath.cmdclickTempCreateDirPath
        )
        val spannableContents = withContext(Dispatchers.Main) {
            spannableDialogObj?.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.spannable_dialog_contents
            )
        }?: return null
        val bitmap =
            BitmapTool.getScreenShotFromView(spannableContents)
                ?: return null
        val imageName = BitmapTool.hash(
            bitmap
        ) + ".png"
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
        val beforeResizeBitMap = withContext(
            Dispatchers.IO
        ) {
            BitmapFactory.decodeFile(imagePath)
        }
        val baseWidth = ScreenSizeCalculator.dpWidth(terminalFragment)
        val resizeScale: Double =
            (baseWidth / beforeResizeBitMap.width).toDouble()
        val bitMap = Bitmap.createScaledBitmap(
            beforeResizeBitMap,
            (beforeResizeBitMap.width * resizeScale).toInt(),
            (beforeResizeBitMap.height * resizeScale).toInt(),
            true
        )
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

    private enum class AsciiArtMapKey(
        val key: String
    ){
        SAVE_PATH("savePath")
    }
}
