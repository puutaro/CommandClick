package com.puutaro.commandclick.proccess.lib

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout

object NestLinearLayout {
    fun make(
        context: Context?,
        weight: Float
    ): LinearLayout {
        val nestLinearLayout = LinearLayout(context)
        nestLinearLayout.orientation =  LinearLayout.VERTICAL
        val linearLayoutParamForListView = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
        linearLayoutParamForListView.weight = weight
        nestLinearLayout.layoutParams = linearLayoutParamForListView
        return nestLinearLayout
    }
}