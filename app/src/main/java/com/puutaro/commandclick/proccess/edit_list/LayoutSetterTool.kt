package com.puutaro.commandclick.proccess.edit_list

import android.view.ViewGroup
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent
import com.puutaro.commandclick.util.str.PairListTool

object LayoutSetterTool {

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
            val marginData = EditComponent.Template.MarginData(
                paramMap?.get(marginTopKey),
                paramMap?.get(marginBottomKey),
                paramMap?.get(marginStartKey),
                paramMap?.get(marginEndKey),
                density,
            )
            topMargin = marginData.marginTop ?: 0
            marginStart = marginData.marginStart ?: 0
            marginEnd = marginData.marginEnd ?: 0
            bottomMargin = marginData.marginBottom ?: 0
        }
    }
}