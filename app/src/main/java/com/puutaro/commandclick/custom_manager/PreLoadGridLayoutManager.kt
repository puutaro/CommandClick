package com.puutaro.commandclick.custom_manager

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PreLoadGridLayoutManager(
    context: Context?,
    col: Int,
    reverseLayout: Boolean = false
) :
    GridLayoutManager(
        context,
        col,
        LinearLayoutManager.VERTICAL,
        reverseLayout
    ) {
    private val mDisplayHeight: Int


    init {
        mDisplayHeight = context?.resources?.displayMetrics?.heightPixels ?: 0
    }
    override fun calculateExtraLayoutSpace(
        state: RecyclerView.State,
        extraLayoutSpace: IntArray
    ) {
        extraLayoutSpace[0] = mDisplayHeight * 100
        extraLayoutSpace[1] = mDisplayHeight * 100
    }
}