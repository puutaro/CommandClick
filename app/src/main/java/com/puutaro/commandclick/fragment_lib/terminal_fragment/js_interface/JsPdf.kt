package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.util.Log
import android.webkit.JavascriptInterface
import androidx.fragment.app.activityViewModels
import com.blankj.utilcode.util.ToastUtils
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.ToastErrMessage
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference


class JsPdf(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {


    @JavascriptInterface
    fun extractText(
        path: String
    ): String {
        var readCompSignal = false
        var errMessage = String()
        var extractText = String()
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                try {
                    extractText = execConvertToText(
                        path
                    )
                } catch (e: Exception){
                    errMessage = e.toString()
                    Log.e("csv", errMessage)
                }
            }
            withContext(Dispatchers.IO){
                readCompSignal = true
            }
        }
        val terminalFragment = terminalFragmentRef.get()
            ?: return String()

        runBlocking {
            for (i in 1..60){
                ToastErrMessage.launch(
                    terminalFragment,
                    errMessage
                )
                if(readCompSignal) break
                val readingMark = "extracting" +
                        ".".repeat(i)
                withContext(Dispatchers.Main) {
                    ToastUtils.showShort(readingMark)
                }
                delay(2000)
            }
        }
        return extractText
    }

    private fun execConvertToText(
        path: String
    ): String {
        val reader = PdfReader(path)
        val n: Int = reader.numberOfPages
        var previoustText = String()
        val parsedText = (0 until n).map {
            val currentText = PdfTextExtractor
                .getTextFromPage(
                    reader,
                    it + 1,
                    SimpleTextExtractionStrategy()
                ).trim()
            if(
                previoustText == currentText
            ) return@map String()
            previoustText = currentText
            currentText
        }.joinToString("\n")
        reader.close()
        return parsedText
    }
}
