package com.puutaro.commandclick.fragment_lib.command_index_fragment.variable

import android.view.ViewGroup
import android.widget.LinearLayout

class CmdIndexLinearWeightParam(
) {
    companion object {
        private val listViewLongWeightNum = 0.6F
        private val listViewShortWeightNum = 0.07F


        val listViewLongWeight = returnLayoutPram(listViewLongWeightNum)
        val listViewShortWeight = returnLayoutPram(listViewShortWeightNum)


        private fun returnLayoutPram(
            weight: Float
        ): LinearLayout.LayoutParams {
            val linearLayoutParam = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0
            )
            linearLayoutParam.weight = weight
            return linearLayoutParam
        }
    }
}