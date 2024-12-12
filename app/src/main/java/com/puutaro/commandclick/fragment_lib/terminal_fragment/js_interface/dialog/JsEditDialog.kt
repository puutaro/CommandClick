package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.dialog

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class JsEditDialog(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

    @JavascriptInterface
    fun show(
        fannelInfoCon: String,
        editListConfigPath: String
    ){
        val terminalFragment =
            terminalFragmentRef.get()
                ?: return
        terminalFragment
            .editListDialogForOrdinaryRevolver
            ?.show(
                fannelInfoCon,
                editListConfigPath,
            )
    }

    @JavascriptInterface
    fun dismiss(){
        val terminalFragment =
            terminalFragmentRef.get()
                ?: return
        CoroutineScope(Dispatchers.Main).launch {
            terminalFragment
                .editListDialogForOrdinaryRevolver
                ?.dismissForRevolver()
        }

    }
}