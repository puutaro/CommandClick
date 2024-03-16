package com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsSettingValFrag
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.libs.ExecJsInterfaceAdder
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionDataMapKeyObj

object ChangeSettingFannel {
    fun change(
        fragment: Fragment,
        jsActionMap: Map<String, String>?
    ) {
        val argsMap = JsActionDataMapKeyObj.getJsMacroArgs(
            jsActionMap,
        ) ?: emptyMap()
        val currentState = argsMap.values.firstOrNull() ?: String()
        val useClassName = ExecJsInterfaceAdder.convertUseJsInterfaceName(
            JsSettingValFrag::class.java.simpleName
        )
        ExecJsLoad.jsConLaunchHandler(
            fragment,
            "${useClassName}.change_S(\"${currentState}\");",
        )
    }
}