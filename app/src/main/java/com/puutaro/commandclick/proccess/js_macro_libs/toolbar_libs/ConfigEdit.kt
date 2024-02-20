package com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.proccess.tool_bar_button.SystemFannelLauncher

object ConfigEdit {

    fun edit(
        fragment: Fragment
    ) {
        val configDirPath = UsePath.cmdclickSystemAppDirPath
        val configShellName = UsePath.cmdclickConfigFileName
        CommandClickScriptVariable.makeConfigJsFile(
            configDirPath,
            configShellName
        )
        SystemFannelLauncher.launch(
            fragment,
            UsePath.cmdclickSystemAppDirPath,
            UsePath.cmdclickConfigFileName,
        )
    }

}