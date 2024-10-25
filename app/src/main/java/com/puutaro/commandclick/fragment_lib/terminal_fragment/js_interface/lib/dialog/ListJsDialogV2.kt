package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog

import com.puutaro.commandclick.util.map.CmdClickMap

object ListJsDialogV2 {

    const val listJsDialogMapSeparator = ','

    fun launch(
        promptWithListDialog: PromptWithListDialog,
        fannelPath: String,
        title: String,
        listIconTsvCon: String,
        listJsDialogMapCon: String,
    ): String {
        val listJsDialogMap = CmdClickMap.createMap(
            listJsDialogMapCon,
            listJsDialogMapSeparator,
        ).toMap()
        val searchHint = listJsDialogMap.get(
            ListJsDialogKey.SEARCH_HINT.key
        ) ?: String()
        val focusItemTitles = listJsDialogMap.get(
            ListJsDialogKey.FOCUS_ITEM_TITLES.key
        ) ?: String()
        val defaultSearchText = listJsDialogMap.get(
            ListJsDialogKey.DEFAULT_SEARCH_TEXT.key
        ) ?: String()
        val searchVisible = listJsDialogMap.get(
            ListJsDialogKey.SEARCH_VISIBLE.key
        ) ?: String()
        val saveTag = listJsDialogMap.get(
            ListJsDialogKey.SAVE_TAG.key
        ) ?: String()
        val backgroundType = listJsDialogMap.get(
            ListJsDialogKey.BACKGROUND_TYPE.key
        ) ?: String()
//        val onKeyOpenMode = listJsDialogMap.get(
//            ListJsDialogKey.ON_KEY_OPEN_MODE.key
//        ) ?: String()
//        val maxLines = when(onKeyOpenMode){
//            PromptWithListDialog.switchOn -> null
//            else -> listJsDialogMap.get(
//                ListJsDialogKey.MAX_LINES.key
//            ).let {
//                try {
//                    it?.toInt()
//                } catch(e: Exception){
//                    null
//                }
//            }
//        }
        val promptConfigCon = """
                ${PromptWithListDialog.Companion.PromptWithTextMapKey.list.name}=
                    |${PromptWithListDialog.Companion.PromptListVars.saveTag.name}=${saveTag}
                    |${PromptWithListDialog.Companion.PromptListVars.concatList.name}="${listIconTsvCon}"
                    |${PromptWithListDialog.Companion.PromptListVars.focusItemTitles.name}=${focusItemTitles}
                    |${PromptWithListDialog.Companion.PromptListVars.onInsertByClick.name}=OFF
                    |${PromptWithListDialog.Companion.PromptListVars.visible.name}=ON
                    |${PromptWithListDialog.Companion.PromptListVars.onDismissByClick.name}=ON
                    |${PromptWithListDialog.Companion.PromptListVars.disableUpdate.name}=ON,
                ${PromptWithListDialog.Companion.PromptWithTextMapKey.editText.name}=
                    ${PromptWithListDialog.Companion.PromptEditTextKey.default.name}=${defaultSearchText}
                    |${PromptWithListDialog.Companion.PromptEditTextKey.hint.name}="${searchHint}"
                    |${PromptWithListDialog.Companion.PromptEditTextKey.visible.name}=${searchVisible},
                ${PromptWithListDialog.Companion.PromptWithTextMapKey.background.name}=
                    ${PromptWithListDialog.Companion.PromptBackground.Key.type.name}=${backgroundType},
            """.trimIndent().split("\n").joinToString(String()) {
            it.trim()
        }
        return promptWithListDialog.create(
            fannelPath,
            title,
            promptConfigCon
        )
    }


    enum class ListJsDialogKey(val key: String){
//        MAX_LINES(PromptWithListDialog.Companion.PromptTitleKey.maxLines.name),
        SAVE_TAG(PromptWithListDialog.Companion.PromptListVars.saveTag.name),
        FOCUS_ITEM_TITLES(PromptWithListDialog.Companion.PromptListVars.focusItemTitles.name),
        SEARCH_VISIBLE("searchVisible"),
        SEARCH_HINT("searchHint"),
        DEFAULT_SEARCH_TEXT("defaultSearchText"),
//        DISABLE_LIST_BIND_STR(PromptWithListDialog.Companion.PromptEditTextKey.disableListBind.name),
        BACKGROUND_TYPE("backgroundType"),
//        ON_KEY_OPEN_MODE(PromptWithListDialog.Companion.PromptExtraKey.onKeyOpenMode.name),
    }
}