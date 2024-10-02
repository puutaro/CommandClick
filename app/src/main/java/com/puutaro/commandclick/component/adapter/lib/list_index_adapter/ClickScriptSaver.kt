package com.puutaro.commandclick.component.adapter.lib.list_index_adapter

import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ClickSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.ListIndexArgsMaker

object ClickScriptSaver {
    fun save(
        editFragment: EditFragment,
        listIndexArgsMaker: ListIndexArgsMaker,
    ){
        val clickConfigMap = listIndexArgsMaker.clickConfigPairList
        val enableClickSave =
            ClickSettingsForListIndex.howEnableClickSave(
                clickConfigMap
            )
        if(!enableClickSave) return
//        ScriptFileSaver.save(editFragment)
    }
}