package com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs

import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar.JsFileAdder
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.libs.ExecJsInterfaceAdder
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
        val shellPath =
            argsMap.get(
                EditSettingExtraArgsTool.ExtraKey.SHELL_PATH.key
            ) ?: String()
        val addTitleArgs =
            argsMap.get(
                AddFileExtraArgs.TITLE_ARGS.key
            ) ?: String()
        val dirPath =
            argsMap.get(
                AddFileExtraArgs.DIR_PATH.key
            ) ?: String()
        execAddItemForEdit(
            editFragment,
            shellPath,
            compPrefix,
            compSuffix,
            dirPath,
            addTitleArgs,
        )
    }

    private fun execAddItemForEdit(
        editFragment: EditFragment,
        shellPath: String,
        compPrefix: String,
        compSuffix: String,
        dirPath: String,
        addTitleArgs: String,
    ){
        val args = listOf(
            "${EditSettingExtraArgsTool.ExtraKey.SHELL_PATH.key}=${shellPath}",
            "${EditSettingExtraArgsTool.ExtraKey.COMP_PREFIX.key}=${compPrefix}",
            "${EditSettingExtraArgsTool.ExtraKey.COMP_SUFFIX.key}=${compSuffix}",
            "${AddFileExtraArgs.DIR_PATH.key}=${dirPath}",
            "${AddFileExtraArgs.TITLE_ARGS.key}=${addTitleArgs}",
        ).joinToString("|")
        val useClassName = ExecJsInterfaceAdder.convertUseJsInterfaceName(
            JsFileAdder::class.java.simpleName
        )
        val jsCon = """
            ${useClassName}.add(
                "${args}",
                "|",
            );
        """.trimIndent()
        ExecJsLoad.jsConLaunchHandler(
            editFragment,
            jsCon
        )
    }

    enum class AddFileExtraArgs(
        val key: String
    ){
        DIR_PATH("dirPath"),
        TITLE_ARGS("titleArgs")
    }

}