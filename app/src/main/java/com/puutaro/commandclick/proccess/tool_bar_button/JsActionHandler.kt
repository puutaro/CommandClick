package com.puutaro.commandclick.proccess.tool_bar_button

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.EditSettingJsTool
import com.puutaro.commandclick.proccess.tool_bar_button.libs.JsPathHandlerForToolbarButton
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.file.FileSystems
import java.io.File

object JsActionHandler {
    fun handle(
        editFragment: EditFragment,
        jsActionPairListCon: String,
    ){
        val jsActionMap = EditSettingJsTool.makeJsActionMap(
            editFragment,
            QuoteTool.replaceBySurroundedIgnore(
                jsActionPairListCon,
                ',',
                "\n"
            ),
        )
        FileSystems.writeFile(
            File(UsePath.cmdclickDefaultAppDirPath, "jsAC.txt").absolutePath,
            listOf(
                "jsActionPairListCon: ${jsActionPairListCon}",
                "jsActionMap: ${jsActionMap}"
            ).joinToString("\n\n")
        )
        if(
            jsActionMap.isNullOrEmpty()
        ) return
        JsPathHandlerForToolbarButton.handle(
            editFragment,
            null,
            jsActionMap,
        )
    }
}