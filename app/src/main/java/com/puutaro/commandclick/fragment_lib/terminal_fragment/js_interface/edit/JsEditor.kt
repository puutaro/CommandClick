package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit

import android.webkit.JavascriptInterface
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.editor.EditorByEditText
import com.puutaro.commandclick.util.editor.EditorByIntent
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.FannelInfoTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class JsEditor(
    private val terminalFragment: TerminalFragment
) {
    val context = terminalFragment.context

    @JavascriptInterface
    fun open_S(
        filePath: String,
    ){
        /*
        Edit file by editor app
        */

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

    @JavascriptInterface
    fun byEditText(
        textPath: String,
    ){

        /*
        Edit file by edit text
        */

        val textPathObj = File(textPath)
        val parentDirPath = textPathObj.parent
            ?: return
        val fileName = textPathObj.name
        val editCon = ReadText(
            textPath
        ).readText()
        CoroutineScope(Dispatchers.Main).launch {
            try {
                EditorByEditText.byEditText(
                    terminalFragment,
                    parentDirPath,
                    fileName,
                    editCon,
                    null
                )
            } catch (e: Exception) {
                ToastUtils.showLong(e.toString())
            }
        }
    }
}