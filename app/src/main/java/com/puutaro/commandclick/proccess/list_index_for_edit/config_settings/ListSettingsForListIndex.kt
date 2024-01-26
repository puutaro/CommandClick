package com.puutaro.commandclick.proccess.list_index_for_edit.config_settings

object ListSettingsForListIndex {
    enum class ListSettingKey(
        val key: String
    ) {
        LIST_DIR("listDir"),
        PREFIX("prefix"),
        SUFFIX("suffix"),
        FILTER_SHELL_PATH("filterShellPath"),
    }
}