package com.puutaro.commandclick.proccess.list_index_for_edit.libs

import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex

object ListIndexReplacer {
    fun replace(
        editFragment: EditFragment,
        jsOrAcCon: String?,
        selectedItemMap: Map<String, String>,
        listIndexListPosition: Int,
    ): String? {
//        val selectedFileNameOrPath =
//            selectedItemMap
//                .split("\t")
//                .lastOrNull()
//                ?: String()
        val selectedSRCTitle =
            selectedItemMap.get(
                ListSettingsForListIndex.MapListPathManager.Key.SRC_TITLE.key
            ) ?: String()
//                .split("\t")
//                .firstOrNull()
//                ?: String()
//        val filterDir = ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
//            editFragment,
//            ListIndexAdapter.indexListMap,
//            ListIndexAdapter.listIndexTypeKey
//        )
        val selectedFileNameOrPath = selectedItemMap.get(
            ListSettingsForListIndex.MapListPathManager.Key.SRC_TITLE.key
        ) ?: String()
        return jsOrAcCon?.replace(
            "\${ITEM_TITLE}",
            selectedSRCTitle,
        )?.replace(
            "\${ITEM_NAME}",
            selectedFileNameOrPath,
        )
//            ?.replace(
//            "\${INDEX_LIST_DIR_PATH}",
//            filterDir,
//        )
            ?.replace(
            "\${POSITION}",
            listIndexListPosition.toString()
        )
    }
}