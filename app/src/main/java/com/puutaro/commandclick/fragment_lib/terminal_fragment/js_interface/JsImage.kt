package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.Spannable
import android.webkit.JavascriptInterface
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.fragment.app.activityViewModels
import com.bachors.img2ascii.Img2Ascii
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.ScreenSizeCalculator
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


class JsImage(
    private val terminalFragment: TerminalFragment
) {
    val context = terminalFragment.context
    val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()

    @JavascriptInterface
    fun toAsciiHtml(
        imagePath: String,
    ): String {
        var htmlSpannableStr = String()
        runBlocking {
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
                            terminalViewModel.tempSpannable = text
                            htmlSpannableStr = HtmlCompat.toHtml(
                                text,
                                HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE
                            )
                        }
                    })
            }
            withContext(Dispatchers.IO){
                for(i in 1..50){
                    delay(100)
                    if(
                        htmlSpannableStr.isNotEmpty()
                    ) break
                }
            }
        }
        return htmlSpannableStr
    }

}

private fun displayProgress(
    context: Context?,
    i: Int
){
    if(
        i % 20 != 0
    ) return
//    withContext(Dispatchers.Main) {
        Toast.makeText(
            context,
            "#".repeat(i / 20 + 1),
            Toast.LENGTH_SHORT
        ).show()
//    }
}
