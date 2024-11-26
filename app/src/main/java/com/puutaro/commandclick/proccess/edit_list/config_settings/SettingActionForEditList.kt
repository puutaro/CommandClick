package com.puutaro.commandclick.proccess.edit_list.config_settings

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionManager
import com.puutaro.commandclick.proccess.edit_list.EditListConfig

object SettingActionForEditList {

    fun getSettingConfigCon(
        editListConfigMap: Map<String, String>?,
    ): String? {
        return editListConfigMap?.get(
            EditListConfig.EditListConfigKey.SETTING_ACTION.key,
        )
    }
}