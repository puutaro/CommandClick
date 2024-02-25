package com.puutaro.commandclick.proccess.tool_bar_button

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionTool
import com.puutaro.commandclick.proccess.tool_bar_button.libs.JsPathHandlerForToolbarButton
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.file.FileSystems
import java.io.File

object JsActionHandler {
    fun handle(
        editFragment: EditFragment,
        jsActionPairListCon: String,
        extraRepValMap: Map<String, String>? = null,
    ){
        val jsAcKeyToSubKeyCon = QuoteTool.replaceBySurroundedIgnore(
            jsActionPairListCon,
            ',',
            "\n"
        )
        val jsActionMap = JsActionTool.makeJsActionMap(
            editFragment,
            jsAcKeyToSubKeyCon,
            extraRepValMap
        )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "jsAC.txt").absolutePath,
//            listOf(
//                "jsActionPairListCon: ${jsActionPairListCon}",
//                "jsActionMap: ${jsActionMap}"
//            ).joinToString("\n\n")
//        )
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