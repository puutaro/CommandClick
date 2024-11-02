package com.puutaro.commandclick.custom_manager

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager

class GridNoScrollManager(
    context: Context?,
    col: Int,
    reverseLayout: Boolean = false
) : GridLayoutManager(
    context,
    col,
    LinearLayoutManager.VERTICAL,
    reverseLayout
) {
    private var isScrollEnabled = true

    fun setScrollEnabled(flag: Boolean) {
        this.isScrollEnabled = flag
    }

    override fun canScrollVertically(): Boolean {
        //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return isScrollEnabled && super.canScrollVertically()
    }
}