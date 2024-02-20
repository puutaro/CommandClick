package com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs

import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.qr.JsQrGetter
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.libs.ExecJsInterfaceAdder
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.EditSettingExtraArgsTool
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionDataMapKeyObj
import com.puutaro.commandclick.proccess.intent.ExecJsLoad

object QrConGetterDialog {

    fun launch(
        editFragment: EditFragment,
        jsActionMap: Map<String, String>,
    ){
        val stockDirPathKeyForTsv =
            EditSettingExtraArgsTool.ExtraKey.PARENT_DIR_PATH.key
        val compPrefixKey =
            EditSettingExtraArgsTool.ExtraKey.COMP_PREFIX.key
        val compSuffixKey =
            EditSettingExtraArgsTool.ExtraKey.COMP_SUFFIX.key
        val argsMap = JsActionDataMapKeyObj.getJsMacroArgs(
                jsActionMap,
            ) ?: emptyMap()
        val stockConDirPathForTsv = argsMap.get(
            stockDirPathKeyForTsv
        )
        val compPrefix = argsMap.get(
            compPrefixKey
        ) ?: String()
        val compSuffix = argsMap.get(
            compSuffixKey
        ) ?: String()
        val useClassName = ExecJsInterfaceAdder.convertUseJsInterfaceName(
            JsQrGetter::class.java.simpleName
        )
        ExecJsLoad.jsConLaunchHandler(
            editFragment,
              """
                  ${useClassName}.get_S(
                    "${stockConDirPathForTsv}",
                    "${compPrefix}",
                    "${compSuffix}",
                  );
              """.trimIndent()
        )
    }
}