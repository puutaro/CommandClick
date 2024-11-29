package com.puutaro.commandclick.proccess.edit.func

import com.puutaro.commandclick.component.adapter.EditComponentListAdapter
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.EditListRecyclerViewGetter
import com.puutaro.commandclick.util.str.QuoteTool

object EditComponentFunc {

    fun getSettingValue(
        terminalFragmentSrc: TerminalFragment?,
        targetVariableName: String,
        srcFragment: String,
        editComponentListAdapterArg: EditComponentListAdapter?,
    ): String {
        val terminalFragment = terminalFragmentSrc
            ?: return String()
        val context = terminalFragment.context
        val editComponentListAdapter = let {
            if(
                editComponentListAdapterArg != null
            ) return@let editComponentListAdapterArg
            val editListRecyclerViewSrc = EditListRecyclerViewGetter.get(
                terminalFragment,
                srcFragment
            ) ?: return String()
            editListRecyclerViewSrc.adapter as EditComponentListAdapter
        }
        return editComponentListAdapter.getCurrentSettingVals(
            context,
            targetVariableName
        )?.let {
            QuoteTool.trimBothEdgeQuote(it)
        } ?: String()
    }
}