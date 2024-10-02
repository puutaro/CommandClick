package com.puutaro.commandclick.proccess.list_index_for_edit.config_settings

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.puutaro.commandclick.custom_manager.PreLoadGridLayoutManager
import com.puutaro.commandclick.custom_manager.PreLoadLayoutManager
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig

object LayoutSettingsForListIndex {

    enum class LayoutSettingKey(
        val key: String
    ) {
//        TYPE("type"),
        COL("col"),
    }

//    enum class LayoutTypeValueStr(
//        val valueStr: String
//    ){
//        LINEAR("linear"),
//        GRID("grid"),
//    }

    fun getLayoutConfigMap(
        listIndexConfigMap: Map<String, String>?,
    ): Map<String, String> {
        return ListIndexEditConfig.getConfigKeyMap(
            listIndexConfigMap,
            ListIndexEditConfig.ListIndexConfigKey.LAYOUT.key
        )
    }



//    fun decideLayoutType(
//        layoutConfigMap: Map<String, String>
//    ): LayoutTypeValueStr {
//        val defaultLayoutType =
//            LayoutTypeValueStr.LINEAR
//        val layoutTypeStr =
//            layoutConfigMap.get(LayoutSettingKey.TYPE.key)
//        return LayoutTypeValueStr.values().firstOrNull {
//            it.valueStr == layoutTypeStr
//        } ?: defaultLayoutType
//    }

    fun setLayout(
        context: Context?,
        layoutConfigMap: Map<String, String>,
        editListRecyclerView: RecyclerView,
        isReverseLayout: Boolean,
    ){
        editListRecyclerView.layoutManager = PreLoadGridLayoutManager(
            context,
            decideColNum(layoutConfigMap),
            isReverseLayout
        )
//        val layoutType = decideLayoutType(layoutConfigMap)
//        when(layoutType){
//            LayoutTypeValueStr.LINEAR ->
//                editListRecyclerView.layoutManager = PreLoadLayoutManager(
//                    context,
//                    isReverseLayout,
//                )
//            LayoutTypeValueStr.GRID ->
//                editListRecyclerView.layoutManager = PreLoadGridLayoutManager(
//                    context,
//                    decideColNum(layoutConfigMap),
//                    isReverseLayout
//                )
//        }
    }

    private fun decideColNum(
        layoutConfigMap: Map<String, String>?,
    ): Int {
        val defaultColNum = 2
        val colNum = layoutConfigMap?.get(
            LayoutSettingKey.COL.key
        ) ?: return defaultColNum
        return try {
            colNum.toInt()
        } catch (e: Exception){
            defaultColNum
        }
    }
}