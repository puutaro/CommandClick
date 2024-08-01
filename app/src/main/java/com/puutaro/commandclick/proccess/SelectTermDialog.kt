package com.puutaro.commandclick.proccess

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsSelectMonitor
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.libs.ExecJsInterfaceAdder
import com.puutaro.commandclick.proccess.intent.ExecJsLoad

object SelectTermDialog {

    fun launch(
        currentFragment: Fragment
    ) {
        val useClassName = ExecJsInterfaceAdder.convertUseJsInterfaceName(
            JsSelectMonitor::class.java.simpleName
        )
        ExecJsLoad.jsConLaunchHandler(
            currentFragment,
            "${useClassName}.launch();",
        )
    }
}