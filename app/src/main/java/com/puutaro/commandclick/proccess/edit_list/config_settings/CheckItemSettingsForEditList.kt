package com.puutaro.commandclick.proccess.edit_list.config_settings

object CheckItemSettingsForEditList {

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