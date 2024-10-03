package com.puutaro.commandclick.proccess.list_index_for_edit.config_settings

import android.content.Context
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.custom_manager.PreLoadGridLayoutManager
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig

object LayoutSettingsForListIndex {

    enum class LayoutSettingKey(
        val key: String
    ) {
//        TYPE("type"),
        COL("col"),
        TO_TOP("toTop")
    }

    private val switchOn = "ON"

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
        val isToTop = howToTop(layoutConfigMap)
        val height = when(isToTop){
            true -> 0
            else -> ViewGroup.LayoutParams.WRAP_CONTENT
        }
        val constraintLayoutParam = ConstraintLayout.LayoutParams(
            0,
            height
        )
        if(
            isToTop
        ) {
            constraintLayoutParam.topToBottom = R.id.editTextView
        }
        constraintLayoutParam.startToStart = ConstraintSet.PARENT_ID
        constraintLayoutParam.endToEnd = ConstraintSet.PARENT_ID
        constraintLayoutParam.bottomToTop = R.id.edit_list_search_edit_text
        editListRecyclerView.layoutParams = constraintLayoutParam

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

    private fun howToTop(
        layoutConfigMap: Map<String, String>?,
    ): Boolean {
        return layoutConfigMap?.get(
            LayoutSettingKey.TO_TOP.key
        ) == switchOn
    }

    private fun decideColNum(
        layoutConfigMap: Map<String, String>?,
    ): Int {
        val defaultColNum = 1
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