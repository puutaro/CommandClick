package com.puutaro.commandclick.proccess.lib

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat

object LinearLayoutForTotal {
    fun make(
        context: Context?
    ): LinearLayout {
        val linearLayoutForTotal = LinearLayout(context)
        linearLayoutForTotal.orientation =  LinearLayout.VERTICAL
        linearLayoutForTotal.weightSum = 1F
        val linearLayoutParamForTotal = LinearLayoutCompat.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        linearLayoutForTotal.layoutParams = linearLayoutParamForTotal
        return linearLayoutForTotal
    }
}