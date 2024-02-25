package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib

import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.intent.lib.JavascriptExecuter
import com.puutaro.commandclick.util.EnableTerminalWebView
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.file.ReadText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object ExecJsScriptInEdit {
    fun exec(
        editFragment: EditFragment,
        jsFilePath: String,
    ){
        if(
            !File(jsFilePath).isFile
        ) {
            LogSystems.stdErr("js not found: ${jsFilePath}")
            return
        }
        if(
            jsFilePath.isEmpty()
        ) {
            LogSystems.stdWarn("js blank: ${jsFilePath}")
            return
        }
        JavascriptExecuter.jsOrActionHandler(
            editFragment,
            jsFilePath,
            ReadText(jsFilePath).textToList(),
        )
    }

    fun execJsConForEdit(
        editFragment: EditFragment,
        jsConSrc: String,
    ){
        val jsCon = JavaScriptLoadUrl.makeFromContents(
            jsConSrc.split("\n")
        )
        execJsUrlForEdit(
            editFragment,
            jsCon
        )
    }

    private fun execJsUrlForEdit(
        editFragment: EditFragment,
        jsCon: String?
    ){
        if(
            jsCon.isNullOrEmpty()
        ) return
        editFragment.jsExecuteJob?.cancel()
        editFragment.jsExecuteJob = CoroutineScope(Dispatchers.IO).launch {
            val onLaunchUrl = EnableTerminalWebView.check(
                editFragment,
                editFragment.context?.getString(
                    R.string.edit_terminal_fragment
                )
            )
            if(!onLaunchUrl) return@launch
            withContext(Dispatchers.Main) {
                val listenerForWebLaunch =
                    editFragment.context as? EditFragment.OnLaunchUrlByWebViewForEditListener
                listenerForWebLaunch?.onLaunchUrlByWebViewForEdit(
                    jsCon
                )
            }
        }
    }
}