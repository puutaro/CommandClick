package com.puutaro.commandclick.proccess.list_index_for_edit.config_settings

object CheckItemSettingsForListIndex {

    enum class CheckItemSettingKey(
        val key: String,
    ){
        ICON("icon"),
        TYPE("type"),
        COLOR("color")
    }

    enum class CheckType(
        val type: String
    ){
        LAST("last"),
        NO("no")
    }
}