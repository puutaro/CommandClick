package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import android.os.Build
import android.util.DisplayMetrics
import android.view.ViewGroup
import android.widget.LinearLayout
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.WithIndexListView
import com.puutaro.commandclick.util.TargetFragmentInstance

object ListIndexSizingToKeyboard {
    fun handle(
        editFragment: EditFragment,
        isOpen: Boolean
    ){
        if(
            !editFragment.existIndexList
        ) return
        val activity = editFragment.activity
        val context = editFragment.context
        val binding = editFragment.binding
        val indexListLinearLayoutTagName = editFragment.indexListLinearLayoutTagName
        TargetFragmentInstance().getFromFragment<TerminalFragment>(
            activity,
            context?.getString(R.string.edit_execute_terminal_fragment)
        ) ?: return
        val listIndexInnerLinearLayout =
            binding.editListLinearLayout.findViewWithTag<LinearLayout>(
                indexListLinearLayoutTagName
            )
        val pxHeight = PxHeightCalculateForIndexList.culc(
            editFragment,
            editFragment.terminalOn,
            isOpen
        )
        val linearLayoutParamForTotal = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            pxHeight
        )
        listIndexInnerLinearLayout.layoutParams = linearLayoutParamForTotal
    }
}

private object PxHeightCalculateForIndexList {
    fun culc(
        editFragment: EditFragment,
        terminalOn: String,
        isOpen: Boolean
    ): Int
    {
        val defaultPxHeight = 300
        val pxHeight = if (
            Build.VERSION.SDK_INT > 30
        ) {
            val windowMetrics =
                editFragment.activity?.windowManager?.currentWindowMetrics
                    ?: return defaultPxHeight
            windowMetrics.bounds.height()
        } else {
            val display = editFragment.activity?.windowManager?.getDefaultDisplay()
            val outMetrics = DisplayMetrics()
            display?.getMetrics(outMetrics)
            outMetrics.heightPixels
        }
        if(isOpen){
            return (pxHeight * WithIndexListView.pxHeightOnKeyboard) / 100
        }
        val heightRate = if (
            terminalOn
            != SettingVariableSelects.Companion.TerminalDoSelects.OFF.name
        ) WithIndexListView.pxHeightOnTerminal
        else WithIndexListView.pxHeightNoTerminal
        return (pxHeight * heightRate) / 100
    }
}