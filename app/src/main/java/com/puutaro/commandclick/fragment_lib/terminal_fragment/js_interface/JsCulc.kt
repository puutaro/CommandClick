package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.edit.func.MathCulc
import com.puutaro.commandclick.util.LogSystems
import java.lang.ref.WeakReference

class JsCulc(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {
    @JavascriptInterface
    fun int(
        formula: String,
    ): Int {
        val defaultReturnInt = 0
        val terminalFragment = terminalFragmentRef.get()
            ?: return defaultReturnInt
        val context = terminalFragment.context
            ?: return defaultReturnInt
        val result = try {
            MathCulc.int(formula)
        } catch (e: Exception){
            LogSystems.stdErr(
                context,
                e.toString()
            )
           return defaultReturnInt
        }
        return result
    }

    @JavascriptInterface
    fun float(
        formula: String,
    ): String {
        val defaultReturnStr = "0"
        val terminalFragment = terminalFragmentRef.get()
            ?: return defaultReturnStr
        val context = terminalFragment.context
            ?: return defaultReturnStr
        val result = try {
            MathCulc.float(formula)
        } catch (e: Exception){
            LogSystems.stdErr(
                context,
                e.toString()
            )
            return defaultReturnStr
        }
        return result.toString()
    }
}
