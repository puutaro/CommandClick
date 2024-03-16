package com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs

import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar.JsFileOrDirGetter
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.libs.ExecJsInterfaceAdder
import com.puutaro.commandclick.proccess.intent.ExecJsLoad

object GeneralFileGetter {
    fun get(
        editFragment: EditFragment,
        onDirectoryPick: Boolean = false,
    ) {
        val useClassName = ExecJsInterfaceAdder.convertUseJsInterfaceName(
            JsFileOrDirGetter::class.java.simpleName
        )
        ExecJsLoad.jsConLaunchHandler(
            editFragment,
            "${useClassName}.get_S(${onDirectoryPick});",
        )
    }
}
