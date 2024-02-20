package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.editor.EditorByIntent
import java.io.File

class JsEditor(
    terminalFragment: TerminalFragment
) {
    val context = terminalFragment.context
    @JavascriptInterface
    fun open(
        filePath: String,
    ){
        val filePathObj = File(filePath)
        val parentDirPath = filePathObj.parent
            ?: return
        val fileName = filePathObj.name
        EditorByIntent(
            parentDirPath,
            fileName,
            context
        ).byIntent()
    }
}