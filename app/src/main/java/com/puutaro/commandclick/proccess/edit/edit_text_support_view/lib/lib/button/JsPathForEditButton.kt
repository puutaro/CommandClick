package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.button

import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.ExecJsScriptInEdit
import com.puutaro.commandclick.util.file.AssetsFileManager

object JsPathForEditButton {

//    val iconSelectBoxArgsKeySeparator = "@@@"
    val buttonIconSeparator = "\t"
    val buttonIconNameIdSeparator = "|"

    enum class JsPathMacroForEditButton {
        ICON_SELECT_BOX
    }

    enum class IconSelectBoxArgsKey(
        val key: String,
    ) {
        VAL_NAME("valName"),
        LIST_PATH("listPath"),
        INIT_LIST_PATH("initListPath"),
    }

    enum class ListPathMacroForEditButton {
        ICON_LIST
    }

    fun jsPathMacroHandler(
        editFragment: EditFragment,
        jsFilePath: String,
    ){
        val jsPathMacro = JsPathMacroForEditButton.values().find {
            it.name == jsFilePath
        } ?: return
        when(jsPathMacro){
            JsPathMacroForEditButton.ICON_SELECT_BOX -> {
                ExecJsScriptInEdit.execJsConForEdit(
                    editFragment,
                    AssetsFileManager.readFromAssets(
                        editFragment.context,
                        AssetsFileManager.iconSelectBox
                    )
                )
            }
        }
    }
}