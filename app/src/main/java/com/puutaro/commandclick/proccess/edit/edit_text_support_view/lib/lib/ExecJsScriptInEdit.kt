package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib

import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.EnableTerminalWebView
import com.puutaro.commandclick.util.JavaScriptLoadUrl
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
        ) return
        if(
            jsFilePath.isEmpty()
        ) return
        val context = editFragment.context
        editFragment.jsExecuteJob?.cancel()
        editFragment.jsExecuteJob = CoroutineScope(Dispatchers.IO).launch {
            val onLaunchUrl = EnableTerminalWebView.check(
                editFragment,
                editFragment.context?.getString(
                    R.string.edit_execute_terminal_fragment
                )
            )
            if(!onLaunchUrl) return@launch
            withContext(Dispatchers.Main) {
                val listenerForWebLaunch =
                    editFragment.context as? EditFragment.OnLaunchUrlByWebViewForEditListener
                listenerForWebLaunch?.onLaunchUrlByWebViewForEdit(
                    JavaScriptLoadUrl.make(
                        context,
                        jsFilePath,
                    ).toString()
                )
            }
        }
    }
}