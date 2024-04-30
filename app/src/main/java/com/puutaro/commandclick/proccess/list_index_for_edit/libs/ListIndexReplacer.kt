package com.puutaro.commandclick.proccess.list_index_for_edit.libs

import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex

object ListIndexReplacer {
    fun replace(
        editFragment: EditFragment,
        jsOrAcCon: String?,
        selectedItem: String,
        listIndexListPosition: Int,
    ): String? {
        val selectedFileNameOrPath =
            selectedItem
                .split("\t")
                .lastOrNull()
                ?: String()
        val selectedTitle =
            selectedItem
                .split("\t")
                .firstOrNull()
                ?: String()
        val filterDir = ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
            editFragment,
            ListIndexForEditAdapter.indexListMap,
            ListIndexForEditAdapter.listIndexTypeKey
        )
        return jsOrAcCon?.replace(
            "\${ITEM_TITLE}",
            selectedTitle,
        )?.replace(
            "\${ITEM_NAME}",
            selectedFileNameOrPath,
        )?.replace(
            "\${INDEX_LIST_DIR_PATH}",
            filterDir,
        )?.replace(
            "\${POSITION}",
            listIndexListPosition.toString()
        )
    }
}