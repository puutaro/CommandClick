package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.LogTool

class JsToast(
    fragment: Fragment
) {
    val context = fragment.context

    @JavascriptInterface
    fun short(
        contents: String,
    ) {
        Toast.makeText(
            context,
            contents,
            Toast.LENGTH_SHORT
        ).show()
    }

    @JavascriptInterface
    fun errLog(
        contents: String
    ){
        if(
            LogTool.howEscapeErrMessage(contents)
        ) return
        Toast.makeText(
            context,
            contents,
            Toast.LENGTH_SHORT
        ).show()
    }

    @JavascriptInterface
    fun long(
        contents: String,
    ) {
        Toast.makeText(
            context,
            contents,
            Toast.LENGTH_LONG
        ).show()
    }
}