package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.dialog.JsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.libs.ExecJsInterfaceAdder
import java.io.File

object ListJsDialogV2Script {
    fun make(
        fannelName: String,
        title: String,
        listConByNewLineSeparated: List<String>,
        saveTag: String? = null,
        focusItemTitle: String? = null,
        searchVisible: Boolean? = null,
        searchHint: String? = null,
        defaultSearchText: String? = null,
    ): String {
        val saveTagKeyValue = saveTag?.let {
            "${ListJsDialogV2.ListJsDialogKey.SAVE_TAG.key}=${it}"
        } ?: String()
        val focusItemTitlesKeyValue = focusItemTitle?.let {
            "${ListJsDialogV2.ListJsDialogKey.FOCUS_ITEM_TITLES.key}=${it}"
        } ?: String()
        val searchVisibleKeyValue = let {
            val searchVisibleStr = when(searchVisible){
                true -> PromptWithListDialog.switchOn
                else -> PromptWithListDialog.switchOff
            }
            "${ListJsDialogV2.ListJsDialogKey.SEARCH_VISIBLE.key}=${searchVisibleStr}"
        }
        val searchHintKeyValue = searchHint?.let {
            "${ListJsDialogV2.ListJsDialogKey.SEARCH_HINT.key}=${searchHint}"
        } ?: String()
        val defaultSearchTextKeyValue = defaultSearchText?.let {
            "${ListJsDialogV2.ListJsDialogKey.DEFAULT_SEARCH_TEXT.key}=${it}"
        } ?: String()
        val listConfigCon = listOf(
            saveTagKeyValue,
            focusItemTitlesKeyValue,
            searchVisibleKeyValue,
            searchHintKeyValue,
            defaultSearchTextKeyValue,
            ).filter {
                it.trim().isNotEmpty()
        }.joinToString(String()) {
            it.trim()
        }
        val jsDialogStr = ExecJsInterfaceAdder.convertUseJsInterfaceName(
            JsDialog::class.java.simpleName
        )
        val v2List =
            listConByNewLineSeparated.joinToString(
                PromptWithListDialog.valueSeparator.toString()
            )
        return """
             ${jsDialogStr}.list(
                  "${File(UsePath.cmdclickDefaultAppDirPath, fannelName).absolutePath}",
                  "${title}",
                  `${v2List}`,
                  `${listConfigCon}`,
            );
        """.trimIndent()
    }

}