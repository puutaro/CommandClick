package com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment_lib.command_index_fragment.init.CmdClickSystemFannelManager
import com.puutaro.commandclick.proccess.tool_bar_button.SystemFannelLauncher

object ConfigEdit {

    fun edit(
        fragment: Fragment
    ) {
        val context = fragment.context
        CmdClickSystemFannelManager.createConfigFannel(context)
        SystemFannelLauncher.launch(
            fragment,
            UsePath.cmdclickSystemAppDirPath,
            UsePath.cmdclickConfigFileName,
        )
    }

}