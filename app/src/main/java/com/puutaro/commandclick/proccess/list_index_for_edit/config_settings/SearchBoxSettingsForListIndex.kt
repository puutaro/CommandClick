package com.puutaro.commandclick.proccess.list_index_for_edit.config_settings

object SearchBoxSettingsForListIndex {

    enum class SearchBoxSettingKey(
        val key: String,
    ) {
        HINT("hint"),
        VISIBLE("visible"),
    }

    enum class SearchBoxVisibleKey {
        OFF
    }
}