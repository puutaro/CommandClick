package com.puutaro.commandclick.proccess.list_index_for_edit.config_settings

import com.puutaro.commandclick.component.adapter.ListIndexAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.ListIndexReplacer
import com.puutaro.commandclick.proccess.tool_bar_button.JsActionHandler

object DeleteSettingsForListIndex {

    enum class DeleteKey(
        val key: String
    ){
        DISABLE_DELETE_CONFIRM("disableDeleteConfirm"),
        ON_DELETE_CON_FILE("onDeleteConFile"),
        WITH_JS_ACTION("withJsAction"),
    }

    enum class OnDeleteConFileValue {
        OFF
    }


    enum class DisableDeleteConfirm {
        ON
    }

    fun howDisableDeleteConfirm(
        deleteConfigMap: Map<String, String>?,
    ): Boolean {
        return deleteConfigMap?.get(
            DeleteKey.DISABLE_DELETE_CONFIRM.key
        ) == DisableDeleteConfirm.ON.name
    }

    fun howOnDeleteConFileValue(
        deleteConfigMap: Map<String, String>?,
    ): Boolean {
        return deleteConfigMap?.get(
            DeleteKey.ON_DELETE_CON_FILE.key
        ) != OnDeleteConFileValue.OFF.name
    }

    fun doWithJsAction(
        editFragment: EditFragment,
        selectedItem: String,
        listIndexListPosition: Int,
    ){
        val jsActionConSrcBeforeReplace = ListIndexAdapter.deleteConfigMap.get(
            DeleteKey.WITH_JS_ACTION.key
        )
        val jsActionCon = ListIndexReplacer.replace(
            editFragment,
            jsActionConSrcBeforeReplace,
            selectedItem,
            listIndexListPosition,
        )
        if(
            jsActionCon.isNullOrEmpty()
        ) return
        JsActionHandler.handle(
            editFragment,
            editFragment.fannelInfoMap,
            String(),
            editFragment.setReplaceVariableMap,
            jsActionCon
        )
    }

}