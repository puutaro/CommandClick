package com.puutaro.commandclick.proccess.edit_list

import android.view.ViewGroup
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent
import com.puutaro.commandclick.util.str.PairListTool

object LayoutSetterTool {

    private val marginKey = EditComponent.Template.EditComponentKey.MARGIN.key
    private val marginTopKey = EditComponent.Template.EditComponentKey.MARGIN_TOP.key
    private val marginBottomKey = EditComponent.Template.EditComponentKey.MARGIN_BOTTOM.key
    private val marginStartKey = EditComponent.Template.EditComponentKey.MARGIN_START.key
    private val marginEndKey = EditComponent.Template.EditComponentKey.MARGIN_END.key

    fun setMargin(
        param: ViewGroup.MarginLayoutParams,
        paramMap: Map<String, String>?,
        density: Float,
    ){
        param.apply {
            val margin = paramMap?.get(marginKey)
            val marginData = EditComponent.Template.MarginData(
                paramMap?.get(marginTopKey) ?: margin,
                paramMap?.get(marginBottomKey) ?: margin,
                paramMap?.get(marginStartKey) ?: margin,
                paramMap?.get(marginEndKey) ?: margin,
                density,
            )
            marginData.marginTop?.let {
                topMargin = it
            }
            marginData.marginStart?.let {
                marginStart = it
            }
            marginData.marginEnd?.let {
                marginEnd = it
            }
            marginData.marginBottom?.let {
                bottomMargin = it
            }
        }
    }
}