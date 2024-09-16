package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.Keyboard
import com.puutaro.commandclick.util.state.FannelInfoTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference


class JsKeyboard(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

    @JavascriptInterface
    fun show(){
        val focusWebView = getFocusWebView()
        val isFocus = focusWebView?.requestFocus() == true
        if (!isFocus) return
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val context = terminalFragment.context
        CoroutineScope(Dispatchers.Main).launch {
            Keyboard.showKeyboard(
                context,
//                activity,
                focusWebView,
            )
        }
//        val imm =
//            activity?.getSystemService(Context.INPUT_METHOD_SERVICE)
//                    as? InputMethodManager
//        imm?.showSoftInput(focusWebView,0)
    }

    private fun getFocusWebView(
    ): WebView? {
        val terminalFragment = terminalFragmentRef.get()
            ?: return null
        return when(terminalFragment.view?.hasWindowFocus()){
            false -> {
                terminalFragment.pocketWebViewManager?.pocketWebView
            }
            else -> terminalFragment.binding.terminalWebView
        }
//        val webViewDialog = terminalFragment.webViewDialogInstance
//        val focusPocketWebView = terminalFragment.pocketWebViewManager.pocketWebView
////            terminalFragment.webViewDialogInstance?.findViewById<WebView>(
////            R.id.webview_dialog_webview
////        )
//        if(
////            webViewDialog != null
////            && webViewDialog.isShowing
//            focusPocketWebView != null
//            && focusPocketWebView.isVisible
//        ) {
//            return focusPocketWebView
//        }
//        return terminalFragment.binding.terminalWebView
    }
}