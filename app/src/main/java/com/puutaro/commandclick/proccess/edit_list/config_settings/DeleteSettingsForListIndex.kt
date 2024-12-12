package com.puutaro.commandclick.proccess.edit_list.config_settings

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.component.adapter.EditConstraintListAdapter
import com.puutaro.commandclick.proccess.edit_list.libs.ListIndexReplacer
import com.puutaro.commandclick.proccess.tool_bar_button.JsActionHandler

object DeleteSettingsForListIndex {

    enum class DeleteKey(
        val key: String
    ){
        DISABLE_DELETE_CONFIRM("disableDeleteConfirm"),
        ON_DELETE_FILE("onDeleteFile"),
        WITH_JS_ACTION("withJsAction"),
    }

    enum class OnDeleteConFileValue {
        ON
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
            DeleteKey.ON_DELETE_FILE.key
        ) == OnDeleteConFileValue.ON.name
    }

    fun doWithJsAction(
        fragment: Fragment,
        editConstraintListAdapter: EditConstraintListAdapter,
        selectedItemMap: Map<String, String>,
        listIndexListPosition: Int,
    ){
        val jsActionConSrcBeforeReplace = editConstraintListAdapter.deleteConfigMap.get(
            DeleteKey.WITH_JS_ACTION.key
        )
        val jsActionCon = ListIndexReplacer.replace(
            jsActionConSrcBeforeReplace,
            selectedItemMap,
            listIndexListPosition,
        )
        if(
            jsActionCon.isNullOrEmpty()
        ) return
        JsActionHandler.handle(
            fragment,
            editConstraintListAdapter.fannelInfoMap,
            String(),
            editConstraintListAdapter.setReplaceVariableMap,
            jsActionCon
        )
    }

}