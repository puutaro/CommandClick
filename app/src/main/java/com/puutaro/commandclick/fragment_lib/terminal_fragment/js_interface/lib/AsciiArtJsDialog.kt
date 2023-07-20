package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib

import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.Spannable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.bachors.img2ascii.Img2Ascii
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.BitmapTool
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.Intent.IntentVarient
import com.puutaro.commandclick.util.ScreenSizeCalculator
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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

    fun create(
        title: String,
        imagePath: String
    ){
        terminalViewModel.onDialog = true
        runBlocking {
            val asciiArtSpannable =  withContext(Dispatchers.IO){
                makeAsciiArt(
                    imagePath,
                )
            }
            withContext(Dispatchers.Main) {
                execCreate(
                    title,
                    asciiArtSpannable
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
    ){
        val asciiText = TextView(context)

        asciiText.text = spannable
        val scrollView = makeScrollView()
        val linearLayout = makeLinearLayout()
        linearLayout.addView(asciiText)
        scrollView.addView(linearLayout)

        val alertDialog = android.app.AlertDialog.Builder(
            context
        )
            .setTitle(title)
            .setView(scrollView)
            .setNegativeButton("SHARE", DialogInterface.OnClickListener{
                    dialog, which ->
                terminalViewModel.onDialog = false
                FileSystems.removeAndCreateDir(
                    UsePath.cmdclickTempCreateDirPath
                )
                val bitmap = BitmapTool.getScreenShotFromView(scrollView)
                    ?: return@OnClickListener
                val imageName = BitmapTool.hash(
                    bitmap
                ) + ".png"
                val file = File(
                    UsePath.cmdclickTempCreateDirPath,
                    imageName
                )
                // ③
                FileOutputStream(file).use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                }
                IntentVarient.sharePngImage(
                    file,
                    activity
                )
            })
            .setPositiveButton("OK", DialogInterface.OnClickListener{ dialog, which ->
                terminalViewModel.onDialog = false
            })
            .show()
        alertDialog.window?.setGravity(Gravity.BOTTOM)
        alertDialog.setOnCancelListener(object : DialogInterface.OnCancelListener {
            override fun onCancel(dialog: DialogInterface?) {
                terminalViewModel.onDialog = false
            }
        })
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(
            context?.getColor(android.R.color.black) as Int
        )
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(
            context.getColor(android.R.color.black)
        )
    }

    private fun makeLinearLayout(
    ): LinearLayout {
        val linearLayout = LinearLayout(context)
        linearLayout.orientation =  LinearLayout.VERTICAL
        linearLayout.weightSum = 1F
        val linearLayoutParam = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        linearLayoutParam.marginStart = 20
        linearLayoutParam.marginEnd = 20
        linearLayout.layoutParams = linearLayoutParam
        return linearLayout
    }

    private fun makeScrollView(
    ): ScrollView {
        val scrollView = ScrollView(context)
        val linearLayoutForScrollViewParam = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        scrollView.layoutParams = linearLayoutForScrollViewParam
        return scrollView
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
}
