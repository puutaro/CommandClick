package com.puutaro.commandclick.proccess.edit_list.config_settings

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.puutaro.commandclick.custom_manager.PreLoadGridLayoutManager
import com.puutaro.commandclick.proccess.edit_list.EditListConfig
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.map.FilePrefixGetter

object LayoutSettingsForEditList {

    enum class LayoutSettingKey(
        val key: String
    ) {
//        TYPE("type"),
        COL("col"),
        ON_REVERSE_LAYOUT("onReverseLayout"),
        EDIT_BY_DRAG("editByDrag"),
        MARGIN("margin"),
        INNER_PADDING("innerPadding"),
        INNER_MARGIN("innerMargin"),
        ELEVATION("elevation"),
        RADIUS("radius"),
        ON_CLICK_UPDATE("onClickUpdate"),
//        TO_TOP("toTop")
    }

    private val switchOn = "ON"

    enum class EditByDragKey(
        val key: String,
    ){
        EDIT_BY_DRAG_DISABLE("editByDragDisable"),
    }

//    enum class LayoutTypeValueStr(
//        val valueStr: String
//    ){
//        LINEAR("linear"),
//        GRID("grid"),
//    }

    fun getLayoutConfigMap(
        editListConfigMap: Map<String, String>?,
        setReplaceVariableMap: Map<String, String>?,
    ): Map<String, String> {
        return EditListConfig.getConfigKeyMap(
            editListConfigMap,
            EditListConfig.EditListConfigKey.LAYOUT.key,
            setReplaceVariableMap,
        )
    }

    fun howReverseLayout(
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        layoutMap: Map<String, String>?,
    ): Boolean {
        return FilePrefixGetter.get(
            fannelInfoMap,
            setReplaceVariableMap,
            layoutMap,
            LayoutSettingKey.ON_REVERSE_LAYOUT.key
        ) == switchOn
    }

    fun howClickUpdate(
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        layoutMap: Map<String, String>?,
    ): Boolean {
        return FilePrefixGetter.get(
            fannelInfoMap,
            setReplaceVariableMap,
            layoutMap,
            LayoutSettingKey.ON_CLICK_UPDATE.key
        ) == switchOn
    }

    fun makeEditByDragMap(
        layoutMap: Map<String, String>?,
    ): Map<String, String> {

        return layoutMap?.get(
            LayoutSettingKey.EDIT_BY_DRAG.key
        ).let{
            CmdClickMap.createMap(
                it,
                '?'
            )
        }.toMap()
    }

    fun howDisableEditByDrag(
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
//        editFragment: EditFragment,
        editByDragMap: Map<String, String>
    ): Boolean {
        return FilePrefixGetter.get(
            fannelInfoMap,
            setReplaceVariableMap,
//            editFragment,
            editByDragMap,
            EditByDragKey.EDIT_BY_DRAG_DISABLE.key
        ) == switchOn
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
    }

//    private fun howToTop(
//        layoutConfigMap: Map<String, String>?,
//    ): Boolean {
//        return layoutConfigMap?.get(
//            LayoutSettingKey.TO_TOP.key
//        ) == switchOn
//    }

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