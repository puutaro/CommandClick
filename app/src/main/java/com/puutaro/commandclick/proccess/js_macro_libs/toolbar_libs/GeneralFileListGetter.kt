package com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs

import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar.JsFileOrDirListGetter
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.libs.ExecJsInterfaceAdder
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionDataMapKeyObj

object GeneralFileListGetter {
    fun get(
        editFragment: EditFragment,
        jsActionMap: Map<String, String>,
        onDirectoryPick: Boolean = false,
    ){
        val argsMapCon = JsActionDataMapKeyObj.getJsMacroArgs(
            jsActionMap
        )?.map {
            "${it.key}=${it.value}"
        }?.joinToString("|")
            ?: String()
        val useClassName = ExecJsInterfaceAdder.convertUseJsInterfaceName(
            JsFileOrDirListGetter::class.java.simpleName
        )
        val jsCon = """
            ${useClassName}.get_S(
                ${onDirectoryPick},
                "${argsMapCon}",
            );
        """.trimIndent()
        ExecJsLoad.jsConLaunchHandler(
            editFragment,
            jsCon,
        )
    }
}