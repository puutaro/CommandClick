package com.puutaro.commandclick.proccess.edit_list

import android.view.View
import android.view.ViewGroup
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent
import com.puutaro.commandclick.util.str.PairListTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object LayoutSetterTool {

    private val marginKey = EditComponent.Template.EditComponentKey.MARGIN.key
    private val marginTopKey = EditComponent.Template.EditComponentKey.MARGIN_TOP.key
    private val marginBottomKey = EditComponent.Template.EditComponentKey.MARGIN_BOTTOM.key
    private val marginStartKey = EditComponent.Template.EditComponentKey.MARGIN_START.key
    private val marginEndKey = EditComponent.Template.EditComponentKey.MARGIN_END.key
    private val marginMultiKey = EditComponent.Template.EditComponentKey.MARGIN_MULTI.key
    private val marginMultiTopKey = EditComponent.Template.EditComponentKey.MARGIN_MULTI_TOP.key
    private val marginMultiBottomKey = EditComponent.Template.EditComponentKey.MARGIN_MULTI_BOTTOM.key
    private val marginMultiStartKey = EditComponent.Template.EditComponentKey.MARGIN_MULTI_START.key
    private val marginMultiEndKey = EditComponent.Template.EditComponentKey.MARGIN_MULTI_END.key

    private val paddingKey = EditComponent.Template.EditComponentKey.PADDING.key
    private val paddingTopKey = EditComponent.Template.EditComponentKey.PADDING_TOP.key
    private val paddingBottomKey = EditComponent.Template.EditComponentKey.PADDING_BOTTOM.key
    private val paddingStartKey = EditComponent.Template.EditComponentKey.PADDING_START.key
    private val paddingEndKey = EditComponent.Template.EditComponentKey.PADDING_END.key
    private val paddingMultiKey = EditComponent.Template.EditComponentKey.PADDING_MULTI.key
    private val paddingMultiTopKey = EditComponent.Template.EditComponentKey.PADDING_MULTI_TOP.key
    private val paddingMultiBottomKey = EditComponent.Template.EditComponentKey.PADDING_MULTI_BOTTOM.key
    private val paddingMultiStartKey = EditComponent.Template.EditComponentKey.PADDING_MULTI_START.key
    private val paddingMultiEndKey = EditComponent.Template.EditComponentKey.PADDING_MULTI_END.key

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
                paramMap?.get(marginMultiKey),
                paramMap?.get(marginMultiTopKey),
                paramMap?.get(marginMultiBottomKey),
                paramMap?.get(marginMultiStartKey),
                paramMap?.get(marginMultiEndKey),
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
    
    suspend fun setPadding(
        viewGroup: ViewGroup,
        paramMap: Map<String, String>?,
        density: Float,
    ){
        viewGroup.apply {
            val paddingData = withContext(Dispatchers.IO) {
                makePaddingData(
                    paramMap,
                    density,
                )
            }
            withContext(Dispatchers.Main) {
                setPadding(
                    paddingData.paddingStart ?: paddingStart,
                    paddingData.paddingTop ?: paddingStart,
                    paddingData.paddingEnd ?: paddingEnd,
                    paddingData.paddingBottom ?: paddingBottom,
                )
            }
        }
    }

    suspend fun setPadding(
        viewGroup: View,
        paramMap: Map<String, String>?,
        density: Float,
    ){
        viewGroup.apply {
            val paddingData = withContext(Dispatchers.IO) {
                makePaddingData(
                    paramMap,
                    density,
                )
            }
            withContext(Dispatchers.Main) {
                setPadding(
                    paddingData.paddingStart ?: paddingStart,
                    paddingData.paddingTop ?: paddingStart,
                    paddingData.paddingEnd ?: paddingEnd,
                    paddingData.paddingBottom ?: paddingBottom,
                )
            }
        }
    }

    private fun makePaddingData(
        paramMap: Map<String, String>?,
        density: Float,
    ): EditComponent.Template.PaddingData {
        val padding = paramMap?.get(
            paddingKey
        )
        return EditComponent.Template.PaddingData(
            paramMap?.get(
                paddingTopKey
            ) ?: padding,
            paramMap?.get(
                paddingBottomKey
            ) ?: padding,
            paramMap?.get(
                paddingStartKey
            ) ?: padding,
            paramMap?.get(
                paddingEndKey
            )?: padding,
            paramMap?.get(paddingMultiKey),
            paramMap?.get(paddingMultiTopKey),
            paramMap?.get(paddingMultiBottomKey),
            paramMap?.get(paddingMultiStartKey),
            paramMap?.get(paddingMultiEndKey),
            density,
        )
    }


}