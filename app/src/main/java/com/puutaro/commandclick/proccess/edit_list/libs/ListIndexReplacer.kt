package com.puutaro.commandclick.proccess.edit_list.libs

import com.puutaro.commandclick.proccess.edit_list.config_settings.ListSettingsForEditList

object ListIndexReplacer {
    fun replace(
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
                ListSettingsForEditList.MapListPathManager.Key.SRC_TITLE.key
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
            ListSettingsForEditList.MapListPathManager.Key.SRC_TITLE.key
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