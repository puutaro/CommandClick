package com.puutaro.commandclick.proccess.list_index_for_edit.config_settings

import com.puutaro.commandclick.util.QuoteTool

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

    fun howEnableClickUpdate(
        clickConfigPairList: List<Pair<String, String>>?
    ): Boolean {
        val enableUpdateValue = clickConfigPairList?.firstOrNull {
            val mainKey = it.first
            mainKey == ClickSettingKey.ENABLE_UPDATE.key
        }?.second.let {
            QuoteTool.trimBothEdgeQuote(it)
        }
        return enableUpdateValue == OnDisableUpdateValue.ON.name
    }

    fun howEnableClickSave(
        clickConfigPairList: List<Pair<String, String>>?
    ): Boolean {
        val onScriptSaveValue = clickConfigPairList?.firstOrNull {
            val mainKey = it.first
            mainKey == ClickSettingKey.ON_SCRIPT_SAVE.key
        }?.second.let {
            QuoteTool.trimBothEdgeQuote(it)
        }
        return onScriptSaveValue == OnScriptSave.ON.name
    }

}