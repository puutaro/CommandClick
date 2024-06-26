package com.puutaro.commandclick.custom_manager

import android.content.Context
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class PreLoadLayoutManager(
    context: Context?,
    reverseLayout: Boolean = false
) :
    LinearLayoutManager(
        context,
        LinearLayoutManager.VERTICAL,
        reverseLayout
    ) {
    private val mDisplayHeight: Int


    init {
        mDisplayHeight = context?.resources?.displayMetrics?.heightPixels ?: 0
    }
    override fun calculateExtraLayoutSpace(
        state: RecyclerView.State ,
        extraLayoutSpace: IntArray
    ) {
        extraLayoutSpace[0] = mDisplayHeight * 100
        extraLayoutSpace[1] = mDisplayHeight * 100
    }
}