package com.puutaro.commandclick.proccess.edit_list.config_settings

import com.puutaro.commandclick.proccess.edit_list.EditListConfig

object ImageActionForConfigCon {
    fun getImageConfigCon(
        editListConfigMap: Map<String, String>?,
    ): String? {
        return editListConfigMap?.get(
            EditListConfig.EditListConfigKey.IMAGE_ACTION.key,
        )
    }
}