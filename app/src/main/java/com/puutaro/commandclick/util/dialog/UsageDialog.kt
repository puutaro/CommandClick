package com.puutaro.commandclick.util.dialog

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.JsCcUsage
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.libs.ExecJsInterfaceAdder
import com.puutaro.commandclick.proccess.intent.ExecJsLoad

object UsageDialog {
    fun launch(
        fragment: Fragment,
    ){
        val useClassName = ExecJsInterfaceAdder.convertUseJsInterfaceName(
            JsCcUsage::class.java.simpleName
        )
        ExecJsLoad.jsConLaunchHandler(
            fragment,
            "${useClassName}.launch_S();",
        )
    }
}