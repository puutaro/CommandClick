package com.puutaro.commandclick.proccess.edit_list.config_settings

import com.puutaro.commandclick.proccess.edit_list.EditListConfig
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.str.QuoteTool

object ClickSettingsForListIndex {

    enum class ClickSettingKey(
        val key: String
    ){
        ON_SCRIPT_SAVE("onScriptSave"),
        MONITOR_SIZE("monitorSize"),
        ENABLE_UPDATE("enableUpdate")
    }

    enum class MonitorSize {
        SHORT,
        LONG,
    }

    enum class OnScriptSave {
        ON,
    }

    enum class OnDisableUpdateValue {
        ON,
    }

    fun makeClickConfigMap(
        listIndexConfigMap: Map<String, String>?
    ): Map<String, String> {
        return CmdClickMap.createMap(
            listIndexConfigMap?.get(EditListConfig.EditListConfigKey.LIST.key),
            '|'
        ).toMap()
    }


    fun howEnableClickUpdate(
        clickConfigMap: Map<String, String>?
    ): Boolean {
        return clickConfigMap?.get(ClickSettingKey.ENABLE_UPDATE.key)?.let {
            QuoteTool.trimBothEdgeQuote(it)
        } == OnDisableUpdateValue.ON.name
    }

    fun howEnableClickSave(
        clickConfigMap: Map<String, String>?
    ): Boolean {
        return clickConfigMap?.get(ClickSettingKey.ON_SCRIPT_SAVE.key)
            ?.let {
                QuoteTool.trimBothEdgeQuote(it)
            } == OnScriptSave.ON.name
    }

}