package com.puutaro.commandclick.fragment_lib.command_index_fragment.variable

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat

object ToolbarWidgetWeightForLinearLayout {
    private val buttonWideWeightNum = 0.2F
//        private val buttonWideWeightForPageSearchNum = 0.1F
    private val buttonShurinkWeightNum = 0F


//        private val searchEditTextWideWeightNum = 0.9F
//        private val searchEditTextShurinkWeightNum = 0.5F
//        private val searchEditTextShurinkWeightNumForPageSearch = 0F
//
//        private val searchEditTextWideWeightNumForPageSearch = 0.6F

    val buttonWideWeight = returnLayoutPram(buttonWideWeightNum)
    val buttonShrinkWeight = returnLayoutPram(buttonShurinkWeightNum)
//        val searchEditTextWideWeight = returnLayoutPram(searchEditTextWideWeightNum)
//        val searchEditTextShrinkWeight = returnLayoutPram(searchEditTextShurinkWeightNum)
//
//        val buttonWideWeightForPageSearch = returnLayoutPram(
//            buttonWideWeightForPageSearchNum
//        )
//        val searchEditTextWideWeightForPageSearch = returnLayoutPram(
//            searchEditTextWideWeightNumForPageSearch
//        )
//        val searchEditTextShrinkWeightForPageSearch = returnLayoutPram(
//            searchEditTextShurinkWeightNumForPageSearch
//        )



    private fun returnLayoutPram(
        weight: Float
    ): LinearLayoutCompat.LayoutParams {
        val linearLayoutParam = LinearLayoutCompat.LayoutParams(
            0,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        linearLayoutParam.weight = weight
        return linearLayoutParam
    }
}