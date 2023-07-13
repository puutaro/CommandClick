package com.puutaro.commandclick.util


import com.puutaro.commandclick.common.variable.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.UsePath

object JsOrShellFromSuffix {
    fun judge(
        shellScriptName: String
    ):LanguageTypeSelects {
        if(
            shellScriptName.endsWith(
                UsePath.SHELL_FILE_SUFFIX
            )
        ) return LanguageTypeSelects.SHELL_SCRIPT
        return  LanguageTypeSelects.JAVA_SCRIPT
    }

}