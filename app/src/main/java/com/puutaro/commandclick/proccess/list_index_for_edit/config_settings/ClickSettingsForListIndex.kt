package com.puutaro.commandclick.proccess.list_index_for_edit.config_settings

import com.puutaro.commandclick.proccess.extra_args.ExtraArgsTool

object ClickSettingsForListIndex {

    enum class ClickSettingKey(
        val key: String
    ){
        JS_PATH("jsPath"),
        MENU_PATH("menuPath"),
        ON_SCRIPT_SAVE("onScriptSave"),
        MONITOR_SIZE("monitorSize"),
        EXTRA(ExtraArgsTool.extraSettingKeyName),
    }

    enum class MonitorSize {
        SHORT,
        LONG,
    }

    enum class OnScriptSave {
        ON,
        OFF
    }

}