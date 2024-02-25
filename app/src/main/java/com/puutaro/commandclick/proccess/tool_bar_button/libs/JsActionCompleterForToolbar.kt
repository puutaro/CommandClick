package com.puutaro.commandclick.proccess.tool_bar_button.libs

import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionTool
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.ToolbarButtonArgsMaker
import com.puutaro.commandclick.proccess.tool_bar_button.SettingButtonConfigMapKey

object JsActionCompleterForToolbar {

    fun makeJsActionMap(
        editFragment: EditFragment,
        toolbarButtonConfigMap: Map<String, String>,
        toolbarButtonBariantForEdit: ToolbarButtonBariantForEdit,
        isLongClick: Boolean,
    ): Map<String, String> {
        return when(
            isLongClick
        ){
            true -> execMakeJsActionMap(
                editFragment,
                toolbarButtonConfigMap,
                SettingButtonConfigMapKey.LONG_CLICK.key,
                ToolbarButtonArgsMaker.defaultLongClickMacroMap.get(
                    toolbarButtonBariantForEdit
                ) ?: String(),
            )
            else -> execMakeJsActionMap(
                editFragment,
                toolbarButtonConfigMap,
                SettingButtonConfigMapKey.CLICK.key,
                ToolbarButtonArgsMaker.defaultClickMacroMap.get(
                    toolbarButtonBariantForEdit
                ) ?: String(),
            )
        }
    }

    private fun execMakeJsActionMap(
        editFragment: EditFragment,
        toolbarButtonConfigMap: Map<String, String>,
        buttonClickMapKey: String,
        defaultButtonMacroStr: String,
    ): Map<String, String> {

        val jsActionMapSrc = JsActionTool.makeJsActionMap(
            editFragment,
            toolbarButtonConfigMap.get(buttonClickMapKey),
        )
//        FileSystems.writeFile(
//            File(
//                UsePath.cmdclickDefaultAppDirPath,
//                "jsConOrMacroStr_execMakeJsPathMacro.txt").absolutePath,
//            listOf(
//                "jsActionMapSrc: ${jsActionMapSrc}",
//                "compJsActionMacro: ${ EditSettingJsTool.compJsActionMacro(
//                    jsActionMapSrc,
//                    defaultButtonMacroStr,
//                )}",
//            ).joinToString("\n\n")
//        )
        return JsActionTool.compJsActionMacro(
            jsActionMapSrc,
            defaultButtonMacroStr,
        )
    }
}