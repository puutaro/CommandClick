package com.puutaro.commandclick.proccess.list_index_for_edit.config_settings

import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig

object PerformSettingForListIndex {

    enum class PerformSettingKey(
        val key: String,
    ){
        FAST("fast")
    }

    enum class FastModeKey {
        ON
    }

//    fun makePerformMap(
//        listIndexConfigMap: Map<String, String>?,
//    ): Map<String, String> {
//        return ListIndexEditConfig.getConfigKeyMap(
//            listIndexConfigMap,
//            ListIndexEditConfig.ListIndexConfigKey.PERFORM.key
//        )
//    }

//    fun howFastMode(
//        performMap: Map<String, String>?,
//    ): Boolean {
//        return performMap?.get(
//            PerformSettingKey.FAST.key
//        ) == FastModeKey.ON.name
//    }
}