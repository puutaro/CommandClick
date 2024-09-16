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
import java.lang.ref.WeakReference

class JsEditor(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

    @JavascriptInterface
    fun open_S(
        filePath: String,
    ){
        /*
        Edit file by editor app
        */

        val filePathObj = File(filePath)
//        val parentDirPath = filePathObj.parent
//            ?: return
        val fileName = filePathObj.name
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val context = terminalFragment.context
        EditorByIntent(
//            parentDirPath,
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
                val terminalFragment = terminalFragmentRef.get()
                    ?: return@launch
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