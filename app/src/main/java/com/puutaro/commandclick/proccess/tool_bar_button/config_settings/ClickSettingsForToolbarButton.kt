package com.puutaro.commandclick.proccess.tool_bar_button.config_settings

object ClickSettingsForToolbarButton {
    enum class ClickConfigMapKey(
        val key: String
    ){
        ON_SAVE("onSave"),
        MONITOR_SIZE("monitorSize"),
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