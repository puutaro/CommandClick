package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.Html
import android.text.Spanned
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.BitmapTool
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.Intent.IntentVarient
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

    fun create(
        title: String,
        imageSrcFilePath: String
    ){
        if(
            !File(imageSrcFilePath).isFile
        ) {
            Toast.makeText(
                context,
                "no image file\n ${imageSrcFilePath}",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        terminalViewModel.onDialog = true
        runBlocking {
            withContext(Dispatchers.Main) {
                execCreate(
                    title,
                    imageSrcFilePath
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
        imageSrcFilePath: String
    ){
        val imageView = ImageView(context)
        imageView.setPadding(10, 20, 20, 10)
        val myBitmap: Bitmap = BitmapFactory.decodeFile(
            imageSrcFilePath
        )
        imageView.setImageBitmap(myBitmap)
        val scrollView = makeScrollView()
        val linearLayout = makeLinearLayout()
        linearLayout.addView(imageView)
        scrollView.addView(linearLayout)

        val alertDialog = android.app.AlertDialog.Builder(
            context
        )
            .setTitle(title)
            .setView(scrollView)
            .setNegativeButton("SHARE", DialogInterface.OnClickListener{
                    dialog, which ->
                terminalViewModel.onDialog = false
                FileSystems.removeDir(
                    UsePath.cmdclickTempCreateDirPath
                )
                FileSystems.createDirs(
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
            .create()
        alertDialog.show()
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
            context.getColor(android.R.color.black) as Int
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
}