package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.file.FileSystems
import java.io.File

object TitleJsDialog {

    fun launch(
        promptWithListDialog: PromptWithListDialog,
        title: String,
    ) {
        val dummyFannelPath = File(
            UsePath.cmdclickDefaultAppDirPath,
            "_systemTitle.js"
        )
        if(!dummyFannelPath.isFile){
            FileSystems.writeFile(
                dummyFannelPath.absolutePath,
                String()
            )
        }
//        val listJsDialogMap = CmdClickMap.createMap(
//            listJsDialogMapCon,
//            listJsDialogMapSeparator,
//        ).toMap()
//        val searchHint = listJsDialogMap.get(
//            ListJsDialogKey.SEARCH_HINT.key
//        ) ?: String()
//        val focusItemTitles = listJsDialogMap.get(
//            ListJsDialogKey.FOCUS_ITEM_TITLES.key
//        ) ?: String()
//        val defaultSearchText = listJsDialogMap.get(
//            ListJsDialogKey.DEFAULT_SEARCH_TEXT.key
//        ) ?: String()
//        val searchVisible = listJsDialogMap.get(
//            ListJsDialogKey.SEARCH_VISIBLE.key
//        ) ?: String()
//        val saveTag = listJsDialogMap.get(
//            ListJsDialogKey.SAVE_TAG.key
//        ) ?: String()
//        val backgroundType = listJsDialogMap.get(
//            ListJsDialogKey.BACKGROUND_TYPE.key
//        ) ?: String()
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
                    |${PromptWithListDialog.Companion.PromptListVars.visible.name}=OFF,
                ${PromptWithListDialog.Companion.PromptWithTextMapKey.editText.name}=
                    |${PromptWithListDialog.Companion.PromptEditTextKey.visible.name}=OFF,
            """.trimIndent().split("\n").joinToString(String()) {
            it.trim()
        }
        promptWithListDialog.create(
            dummyFannelPath.absolutePath,
            title,
            promptConfigCon
        )
    }


//    enum class ListJsDialogKey(val key: String){
//        //        MAX_LINES(PromptWithListDialog.Companion.PromptTitleKey.maxLines.name),
//        SAVE_TAG(PromptWithListDialog.Companion.PromptListVars.saveTag.name),
//        FOCUS_ITEM_TITLES(PromptWithListDialog.Companion.PromptListVars.focusItemTitles.name),
//        SEARCH_VISIBLE("searchVisible"),
//        SEARCH_HINT("searchHint"),
//        DEFAULT_SEARCH_TEXT("defaultSearchText"),
////        DISABLE_LIST_BIND_STR(PromptWithListDialog.Companion.PromptEditTextKey.disableListBind.name),
////        BACKGROUND_TYPE("backgroundType"),
////        ON_KEY_OPEN_MODE(PromptWithListDialog.Companion.PromptExtraKey.onKeyOpenMode.name),
//    }
}