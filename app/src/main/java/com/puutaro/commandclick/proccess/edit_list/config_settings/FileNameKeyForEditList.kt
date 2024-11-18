package com.puutaro.commandclick.proccess.edit_list.config_settings

object FileNameKeyForEditList {

    enum class EditListFileNameKey(
        val key: String,
    ) {
        ON_HIDE("onHide"),
        REMOVE_EXTEND("removeExtend"),
        COMP_PREFIX("compPrefix"),
        COMP_SUFFIX("compSuffix"),
        SHELL_PATH("shellPath"),
        LENGTH("length"),
    }
}