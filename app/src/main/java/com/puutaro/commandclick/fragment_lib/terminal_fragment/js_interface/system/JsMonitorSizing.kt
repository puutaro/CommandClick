package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.webkit.JavascriptInterface
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.state.FannelInfoTool
import java.lang.ref.WeakReference

class JsMonitorSizing(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

    @JavascriptInterface
    fun change(){
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val context = terminalFragment.context
        val linearLayoutParam =
            terminalFragment.view?.layoutParams as? LinearLayoutCompat.LayoutParams
                ?: return
        if(
            linearLayoutParam.weight == 0f
        ) return
        val listener = context as? TerminalFragment.OnMonitorSizeChangingForTerm
            ?: return
        listener.onMonitorSizeChangingForTerm(
            terminalFragment.fannelInfoMap
        )
    }
}