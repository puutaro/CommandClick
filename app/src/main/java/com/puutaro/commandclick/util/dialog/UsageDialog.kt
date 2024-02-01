package com.puutaro.commandclick.util.dialog

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.WebUrlVariables
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.util.file.UrlFileSystems

object UsageDialog {
    fun launch(
        fragment: Fragment,
        currentAppDirPath: String,
    ){
        val webSearcherName = UrlFileSystems.Companion.FirstCreateFannels.WebSearcher.str +
                UsePath.JS_FILE_SUFFIX
        ExecJsLoad.execExternalJs(
            fragment,
            currentAppDirPath,
            webSearcherName,
            listOf(WebUrlVariables.commandClickUsageUrl),
        )
    }
}