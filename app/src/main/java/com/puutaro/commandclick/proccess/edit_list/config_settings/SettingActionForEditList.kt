package com.puutaro.commandclick.proccess.edit_list.config_settings

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionManager
import com.puutaro.commandclick.proccess.edit_list.EditListConfig

object SettingActionForEditList {

    fun getSettingConfigMap(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMapSrc: Map<String, String>?,
        editListConfigMap: Map<String, String>?,
    ): List<Pair<String, String>>? {
        return editListConfigMap?.get(
            EditListConfig.EditListConfigKey.SETTING_ACTION.key,
        ).let {
            keyToSubKeyCon ->
            SettingActionManager.makeSettingActionKeyToSubKeyList(
                fragment,
                fannelInfoMap,
                keyToSubKeyCon,
                setReplaceVariableMapSrc,
            )
        }
    }
}