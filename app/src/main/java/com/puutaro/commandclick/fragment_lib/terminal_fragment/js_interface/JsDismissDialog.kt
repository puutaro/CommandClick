package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.app.Dialog
import android.webkit.JavascriptInterface
import androidx.fragment.app.Fragment

class DismissDialog(
    fragment: Fragment
){
    @JavascriptInterface
    fun dismiss(
        dialog: Dialog?,
    ){
        dialog?.dismiss()
    }
}