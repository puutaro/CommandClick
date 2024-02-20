package com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs

import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.EditSettingExtraArgsTool
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionDataMapKeyObj
import com.puutaro.commandclick.proccess.intent.ExecJsLoad

object AddFileForEdit {

    fun add(
        editFragment: EditFragment,
        jsActionMap: Map<String, String>,
    ){
        val argsMap = JsActionDataMapKeyObj.getJsMacroArgs(
            jsActionMap
        ) ?: emptyMap()
        val compPrefix =
            argsMap.get(
                EditSettingExtraArgsTool.ExtraKey.COMP_PREFIX.key
            ) ?: String()
        val compSuffix =
            argsMap.get(
                EditSettingExtraArgsTool.ExtraKey.COMP_SUFFIX.key
            ) ?: String()
        execAddItemForEdit(
            editFragment,
            compPrefix,
            compSuffix,
        )
    }

    private fun execAddItemForEdit(
        editFragment: EditFragment,
        compPrefix: String,
        compSuffix: String
    ){
        val jsCon = """
            jsFileAdder.add(
                "${compPrefix}",
                "${compSuffix}",
            );
        """.trimIndent()
        ExecJsLoad.jsConLaunchHandler(
            editFragment,
            jsCon
        )
    }
}