package com.puutaro.commandclick.proccess.list_index_for_edit.config_settings


object DeleteSettingsForListIndex {

    enum class DeleteKey(
        val key: String
    ){
        DISABLE_DELETE_CONFIRM("disableDeleteConfirm"),
        ON_DELETE_CON_FILE("onDeleteConFile"),
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

}