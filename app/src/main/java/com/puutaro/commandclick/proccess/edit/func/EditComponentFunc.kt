package com.puutaro.commandclick.proccess.edit.func

import com.puutaro.commandclick.component.adapter.EditConstraintListAdapter
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.EditListRecyclerViewGetter
import com.puutaro.commandclick.util.str.QuoteTool

object EditComponentFunc {

    fun getSettingValue(
        terminalFragmentSrc: TerminalFragment?,
        targetVariableName: String,
        srcFragment: String,
        editConstraintListAdapterArg: EditConstraintListAdapter?,
    ): String {
        val terminalFragment = terminalFragmentSrc
            ?: return String()
        val context = terminalFragment.context
        val editComponentListAdapter = let {
            if(
                editConstraintListAdapterArg != null
            ) return@let editConstraintListAdapterArg
            val editListRecyclerViewSrc = EditListRecyclerViewGetter.get(
                terminalFragment,
                srcFragment
            ) ?: return String()
            editListRecyclerViewSrc.adapter as EditConstraintListAdapter
        }
        return editComponentListAdapter.getCurrentSettingVals(
            context,
            targetVariableName
        )?.let {
            QuoteTool.trimBothEdgeQuote(it)
        } ?: String()
    }
}