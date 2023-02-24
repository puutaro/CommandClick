package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.webkit.JavascriptInterface
import android.widget.Toast
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ReadText
import java.io.File

class WebAppInterface(
    private val terminalFragment: TerminalFragment
) {

    @JavascriptInterface
    fun readLocalFile(path: String): String {
        val fileObj = File(path)
        if(!fileObj.isFile) return String()
        val parentDir = fileObj.parent ?: return String()
        return ReadText(
            parentDir,
            fileObj.name
        ).readText()
    }

    @JavascriptInterface
    fun writeLocalFile(path: String, contents: String) {
        val fileObj = File(path)
        val parentDir = fileObj.parent ?: return
        FileSystems.writeFile(
            parentDir,
            fileObj.name,
            contents
        )
    }


    @JavascriptInterface
    fun copyToClipboard(text: String?) {
        val clipboard: ClipboardManager? =
            terminalFragment.activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        val clip = ClipData.newPlainText("demo", text)
        clipboard?.setPrimaryClip(clip)
    }
}