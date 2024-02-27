package com.puutaro.commandclick.proccess.list_index_for_edit.config_settings

object FileNameKeyForListIndex {

    enum class ListIndexFileNameKey(
        val key: String,
    ) {
        TYPE("type"),
        ON_HIDE("onHide"),
        REMOVE_EXTEND("removeExtend"),
        COMP_PREFIX("compPrefix"),
        COMP_SUFFIX("compSuffix"),
        SHELL_PATH("shellPath"),
        LENGTH("length"),
    }
}