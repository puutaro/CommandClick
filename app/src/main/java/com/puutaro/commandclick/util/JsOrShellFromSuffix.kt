package com.puutaro.commandclick.util

import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.LanguageTypeSelects

object JsOrShellFromSuffix {
    fun judge(
        shellScriptName: String
    ):LanguageTypeSelects {
        if(
            shellScriptName.endsWith(
                CommandClickShellScript.SHELL_FILE_SUFFIX
            )
        ) return LanguageTypeSelects.SHELL_SCRIPT
        return  LanguageTypeSelects.JAVA_SCRIPT
    }

}